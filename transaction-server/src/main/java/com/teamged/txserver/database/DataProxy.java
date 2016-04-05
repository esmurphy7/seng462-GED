package com.teamged.txserver.database;

import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.TransactionCompleteType;
import com.teamged.txserver.InternalLog;
import com.teamged.txserver.transactions.TransactionObject;
import com.teamged.txserver.transactions.TransactionResponse;
import com.teamged.txserver.transactions.UserCommand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.concurrent.*;

/**
 * Created by DanielF on 2016-02-02.
 */
public class DataProxy {
    private static final ScheduledExecutorService timingLogger = Executors.newSingleThreadScheduledExecutor();
    private static final String OUTPUT_FILE = "command_timings";
    private static final ConcurrentLinkedQueue<String> timingQueue = new ConcurrentLinkedQueue<>();
    private static final ConcurrentHashMap<String, UserDatabaseObject> dbInterfaces = new ConcurrentHashMap<>();

    static {
        timingLogger.scheduleWithFixedDelay(
                (Runnable) () -> {
                    System.out.println("Writing command timings to file...");
                    URL url = DataProxy.class.getResource("");
                    File outfile = new File(url.getPath() + OUTPUT_FILE);
                    try (FileWriter fw = new FileWriter(outfile, true)) {
                        String nextTiming;
                        while ((nextTiming = timingQueue.poll()) != null) {
                            fw.write(nextTiming);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Done writing command timings to file.");
                },
                30,
                10,
                TimeUnit.SECONDS
        );
    }

    public static String dbOperation(TransactionObject tx) {
        TransactionResponse opResult = new TransactionResponse();
        opResult.setUsername(tx.getUserName());
        try {
            if (tx.getErrorString().isEmpty()) {
                InternalLog.Log(tx.getUserName() + " - " + tx.getUserSeqNum() + " - " + tx.getWorkloadSeqNum() + " - " + tx.getUserCommand().toString());
                if (tx.getUserCommand().equals(UserCommand.DUMPLOG_ROOT)) {
                    // TODO: Add the dumplog op result?
                    Logger.getInstance().DumpLog(tx.getWorkloadSeqNum());
                    System.out.println("RECEIVED DUMPLOG_ROOT COMMAND!");
                } else {
                    UserDatabaseObject dbProxy = getDBProxy(tx.getUserName());
                    if (dbProxy != null) {
                        long startTime = System.nanoTime();
                        switch (tx.getUserCommand()) {
                            case NO_COMMAND:
                                opResult.setErrorMsg("NO_COMMAND");
                                break;
                            case ADD:
                                opResult = dbProxy.add(tx.getAmountDollars(), tx.getAmountCents(), tx.getWorkloadSeqNum());
                                break;
                            case QUOTE:
                                QuoteObject qo = dbProxy.quote(tx.getStockSymbol(), tx.getWorkloadSeqNum());
                                opResult.setStock(qo.getStockSymbol());
                                opResult.setStockDollars(qo.getDollars());
                                opResult.setStockCents(qo.getCents());
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
                                opResult.setErrorMsg("Unknown command \"" + tx.getUserCommand().toString() + "\" was given");
                                break;
                        }
                        long commTime = System.nanoTime() - startTime;
                        String timeResult = tx.getUserCommand().name() + ":" + commTime;
                        timingQueue.add(timeResult);

                        // Save the user database object to our database
                        dbProxy.getDatabasePersister().submit(() -> PersistentDatabase.saveUserDatabaseObject(dbProxy, tx.getWorkloadSeqNum()));
                    } else {
                        opResult.setErrorMsg("ERROR," + tx.toString());
                    }
                }
            } else {
                opResult.setErrorMsg(tx.getErrorString());
            }
        } catch (Exception e) {
            System.out.println("[ERROR CAUGHT IN DATA PROXY] " + e.getMessage());
        }

        TransactionCompleteType tcType = new TransactionCompleteType();
        tcType.setTransactionNum(BigInteger.valueOf(tx.getWorkloadSeqNum()));
        Logger.getInstance().Log(tcType);

        // TODO: Some additional processing and parsing of the result will be needed.
        return opResult.toString();
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
