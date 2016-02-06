package com.teamged.txserver.database;

import com.teamged.ServerConstants;
import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.generated.CommandType;
import com.teamged.logging.xmlelements.generated.SystemEventType;
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
    private static final Object lock = new Object();
    private static final ConcurrentHashMap<String, UserDatabaseObject> dbInterfaces = new ConcurrentHashMap<>();

    public static String dbOperation(TransactionObject tx) {
        String opResult;
        if (tx.getUserCommand().equals(UserCommand.DUMPLOG_ROOT)) {
            opResult = "DUMPLOG_ROOT";
            try {
                Logger.getInstance().SaveLog();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        } else {

            UserDatabaseObject dbProxy = getDBProxy(tx.getUserName());
            if (dbProxy != null) {

                // Log the database connection as a system event
                SystemEventType systemEvent = new SystemEventType();

                systemEvent.setTimestamp(System.currentTimeMillis());
                systemEvent.setServer(ServerConstants.TX_SERVERS[0]);
                systemEvent.setTransactionNum(BigInteger.valueOf(tx.getSequenceNumber()));
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
                        opResult = dbProxy.add(tx.getAmountDollars(), tx.getAmountCents());
                        break;
                    case QUOTE:
                        opResult = dbProxy.quote(tx.getStockSymbol());
                        break;
                    case BUY:
                        opResult = dbProxy.buy(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                        break;
                    case COMMIT_BUY:
                        opResult = dbProxy.commitBuy();
                        break;
                    case CANCEL_BUY:
                        opResult = dbProxy.cancelBuy();
                        break;
                    case SELL:
                        opResult = dbProxy.sell(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                        break;
                    case COMMIT_SELL:
                        opResult = dbProxy.commitSell();
                        break;
                    case CANCEL_SELL:
                        opResult = dbProxy.cancelSell();
                        break;
                    case SET_BUY_AMOUNT:
                        opResult = dbProxy.setBuyAmount(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                        break;
                    case CANCEL_SET_BUY:
                        opResult = dbProxy.cancelSetBuy(tx.getStockSymbol());
                        break;
                    case SET_BUY_TRIGGER:
                        opResult = dbProxy.setBuyTrigger(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                        break;
                    case SET_SELL_AMOUNT:
                        opResult = dbProxy.setSellAmount(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                        break;
                    case SET_SELL_TRIGGER:
                        opResult = dbProxy.setSellTrigger(tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                        break;
                    case CANCEL_SET_SELL:
                        opResult = dbProxy.cancelSetSell(tx.getStockSymbol());
                        break;
                    case DUMPLOG:
                        opResult = dbProxy.dumplog(tx.getFileName());
                        break;
                    case DISPLAY_SUMMARY:
                        opResult = dbProxy.displaySummary();
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
