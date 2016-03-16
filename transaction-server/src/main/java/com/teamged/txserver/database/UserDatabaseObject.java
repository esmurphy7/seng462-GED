package com.teamged.txserver.database;

import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.AccountTransactionType;
import com.teamged.logging.xmlelements.CommandType;
import com.teamged.logging.xmlelements.ErrorEventType;
import com.teamged.logging.xmlelements.SystemEventType;
import com.teamged.txserver.InternalLog;
import com.teamged.txserver.TxMain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
                eeType.setServer(TxMain.getServerName());
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

    public QuoteObject quote(String stock, int tid) {
        InternalLog.CacheDebug("[QUOTE B] Fetching regular quote. Stock: " + stock + "; User: " + userName + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
        return performQuote(stock, tid, false);
    }

    public QuoteObject realtimeQuote(String stock, int tid) {
        InternalLog.CacheDebug("[QUOTE B] Fetching regular quote. Stock: " + stock + "; User: " + userName + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
        return performQuote(stock, tid, true);
    }

    public String buy(String stock, int dollars, int cents, int tid) {
        String buyRes;
        synchronized (lock) {
            if (this.dollars > dollars || (this.dollars == dollars && this.cents >= cents)) {
                QuoteObject quote = realtimeQuote(stock, tid);

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

                        // TODO: Start a timer to expire the buy request in 60 seconds
                        // TODO: Start a timer on the remaining life of the buy request to notify user
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
            QuoteObject quote = realtimeQuote(stock, tid);

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
                    // TODO: Start a timer to expire the stored sell request in 60 seconds
                    // TODO: Start a timer on the remaining life of the sell request to notify user
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
                    stockCount += stocksOwned.get(stock);
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
        StockTrigger trigger = null;
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
                        triggerPurchaseStock(stock, tid, buy, stockDollars, stockCents);

                        triggerSet = "SET_BUY_TRIGGER,BUY,COMMIT_BUY," + quote;
                    } else {
                        // TODO: Update expiry of buy request amount
                        trigger = new StockTrigger(buy, dollars, cents);
                        buyTriggers.add(trigger);

                        triggerSet = "SET_BUY_TRIGGER," + quote;
                    }

                } else {
                    triggerSet = "SET_BUY_TRIGGER ERROR," + userName + "," + this.dollars + "." + this.cents + "," + quote;
                }
            } else {
                triggerSet = "SET_BUY_TRIGGER ERROR";
            }
        }

        if (trigger != null) {
            InternalLog.Log("Setting buy trigger");
            final StockTrigger t = trigger;
            triggerScheduler.scheduleAtFixedRate(
                    (Runnable) () -> {
                        InternalLog.Log("Buy trigger running");
                        synchronized (lock) {
                            if (t.isCancelled()) {
                                throw new TriggerCompletion();
                            } else {
                                QuoteObject quote = quote(stock, tid);
                                if (quote.getErrorString().isEmpty()) {
                                    int stockDollars = quote.getDollars();
                                    int stockCents = quote.getCents();

                                    if (stockDollars < dollars || (stockDollars == dollars && stockCents <= cents)) {
                                        triggerPurchaseStock(stock, tid, t.getSetAmount(), stockDollars, stockCents);
                                        throw new TriggerCompletion();
                                    }
                                }
                            }
                        }
                    },
                    60,
                    60,
                    TimeUnit.SECONDS
            );
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

            if (sellAmount == null && ownedCount > 0) {
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

                        // Returns stocks to holdings from the trigger
                        int stockCount = sellReq.getShares();
                        if (stocksOwned.containsKey(stock)) {
                            stockCount += stocksOwned.get(stock);
                        }

                        stocksOwned.put(stock, stockCount);

                        break;
                    }
                }
            }

            if (sellReq != null) {
                cancelSet = "CANCEL_SET_SELL," + userName + "," + stock + "," + this.dollars + "." + this.cents;
            } else {
                cancelSet = "CANCEL_SET_SELL ERROR";
            }
        }

        return cancelSet;
    }

    public String setSellTrigger(String stock, int dollars, int cents, int tid) {
        StockTrigger trigger = null;
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
                            triggerSellStock(stock, tid, sell, actualValue);

                            triggerSet = "SET_SELL_TRIGGER,SELL,COMMIT_SELL," + quote;
                        } else {
                            sell = new StockRequest(
                                    stock,
                                    actualSellCount,
                                    (int) (actualValue / CENT_CAP),
                                    (int) (actualValue % CENT_CAP),
                                    Calendar.getInstance().getTimeInMillis()
                            );
                            trigger = new StockTrigger(sell, dollars, cents);
                            sellTriggers.add(trigger);

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

        if (trigger != null) {
            InternalLog.Log("Setting sell trigger");
            final StockTrigger t = trigger;
            triggerScheduler.scheduleAtFixedRate(
                    (Runnable) () -> {
                        InternalLog.Log("Sell trigger running");
                        synchronized (lock) {
                            if (t.isCancelled()) {
                                throw new TriggerCompletion();
                            } else {
                                QuoteObject quote = quote(stock, tid);    // Get a quote first to see if the current price is good
                                if (quote.getErrorString().isEmpty()) {
                                    long sellMoney = (long) t.getSetAmount().getDollars() * CENT_CAP + t.getSetAmount().getCents();

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
                                            triggerSellStock(stock, tid, t.getSetAmount(), actualValue);
                                            throw new TriggerCompletion();
                                        }
                                    }
                                }
                            }
                        }
                    },
                    60,
                    60,
                    TimeUnit.SECONDS
            );
        }

        return triggerSet;
    }

    public String dumplog(String filename, int tid) {
        // TODO: May have to eventually handle this (or just handle elsewhere)
        history.add("DUMPLOG," + userName + "," + filename);
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
        atType.setServer(TxMain.getServerName());
        atType.setTransactionNum(BigInteger.valueOf(tid));
        atType.setAction(type);
        atType.setUsername(userName);
        atType.setFunds(BigDecimal.valueOf((long)dollars * CENT_CAP + cents, 2));

        Logger.getInstance().Log(atType);
    }

    /**
     * Performs a quote operation with multiple caching levels. First, the local cache for this user is consulted.
     * If the local user cache has no item, or an outdated item for the operation requested, the secondary quote cache
     * will be consulted. That quote cache will either return a cached value or else fetch a quote from the quote
     * server.
     *
     * @param stock The name of the stock to fetch a quote for.
     * @param tid The transaction identifier for this operation.
     * @param useShortTimeout True to use a short timeout period, false to use a regular timeout.
     * @return The quote.
     */
    private QuoteObject performQuote(String stock, int tid, boolean useShortTimeout) {
        QuoteObject quote;
        synchronized (lock) {
            long currTime = Calendar.getInstance().getTimeInMillis();
            if ((quote = stockCache.get(stock)) != null) {
                if (!quote.getErrorString().isEmpty()) {
                    // Log error
                    quote = null;
                } else {
                    long timeout = useShortTimeout ? quote.getQuoteShortTimeout() : quote.getQuoteTimeout();
                    if (currTime >= timeout) {
                        // Log timed out quote?
                        stockCache.remove(stock);
                        quote = null;
                    }
                }
            }

            if (quote == null) {
                InternalLog.CacheDebug("[QUOTE C1] Cache Level I miss for quote. Stock: " + stock + "; User: " + userName + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
                InternalLog.Log("[DEBUG PRINT] FETCHING QUOTE");
                if (useShortTimeout) {
                    quote = QuoteCache.fetchShortQuote(stock, userName, tid);
                } else {
                    quote = QuoteCache.fetchQuote(stock, userName, tid);
                }

                if (quote.getErrorString().isEmpty()) {
                    stockCache.put(stock, quote);
                }
            } else {
                InternalLog.CacheDebug("[QUOTE C1] Cache Level I hit for quote. Stock: " + stock + "; User: " + userName + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
            }

            history.add("QUOTE," + userName + "," + stock);
        }

        if (quote.getErrorString().isEmpty()) {
            InternalLog.CacheDebug("[QUOTE] Quote fetch complete. Stock: " + stock + "; User: " + userName + "; Value: $" + quote.getPrice() + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
        } else {
            InternalLog.CacheDebug("[QUOTE] Quote fetch complete. Stock: " + stock + "; User: " + userName + "; Value: $NA; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
        }
        return quote;
    }

    private void triggerPurchaseStock(String stock, int tid, StockRequest buy, long stockDollars, int stockCents) {
        long stockPrice = stockDollars * CENT_CAP + stockCents;
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

        long actualValue = stockDollars * 100 + stockCents;

        SystemEventType set = new SystemEventType();
        set.setCommand(CommandType.BUY);
        set.setFunds(BigDecimal.valueOf(actualValue, 2));
        set.setServer(TxMain.getServerName());
        set.setStockSymbol(stock);
        set.setTimestamp(Calendar.getInstance().getTimeInMillis());
        set.setTransactionNum(BigInteger.valueOf(tid));
        set.setUsername(userName);

        SystemEventType setc = new SystemEventType();
        setc.setCommand(CommandType.COMMIT_BUY);
        setc.setServer(TxMain.getServerName());
        setc.setTransactionNum(BigInteger.valueOf(tid));
        setc.setUsername(userName);
        setc.setTimestamp(Calendar.getInstance().getTimeInMillis());

        Logger.getInstance().Log(set);
        Logger.getInstance().Log(setc);

        history.add("BUY," + userName + "," + stock + "," + buy.getDollars() + "." + buy.getCents());
        history.add("COMMIT_BUY," + userName);
    }

    private void triggerSellStock(String stock, int tid, StockRequest sell, long actualValue) {
        logAddFunds((int) (actualValue / CENT_CAP), (int) (actualValue % CENT_CAP), tid);
        this.dollars += (int) (actualValue / CENT_CAP);
        this.cents += (int) (actualValue % CENT_CAP);
        if (this.cents >= CENT_CAP) {
            this.cents -= CENT_CAP;
            this.dollars++;
        }

        SystemEventType set = new SystemEventType();
        set.setCommand(CommandType.SELL);
        set.setFunds(BigDecimal.valueOf(actualValue, 2));
        set.setServer(TxMain.getServerName());
        set.setStockSymbol(stock);
        set.setTimestamp(Calendar.getInstance().getTimeInMillis());
        set.setTransactionNum(BigInteger.valueOf(tid));
        set.setUsername(userName);

        SystemEventType setc = new SystemEventType();
        setc.setCommand(CommandType.COMMIT_SELL);
        setc.setServer(TxMain.getServerName());
        setc.setTransactionNum(BigInteger.valueOf(tid));
        setc.setUsername(userName);
        setc.setTimestamp(Calendar.getInstance().getTimeInMillis());

        Logger.getInstance().Log(set);
        Logger.getInstance().Log(setc);

        history.add("SELL," + userName + "," + stock + "," + sell.getDollars() + "." + sell.getCents());
        history.add("COMMIT_SELL," + userName);
    }
}
