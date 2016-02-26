package com.teamged.txserver.database;

import com.teamged.ServerConstants;
import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.generated.*;
import com.teamged.txserver.InternalLog;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by DanielF on 2016-02-02.
 */
public class UserDatabaseObject {
    private static final int CENT_CAP = 100;

    private final ScheduledExecutorService triggerScheduler;
    private final Object lock = new Object();
    private final List<String> history = new ArrayList<>();
    private final Deque<StockRequest> sellList = new ArrayDeque<>();
    private final Deque<StockRequest> buyList = new ArrayDeque<>();
    private final Deque<StockTrigger> buyTriggers = new ArrayDeque<>();
    private final Deque<StockTrigger> sellTriggers = new ArrayDeque<>();
    private final Map<String, Integer> stocksOwned = new HashMap<>();
    private final Map<String, QuoteObject> stockCache = new HashMap<>();
    private final String userName;
    private StockRequest sellAmount = null;
    private StockRequest buyAmount = null;
    private int dollars = 0;
    private int cents = 0;

    public UserDatabaseObject(String user) {
        userName = user;
        triggerScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public String add(int dollars, int cents, int tid) {
        synchronized (lock) {
            if (dollars < 0) {
                ErrorEventType eeType = new ErrorEventType();
                eeType.setTimestamp(Calendar.getInstance().getTimeInMillis());
                eeType.setFunds(BigDecimal.valueOf((long)dollars * CENT_CAP + cents, 2));
                eeType.setServer(ServerConstants.TX_SERVERS[0]);
                eeType.setTransactionNum(BigInteger.valueOf(tid));
                eeType.setCommand(CommandType.ADD);
                eeType.setUsername(userName);
                eeType.setErrorMessage("Attempted to add negative dollars");
                Logger.getInstance().Log(eeType);
            } else {
                this.dollars += dollars;
                this.cents += cents;
                if (this.cents >= CENT_CAP) {
                    this.cents -= CENT_CAP;
                    this.dollars++;
                }
                logAddFunds(dollars, cents, tid);
                history.add("ADD," + userName + "," + dollars + "." + cents);
                // TODO: Update database
            }
        }

        return userName + ", " + this.dollars + "." + this.cents;
    }

    /**
     * Gets a raw quote string from the request server.
     *
     * @param stock
     * @return
     */
    public QuoteObject quote(String stock, int tid) {
        QuoteObject quote;
        synchronized (lock) {
            long currTime = Calendar.getInstance().getTimeInMillis();
            if ((quote = stockCache.get(stock)) != null) {
                if (!quote.getErrorString().isEmpty()) {
                    // Log error
                    quote = null;
                } else {
                    if (currTime >= quote.getQuoteTimeout()) {
                        // Log timed out quote
                        stockCache.remove(stock);
                        quote = null;
                    }
                }
            }

            if (quote == null) {
                InternalLog.Log("[DEBUG PRINT] FETCHING QUOTE");
                try (
                        Socket quoteSocket = new Socket(ServerConstants.QUOTE_SERVER, ServerConstants.QUOTE_PORT);
                        PrintWriter out = new PrintWriter(quoteSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
                ) {
                    String quoteString;
                    out.println(stock + "," + userName);
                    quoteString = in.readLine();
                    quote = QuoteObject.fromQuote(quoteString);

                    if (!quote.getErrorString().isEmpty()) {
                        // Log error
                    } else {
                        stockCache.put(stock, quote);
                        try {
                            //String[] qp = quote.split(",");
                            QuoteServerType qtype = new QuoteServerType();
                            qtype.setTimestamp(currTime);
                            qtype.setQuoteServerTime(BigInteger.valueOf(quote.getQuoteTime()));
                            qtype.setServer(ServerConstants.TX_SERVERS[0]);
                            qtype.setTransactionNum(BigInteger.valueOf(tid));
                            qtype.setPrice(quote.getPrice());
                            qtype.setStockSymbol(quote.getStockSymbol());
                            qtype.setUsername(quote.getUserName());
                            qtype.setCryptokey(quote.getCryptoKey());

                            Logger.getInstance().Log(qtype);
                        } catch (Exception e) {
                            e.printStackTrace();
                            InternalLog.Log("Error logging");
                        }

                        history.add("QUOTE," + userName + "," + stock);
                    }
                } catch(UnknownHostException e) {
                    e.printStackTrace();
                    quote = QuoteObject.fromQuote("QUOTE ERROR");
                } catch(IOException e) {
                    e.printStackTrace();
                    quote = QuoteObject.fromQuote("QUOTE ERROR");
                }
            }
        }

        return quote;
    }

    public String buy(String stock, int dollars, int cents, int tid) {
        String buyRes;
        synchronized (lock) {
            if (this.dollars > dollars || (this.dollars == dollars && this.cents >= cents)) {
                QuoteObject quote = quote(stock, tid);

                try {
                    if (quote.getErrorString().isEmpty()) {
                        long stockPrice = (long) quote.getDollars() * CENT_CAP + quote.getCents();
                        long stockPurchaseMoney = (long) dollars * CENT_CAP + cents;
                        int stockCount = (int) (stockPurchaseMoney / stockPrice);
                        long remainingMoney = stockPurchaseMoney % stockPrice;
                        long spentMoney = stockPurchaseMoney - remainingMoney;

                        StockRequest sr = new StockRequest(
                                stock,
                                stockCount,
                                (int) (spentMoney / CENT_CAP),
                                (int) (spentMoney % CENT_CAP),
                                Calendar.getInstance().getTimeInMillis());
                        buyList.push(sr);

                        logRemoveFunds(sr.getDollars(), sr.getCents(), tid);
                        this.dollars -= sr.getDollars();
                        this.cents -= sr.getCents();
                        if (this.cents < 0) {
                            this.dollars--;
                            this.cents += CENT_CAP;
                        }

                        // TODO: Start a timer to expire the stored buy request in 60 seconds
                        history.add("BUY," + userName + "," + stock + "," + dollars + "." + cents);
                        buyRes = quote.toString();
                    } else {
                        buyRes = "BUY ERROR," + userName + "," + this.dollars + "." + this.cents + "," + quote.toString();
                    }
                } catch (Exception e) {
                    buyRes = "BUY ERROR";
                    System.out.println(quote.toString());
                    System.out.println(quote.getErrorString());
                    System.out.println();
                }
            } else {
                buyRes = "BUY ERROR," + userName + "," + this.dollars + "." + this.cents;
            }
        }

        return buyRes;
    }

    public String commitBuy(int tid) {
        String commitRes;
        synchronized (lock) {
            if (!buyList.isEmpty()) {
                StockRequest buyReq = buyList.remove();
                // TODO: Confirm time has not expired on this buy request

                String stock = buyReq.getStock();
                int stockAmt = buyReq.getShares();
                if (stocksOwned.containsKey(stock)) {
                    stockAmt += stocksOwned.get(stock);
                }

                stocksOwned.put(stock, stockAmt);
                history.add("COMMIT_BUY," + userName);

                commitRes = "COMMIT_BUY," + userName + "," + stock + "," + buyReq.getDollars() + "." + buyReq.getCents();
            } else {
                commitRes = "COMMIT_BUY ERROR";
            }
        }

        return commitRes;
    }

    public String cancelBuy(int tid) {
        String cancelRes;
        synchronized (lock) {
            if (!buyList.isEmpty()) {
                StockRequest buyReq = buyList.remove();

                logAddFunds(buyReq.getDollars(), buyReq.getCents(), tid);
                this.dollars += buyReq.getDollars();
                this.cents += buyReq.getCents();
                if (this.cents >= CENT_CAP) {
                    this.cents -= CENT_CAP;
                    this.dollars++;
                }

                history.add("CANCEL_BUY," + userName);
                cancelRes = "CANCEL_BUY," + userName + "," + buyReq.getStock() + "," + this.dollars + "." + this.cents;
            } else {
                cancelRes = "CANCEL_BUY ERROR";
            }
        }

        return cancelRes;
    }

    public String sell(String stock, int dollars, int cents, int tid) {
        String sellRes;
        synchronized (lock) {
            QuoteObject quote = quote(stock, tid);

            if (quote.getErrorString().isEmpty()) {
                long sellMoney = (long) dollars * CENT_CAP + cents;

                // Gets the current value of the stock on the market
                long stockPrice = (long) quote.getDollars() * CENT_CAP + quote.getCents();

                // Gets the current value of the owned stocks of this type
                int ownedCount = 0;
                if (stocksOwned.containsKey(stock)) {
                    ownedCount = stocksOwned.get(stock);
                }
                long ownedValue = stockPrice * ownedCount;

                // Gets the value of the rounded stocks to sell
                int actualSellCount = (int) (sellMoney / stockPrice);
                long actualValue = actualSellCount * stockPrice;

                // Removes the number of stocks as would be sold by this command
                if (ownedValue >= actualValue) {
                    StockRequest sr = new StockRequest(
                            stock,
                            actualSellCount,
                            (int) (actualValue / CENT_CAP),
                            (int) (actualValue % CENT_CAP),
                            Calendar.getInstance().getTimeInMillis()
                    );

                    sellList.push(sr);
                    stocksOwned.put(stock, ownedCount - actualSellCount);
                    // TODO: Start a timer to expire the stored buy request in 60 seconds
                    history.add("SELL," + userName + "," + stock + "," + dollars + "." + cents);
                    sellRes = quote.toString();
                } else {
                    sellRes = "SELL ERROR," + userName + "," + stock + "," + ownedCount + "," + quote.toString();
                }
            } else {
                sellRes = "SELL ERROR," + userName + "," + this.dollars + "." + this.cents + "," + quote;
            }
        }

        return sellRes;
    }

    public String commitSell(int tid) {
        String commitRes;
        synchronized (lock) {
            if (!sellList.isEmpty()) {
                StockRequest sellReq = sellList.remove();
                // TODO: Confirm time has not expired on this sell request

                // Releases the money into the account
                logAddFunds(sellReq.getDollars(), sellReq.getCents(), tid);
                this.dollars += sellReq.getDollars();
                this.cents += sellReq.getCents();
                if (this.cents >= CENT_CAP) {
                    this.cents -= CENT_CAP;
                    this.dollars++;
                }

                history.add("COMMIT_SELL," + userName);

                commitRes = "COMMIT_SELL," + userName + "," + sellReq.getStock() + "," + sellReq.getDollars() + "." + sellReq.getCents();
            } else {
                commitRes = "COMMIT_SELL ERROR";
            }
        }

        return commitRes;
    }

    public String cancelSell(int tid) {
        String cancelRes;
        synchronized (lock) {
            if (!sellList.isEmpty()) {
                StockRequest sellReq = sellList.remove();
                String stock = sellReq.getStock();

                // Returns stocks to holdings
                int stockCount = sellReq.getShares();
                if (stocksOwned.containsKey(stock)) {
                    stockCount = stocksOwned.get(stock);
                }

                stocksOwned.put(stock, stockCount);

                history.add("CANCEL_SELL," + userName);
                cancelRes = "CANCEL_SELL," + userName + "," + stock + "," + this.dollars + "." + this.cents;
            } else {
                cancelRes = "CANCEL_SELL ERROR";
            }
        }

        return cancelRes;
    }

    public String setBuyAmount(String stock, int dollars, int cents, int tid) {
        String setRes;
        synchronized (lock) {
            // TODO: Confirm the business logic that should be followed if a previous set buy is present
            if (buyAmount == null && (this.dollars > dollars || (this.dollars == dollars && this.cents >= cents))) {
                logRemoveFunds(dollars, cents, tid);
                this.dollars -= dollars;
                this.cents -= cents;
                if (this.cents < 0) {
                    this.cents += CENT_CAP;
                    this.dollars--;
                }
                buyAmount = new StockRequest(stock, 0, dollars, cents, 0);

                history.add("SET_BUY_AMOUNT," + userName + "," + stock + "," + dollars + "." + cents);
                setRes = "SET_BUY_AMOUNT," + userName + "," + stock + "," + dollars + "." + cents;
            } else {
                setRes = "SET_BUY_AMOUNT ERROR," + userName + "," + this.dollars + "." + this.cents;
            }
        }

        return setRes;
    }

    public String cancelSetBuy(String stock, int tid) {
        String cancelSet;
        synchronized (lock) {
            StockRequest buyReq = null;
            history.add("CANCEL_SET_BUY," + userName + "," + stock);

            if (buyAmount != null && buyAmount.getStock().equals(stock)) {
                buyReq = buyAmount;
                buyAmount = null;
            } else {
                Iterator<StockTrigger> iter = buyTriggers.descendingIterator();
                while (iter.hasNext()) {
                    StockTrigger trigger = iter.next();
                    if (trigger.getSetAmount().getStock().equals(stock)) {
                        buyTriggers.remove(trigger);
                        buyReq = trigger.getSetAmount();
                        trigger.cancelTrigger(); // Notify the trigger executor that this trigger is cancelled.
                        break;
                    }
                }
            }

            if (buyReq != null) {
                logAddFunds(buyReq.getDollars(), buyReq.getCents(), tid);
                this.dollars += buyReq.getDollars();
                this.cents += buyReq.getCents();
                if (this.cents >= CENT_CAP) {
                    this.cents -= CENT_CAP;
                    this.dollars++;
                }

                cancelSet = "CANCEL_SET_BUY," + userName + "," + stock + "," + this.dollars + "." + this.cents;
            } else {
                cancelSet = "CANCEL_SET_BUY ERROR";
            }
        }

        return cancelSet;
    }

    public String setBuyTrigger(String stock, int dollars, int cents, int tid) {
        String triggerSet;
        synchronized (lock) {
            if (buyAmount != null && buyAmount.getStock().equals(stock)) {
                StockRequest buy = buyAmount;
                buyAmount = null;

                QuoteObject quote = quote(stock, tid);    // Get a quote first to see if the current price is good
                if (quote.getErrorString().isEmpty()) {
                    int stockDollars = quote.getDollars();
                    int stockCents = quote.getCents();
                    history.add("SET_BUY_TRIGGER," + userName + "," + stock + "," + dollars + "." + cents);

                    if (stockDollars < dollars || (stockDollars == dollars && stockCents <= cents)) {
                        long stockPrice = (long) stockDollars * CENT_CAP + stockCents;
                        long stockPurchaseMoney = (long) buy.getDollars() * CENT_CAP + buy.getCents();
                        int stockCount = (int) (stockPurchaseMoney / stockPrice);
                        long remainingMoney = stockPurchaseMoney % stockPrice;

                        logAddFunds((int)(remainingMoney / CENT_CAP), (int)(remainingMoney % CENT_CAP), tid);
                        this.dollars += (remainingMoney / CENT_CAP);
                        this.cents += (remainingMoney % CENT_CAP);
                        if (this.cents >= CENT_CAP) {
                            this.cents -= CENT_CAP;
                            this.dollars++;
                        }

                        int newStockCount = stockCount;
                        if (stocksOwned.containsKey(stock)) {
                            newStockCount = stocksOwned.get(stock);
                        }
                        stocksOwned.put(stock, newStockCount);

                        history.add("BUY," + userName + "," + stock + "," + buy.getDollars() + "." + buy.getCents());
                        history.add("COMMIT_BUY," + userName);

                        triggerSet = "SET_BUY_TRIGGER,BUY,COMMIT_BUY," + quote;
                    } else {
                        // TODO: Update expiry of buy request amount
                        StockTrigger trigger = new StockTrigger(buy, dollars, cents);
                        buyTriggers.add(trigger);

                        // TODO: Start a trigger timer
                        triggerSet = "SET_BUY_TRIGGER," + quote;
                    }

                } else {
                    triggerSet = "SET_BUY_TRIGGER ERROR," + userName + "," + this.dollars + "." + this.cents + "," + quote;
                }
            } else {
                triggerSet = "SET_BUY_TRIGGER ERROR";
            }
        }

        return triggerSet;
    }

    public String setSellAmount(String stock, int dollars, int cents, int tid) {
        String setRes;
        synchronized (lock) {
            // TODO: Confirm the business logic that should be followed if a previous set sell is present
            int ownedCount = 0;
            if (stocksOwned.containsKey(stock)) {
                ownedCount = stocksOwned.get(stock);
            }

            if (sellAmount == null && ownedCount >0) {
                sellAmount = new StockRequest(stock, 0, dollars, cents, 0);

                history.add("SET_SELL_AMOUNT," + userName + "," + stock + "," + dollars + "." + cents);
                setRes = "SET_SELL_AMOUNT," + userName + "," + stock + "," + dollars + "." + cents;
            } else {
                setRes = "SET_SELL_AMOUNT ERROR," + userName + "," + this.dollars + "." + this.cents;
            }
        }

        return setRes;
    }

    public String cancelSetSell(String stock, int tid) {
        String cancelSet;
        synchronized (lock) {
            StockRequest sellReq = null;
            history.add("CANCEL_SET_SELL," + userName + "," + stock);

            if (sellAmount != null && sellAmount.getStock().equals(stock)) {
                sellReq = sellAmount;
                sellAmount = null;
            } else {
                Iterator<StockTrigger> iter = sellTriggers.descendingIterator();
                while (iter.hasNext()) {
                    StockTrigger trigger = iter.next();
                    if (trigger.getSetAmount().getStock().equals(stock)) {
                        sellTriggers.remove(trigger);
                        sellReq = trigger.getSetAmount();
                        trigger.cancelTrigger(); // Notify the trigger executor that this trigger is cancelled.
                        break;
                    }
                }
            }

            if (sellReq != null) {
                // Returns stocks to holdings
                int stockCount = sellReq.getShares();
                if (stocksOwned.containsKey(stock)) {
                    stockCount = stocksOwned.get(stock);
                }

                stocksOwned.put(stock, stockCount);

                cancelSet = "CANCEL_SET_SELL," + userName + "," + stock + "," + this.dollars + "." + this.cents;
            } else {
                cancelSet = "CANCEL_SET_SELL ERROR";
            }
        }

        return cancelSet;
    }

    public String setSellTrigger(String stock, int dollars, int cents, int tid) {
        String triggerSet;
        synchronized (lock) {
            if (sellAmount != null && sellAmount.getStock().equals(stock)) {
                StockRequest sell = sellAmount;
                sellAmount = null;

                QuoteObject quote = quote(stock, tid);    // Get a quote first to see if the current price is good
                if (quote.getErrorString().isEmpty()) {
                    long sellMoney = (long) sell.getDollars() * CENT_CAP + sell.getCents();

                    int stockDollars = quote.getDollars();
                    int stockCents = quote.getCents();
                    long stockPrice = (long) stockDollars * CENT_CAP + stockCents;

                    int ownedCount = 0;
                    if (stocksOwned.containsKey(stock)) {
                        ownedCount = stocksOwned.get(stock);
                    }
                    long ownedValue = stockPrice * ownedCount;

                    int actualSellCount = (int) (sellMoney / stockPrice);
                    long actualValue = actualSellCount * stockCents;

                    if (ownedValue >= actualValue) {
                        stocksOwned.put(stock, ownedCount - actualSellCount);
                        history.add("SET_SELL_TRIGGER," + userName + "," + stock + "," + dollars + "." + cents);

                        if (stockDollars > dollars || (stockDollars == dollars && stockCents >= cents)) {
                            logAddFunds((int)(actualValue / CENT_CAP), (int)(actualValue % CENT_CAP), tid);
                            this.dollars += (int) (actualValue / CENT_CAP);
                            this.cents += (int) (actualValue % CENT_CAP);
                            if (this.cents >= CENT_CAP) {
                                this.cents -= CENT_CAP;
                                this.dollars++;
                            }

                            history.add("SELL," + userName + "," + stock + "," + sell.getDollars() + "." + sell.getCents());
                            history.add("COMMIT_SELL," + userName);

                            triggerSet = "SET_SELL_TRIGGER,SELL,COMMIT_SELL," + quote;
                        } else {
                            sell = new StockRequest(
                                    stock,
                                    actualSellCount,
                                    (int) (actualValue / CENT_CAP),
                                    (int) (actualValue % CENT_CAP),
                                    Calendar.getInstance().getTimeInMillis()
                            );
                            StockTrigger trigger = new StockTrigger(sell, dollars, cents);
                            sellTriggers.add(trigger);

                            // TODO: Start a trigger timer
                            triggerSet = "SET_SELL_TRIGGER," + quote;
                        }
                    } else {
                        triggerSet = "SET_SELL_TRIGGER ERROR," + userName + "," + this.dollars + "." + this.cents + "," + quote;
                    }
                } else {
                    triggerSet = "SET_SELL_TRIGGER ERROR," + userName + "," + this.dollars + "." + this.cents + "," + quote;
                }
            } else {
                triggerSet = "SET_SELL_TRIGGER ERROR";
            }
        }

        return triggerSet;
    }

    public String dumplog(String filename, int tid) {
        // TODO: May have to eventually handle this (or just handle elsewhere)
        history.add("DUMPLOG," + userName + "," + filename);
        return "";
    }

    public String dumplog(int tid) {
        // TODO: May have to eventually handle this (or just handle elsewhere)
        return "";
    }

    public String displaySummary(int tid) {
        StringBuilder summary = new StringBuilder();
        for (String event : history) {
            summary.append(event);
            summary.append(";");
        }

        history.add("DISPLAY_SUMMARY," + userName);

        return summary.toString();
    }

    private void logAddFunds(int dollars, int cents, int tid) {
        logFundChange("add", dollars, cents, tid);
    }

    private void logRemoveFunds(int dollars, int cents, int tid) {
        logFundChange("remove", dollars, cents, tid);
    }

    private void logFundChange(String type, int dollars, int cents, int tid) {
        AccountTransactionType atType = new AccountTransactionType();
        atType.setTimestamp(Calendar.getInstance().getTimeInMillis());
        atType.setServer(ServerConstants.TX_SERVERS[0]);
        atType.setTransactionNum(BigInteger.valueOf(tid));
        atType.setAction(type);
        atType.setUsername(userName);
        atType.setFunds(BigDecimal.valueOf((long)dollars * CENT_CAP + cents, 2));

        Logger.getInstance().Log(atType);
    }
}
