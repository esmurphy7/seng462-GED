package com.teamged.txserver.database;

import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.CommandType;
import com.teamged.logging.xmlelements.ErrorEventType;
import com.teamged.txserver.InternalLog;
import com.teamged.txserver.TxMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.*;

/**
 * Created by DanielF on 2016-02-27.
 */
public class QuoteCache {
    private static ExecutorService quotePool = Executors.newFixedThreadPool(TxMain.Deployment.getTransactionServers().getInternals().getProcedureThreads());
    private static final Object lock = new Object();
    private static final ConcurrentHashMap<String, Future<QuoteObject>> quoteMap = new ConcurrentHashMap<>(/* TODO: args */);

    // TODO: Add a cleanup service that occasionally traverses the quote map and removes anything expired

    /**
     * Fetches a quote either from the cache or the quote server. If the quote is not in the cache or if it has
     * expired (e.g. passed the valid ~60 second period), then the quote server will be queried. This is a blocking
     * operation and will not return until a quote is available.
     *
     * @param stock The name of the stock to fetch a quote for.
     * @param callingUser The name of the user asking for the quote value.
     * @param tid The transaction identifier for this operation.
     * @return The quote.
     */
    public static QuoteObject fetchQuote(String stock, String callingUser, int tid) {
        QuoteObject q;
        try {
            InternalLog.CacheDebug("[QUOTE C2] Fetching regular quote. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
            q = fetchQuoteObject(stock, callingUser, tid, false);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logErrorEvent(stock, callingUser, tid, e.getMessage());
            q = QuoteObject.fromQuote("QUOTE ERROR");
        }

        return q;
    }

    /**
     * Fetches a quote either from the cache or the quote server. If the quote is not in the cache or if it has
     * timed out under the shorter life span, then the quote server will be queried. This is a blocking operation
     * and will not return until a quote is available.
     *
     * @param stock The name of the stock to fetch a quote for.
     * @param callingUser The name of the user asking for the quote value.
     * @param tid The transaction identifier for this operation.
     * @return The quote.
     */
    public static QuoteObject fetchShortQuote(String stock, String callingUser, int tid) {
        QuoteObject q;
        try {
            InternalLog.CacheDebug("[QUOTE C2] Fetching realtime quote. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
            q = fetchQuoteObject(stock, callingUser, tid, true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logErrorEvent(stock, callingUser, tid, e.getMessage());
            q = QuoteObject.fromQuote("QUOTE ERROR");
        }

        return q;
    }

    /**
     * If a quote is not present in the local cache, or if it is present but has timed out, this notifies the
     * quote proxy to prefetch and cache a quote. This value will not be received until requested from the
     * quote server with a fetchQuote or fetchShortQuote.
     *
     * @param stock The name of the stock to prefetch a quote for.
     * @param callingUser The name of the user eventually asking for the quote value.
     * @param tid The transaction identifier for this operation.
     */
    public static void prefetchQuote(String stock, String callingUser, int tid) {
        prefetchQuoteObject(stock, callingUser, tid, false);
    }

    /**
     * If a quote is not present in the local cache, or if it is present but has timed out using the
     * shorter life span, this notifies the quote proxy to prefetch and cache a quote. This value will
     * not be received until requested from the quote server with a fetchQuote or fetchShortQuote.
     *
     * @param stock The name of the stock to prefetch a quote for.
     * @param callingUser The name of the user eventually asking for the quote value.
     * @param tid The transaction identifier for this operation.
     */
    public static void prefetchNewQuote(String stock, String callingUser, int tid) {
        prefetchQuoteObject(stock, callingUser, tid, true);
    }

    /**
     * Internal method for fetching a quote either from the cache or the quote server based on which timeout value
     * needs to be observed. This is a blocking operation and will not return until a quote is available or an exception
     * is thrown.
     *
     * @param stock The name of the stock to fetch a quote for.
     * @param callingUser The name of the user asking for the quote value.
     * @param tid The transaction identifier for this operation.
     * @param useShortTimeout True to use a short timeout period, false to use a regular timeout.
     * @return The quote.
     * @throws ExecutionException Task aborted by throwing an exception and has no value.
     * @throws InterruptedException Tasks were interrupted from executing.
     */
    private static QuoteObject fetchQuoteObject(String stock, String callingUser, int tid, boolean useShortTimeout)
            throws ExecutionException, InterruptedException {
        QuoteObject q;

        if (TxMain.l2Enabled() && (!useShortTimeout || (useShortTimeout && TxMain.rtEnabled()))) {
            Future<QuoteObject> fq;
            long nowMillis = Calendar.getInstance().getTimeInMillis();

            fq = quoteMap.get(stock);
            // Check if a new query needs to be prepped
            if (fq == null || // Not in cache
                fq.isCancelled() || // Cached a version with no value
                (!useShortTimeout && (fq.isDone() && fq.get().getQuoteTimeout() < nowMillis)) || // Cached, but older than a minute
                (useShortTimeout && (fq.isDone() && fq.get().getQuoteShortTimeout() < nowMillis))) // Cached, but older than half a second and we need brand new
            {
                // TODO: Take out some sort of write lock here? Could have unnecessary QUOTE requests here.
                InternalLog.CacheDebug("[QUOTE C2] Cache Level II miss for quote. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
                fq = quotePool.submit(() -> fetchQuoteFromServer(stock, callingUser, tid, useShortTimeout));
                quoteMap.put(stock, fq);
            } else {
                InternalLog.CacheDebug("[QUOTE C2] Cache Level II hit for quote. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
            }

            q = fq.get();
        } else {
            q = fetchQuoteFromServer(stock, callingUser, tid, useShortTimeout);
        }

        return q;
    }

    /**
     * Internal method for prefetching a quote if it is not present in the cache.
     *
     * @param stock The name of the stock to prefetch a quote for.
     * @param callingUser The name of the user eventually asking for the quote value.
     * @param tid The transaction identifier for this operation.
     * @param useShortTimeout True to use a short timeout period, false to use a regular timeout.
     */
    private static void prefetchQuoteObject(String stock, String callingUser, int tid, boolean useShortTimeout) {
        boolean doPrefetch = true;

        try {
            if (TxMain.prefetchEnabled() && (!useShortTimeout || (useShortTimeout && TxMain.rtEnabled()))) {
                Future<QuoteObject> fq = quoteMap.get(stock);
                long nowMillis = Calendar.getInstance().getTimeInMillis();

                if (fq == null || // Not in cache
                        fq.isCancelled() || // Cached a version with no value
                        (!useShortTimeout && (fq.isDone() && fq.get().getQuoteTimeout() < nowMillis)) || // Cached, but older than a minute
                        (useShortTimeout && (fq.isDone() && fq.get().getQuoteShortTimeout() < nowMillis))) // Cached, but older than half a second and we need brand new
                {
                    InternalLog.CacheDebug("[QUOTE PF] Cache Level II miss for prefetch quote - prefetch will occur. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + nowMillis);
                } else {
                    InternalLog.CacheDebug("[QUOTE C2] Cache Level II hit for prefetch quote. No prefetch necessary. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + nowMillis);
                    doPrefetch = false;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (doPrefetch) {
            quotePool.submit(() -> fetchQuoteFromServer(stock, callingUser, tid, useShortTimeout));
        }
    }

    /**
     * Contacts the remote server to fetch a quote.
     * @param stock The name of the stock to query the remote server for.
     * @param callingUser The name of the user asking for the quote value.
     * @param tid The transaction identifier for this operation.
     * @return The quote.
     */
    private static QuoteObject fetchQuoteFromServer(String stock, String callingUser, int tid, boolean userShortTimeout) {
        QuoteObject quote;
        long nowMillis;

        try (
                Socket quoteSocket = new Socket(TxMain.Deployment.getProxyServer().getServer(), TxMain.Deployment.getProxyServer().getPort());
                PrintWriter out = new PrintWriter(quoteSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
        ) {
            String quoteString;
            int shortTimeout = userShortTimeout ? 1 : 0;
            out.println(stock + "," + callingUser + "," + tid + "," + shortTimeout);
            quoteString = in.readLine();
            quote = QuoteObject.fromQuote(quoteString);
            nowMillis = Calendar.getInstance().getTimeInMillis();

            if (!quote.getErrorString().isEmpty()) {
                // Log error?
                InternalLog.CacheDebug("[QUOTE C3] Server query failed for quote. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + nowMillis);
            } else {
                InternalLog.CacheDebug("[QUOTE C3] Server query got quote. Stock: " + stock + "; User: " + callingUser + "; Value: $" + quote.getPrice() + "; ID: " + tid + "; Timestamp: " + nowMillis);
                // No longer logs the quote here - the canonical quote log happens in the proxy server now.
            }
        } catch(IOException e) {
            e.printStackTrace();
            logErrorEvent(stock, callingUser, tid, e.getMessage());
            quote = QuoteObject.fromQuote("QUOTE ERROR");
        }

        return quote;
    }

    private static void logErrorEvent(String stock, String callingUser, int tid, String errMessage) {
        ErrorEventType eet = new ErrorEventType();
        eet.setTimestamp(Calendar.getInstance().getTimeInMillis());
        eet.setServer(TxMain.getServerName());
        eet.setTransactionNum(BigInteger.valueOf(tid));
        eet.setCommand(CommandType.QUOTE);
        eet.setUsername(callingUser);
        eet.setStockSymbol(stock);
        eet.setErrorMessage(errMessage);
        Logger.getInstance().Log(eet);
    }
}
