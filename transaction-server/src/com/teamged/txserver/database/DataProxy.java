package com.teamged.txserver.database;

import com.teamged.ServerConstants;
import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.generated.CommandType;
import com.teamged.logging.xmlelements.generated.SystemEventType;
import com.teamged.txserver.InternalLog;
import com.teamged.txserver.transactions.TransactionObject;
import com.teamged.txserver.transactions.UserCommand;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by DanielF on 2016-02-02.
 */
public class DataProxy {
    private static final ConcurrentHashMap<String, UserDatabaseObject> dbInterfaces = new ConcurrentHashMap<>();

    public static String dbOperation(TransactionObject tx) {
        InternalLog.Log(tx.getUserName() + " - " + tx.getUserSeqNum() + " - " + tx.getWorkloadSeqNum() + " - " + tx.getUserCommand().toString());
        String opResult;
        if (tx.getUserCommand().equals(UserCommand.DUMPLOG_ROOT)) {
            opResult = "DUMPLOG_ROOT";
            System.out.println("RECEIVED DUMPLOG_ROOT COMMAND!");
        } else {

            UserDatabaseObject dbProxy = getDBProxy(tx.getUserName());
            if (dbProxy != null) {

                // Log the database connection as a system event
                SystemEventType systemEvent = new SystemEventType();

                systemEvent.setTimestamp(System.currentTimeMillis());
                systemEvent.setServer(ServerConstants.TX_SERVERS[0]);
                systemEvent.setTransactionNum(BigInteger.valueOf(tx.getWorkloadSeqNum()));
                systemEvent.setUsername(tx.getUserName());
                systemEvent.setCommand(CommandType.fromValue(tx.getUserCommand().name()));
                systemEvent.setStockSymbol(tx.getStockSymbol());
                systemEvent.setFilename(tx.getFileName());

                Logger.getInstance().Log(systemEvent);

                switch (tx.getUserCommand()) {
                    case NO_COMMAND:
                        opResult = "NO_COMMAND";
                        break;
                    case ADD:
                        opResult = dbProxy.add(tx.getAmountDollars(), tx.getAmountCents(), tx.getWorkloadSeqNum());
                        break;
                    case QUOTE:
                        opResult = dbProxy.quote(tx.getStockSymbol(), tx.getWorkloadSeqNum()).toString();
                        break;
                    case BUY:
                        opResult = dbProxy.buy(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents(), tx.getWorkloadSeqNum());
                        break;
                    case COMMIT_BUY:
                        opResult = dbProxy.commitBuy(tx.getWorkloadSeqNum());
                        break;
                    case CANCEL_BUY:
                        opResult = dbProxy.cancelBuy(tx.getWorkloadSeqNum());
                        break;
                    case SELL:
                        opResult = dbProxy.sell(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents(), tx.getWorkloadSeqNum());
                        break;
                    case COMMIT_SELL:
                        opResult = dbProxy.commitSell(tx.getWorkloadSeqNum());
                        break;
                    case CANCEL_SELL:
                        opResult = dbProxy.cancelSell(tx.getWorkloadSeqNum());
                        break;
                    case SET_BUY_AMOUNT:
                        opResult = dbProxy.setBuyAmount(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents(), tx.getWorkloadSeqNum());
                        break;
                    case CANCEL_SET_BUY:
                        opResult = dbProxy.cancelSetBuy(tx.getStockSymbol(), tx.getWorkloadSeqNum());
                        break;
                    case SET_BUY_TRIGGER:
                        opResult = dbProxy.setBuyTrigger(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents(), tx.getWorkloadSeqNum());
                        break;
                    case SET_SELL_AMOUNT:
                        opResult = dbProxy.setSellAmount(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents(), tx.getWorkloadSeqNum());
                        break;
                    case SET_SELL_TRIGGER:
                        opResult = dbProxy.setSellTrigger(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents(), tx.getWorkloadSeqNum());
                        break;
                    case CANCEL_SET_SELL:
                        opResult = dbProxy.cancelSetSell(tx.getStockSymbol(), tx.getWorkloadSeqNum());
                        break;
                    case DUMPLOG:
                        opResult = dbProxy.dumplog(tx.getFileName(), tx.getWorkloadSeqNum());
                        break;
                    case DISPLAY_SUMMARY:
                        opResult = dbProxy.displaySummary(tx.getWorkloadSeqNum());
                        break;
                    case DUMPLOG_ROOT:
                    default:
                        opResult = "Unknown command \"" + tx.getUserCommand().toString() + "\" was given";
                        break;
                }

            } else {
                opResult = "ERROR," + tx.toString();
            }
        }

        // TODO: This is a hideous HACK!!
        /*if (tx.getWorkloadSeqNum() == 99) {
            System.out.println("[SYS DUMP] DUMPING!");
            try {
                Logger.getInstance().SaveLog();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }*/

        // TODO: Some additional processing and parsing of the result will be needed.
        return "[DATA PROXY RESULT]" + opResult;
    }

    private static String buyOperation(String name, String stock, int dollars, int cents) {
        String resp = "BUY was given for " + name + ", for $" + dollars + "." + cents + " of " + stock;
        try (
                Socket quoteSocket = new Socket(ServerConstants.QUOTE_SERVER, ServerConstants.QUOTE_PORT);
                PrintWriter out = new PrintWriter(quoteSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
        ) {
            out.println(name + "," + stock);
            resp += ": " + in.readLine();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            resp += ". But quote server was not found";
        } catch (IOException e) {
            e.printStackTrace();
            resp += ". But quote server was uncommunicative";
        }

        return resp;
    }

    private static UserDatabaseObject getDBProxy(String name) {
        UserDatabaseObject dbProxy = dbInterfaces.get(name);
        if (dbProxy == null) {
            dbInterfaces.putIfAbsent(name, new UserDatabaseObject(name));
            dbProxy = dbInterfaces.get(name);
        }

        return dbProxy;
    }
}
