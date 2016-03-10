package com.teamged.proxyserver.quotecache;

import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.CommandType;
import com.teamged.logging.xmlelements.ErrorEventType;
import com.teamged.logging.xmlelements.QuoteServerType;
import com.teamged.proxyserver.InternalLog;
import com.teamged.proxyserver.ProxyMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by DanielF on 2016-03-09.
 */
public class QuoteCache {
    private static ExecutorService quotePool = Executors.newFixedThreadPool(ProxyMain.Deployment.getProxyServer().getInternals().getThreadPoolSize());
    private static final ConcurrentHashMap<String, Future<QuoteObject>> quoteMap = new ConcurrentHashMap<>();
    private static final AtomicInteger roundRobinCounter = new AtomicInteger(0);
    private static final int fetchServerCount = ProxyMain.Deployment.getFetchServers().getServers().size();

    public static QuoteObject fetchQuote(String stock, String callingUser, int tid) {
        QuoteObject q;
        try {
            InternalLog.Log("Fetching regular quote. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
            q = fetchQuoteObject(stock, callingUser, tid, false);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logErrorEvent(stock, callingUser, tid, e.getMessage());
            q = QuoteObject.fromQuote("QUOTE ERROR");
        }

        return q;
    }

    public static QuoteObject fetchShortQuote(String stock, String callingUser, int tid) {
        QuoteObject q;
        try {
            InternalLog.Log("Fetching short quote. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + Calendar.getInstance().getTimeInMillis());
            q = fetchQuoteObject(stock, callingUser, tid, true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logErrorEvent(stock, callingUser, tid, e.getMessage());
            q = QuoteObject.fromQuote("QUOTE ERROR");
        }

        return q;
    }

    private static QuoteObject fetchQuoteObject(String stock, String callingUser, int tid, boolean useShortTimeout)
            throws ExecutionException, InterruptedException {
        Future<QuoteObject> fq;
        long nowMillis = Calendar.getInstance().getTimeInMillis();

        fq = quoteMap.get(stock);
        if (fq == null ||
                fq.isCancelled() ||
                (!useShortTimeout && (fq.isDone() && fq.get().getQuoteTimeout() < nowMillis)) ||
                (useShortTimeout && (fq.isDone() && fq.get().getQuoteShortTimeout() < nowMillis))) {
            InternalLog.Log("Cache miss for quote. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + nowMillis);
            fq = quotePool.submit(() -> fetchQuoteFromServer(stock, callingUser, tid));
            quoteMap.put(stock, fq);
        } else {
            InternalLog.Log("Cache hit for quote. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + nowMillis);
        }

        return fq.get();
    }

    private static QuoteObject fetchQuoteFromServer(String stock, String callingUser, int tid) {
        QuoteObject quote;
        long nowMillis;
        int index = roundRobinCounter.incrementAndGet() % fetchServerCount;
        try (
                Socket quoteSocket = new Socket(ProxyMain.Deployment.getFetchServers().getServers().get(index), ProxyMain.Deployment.getFetchServers().getPort());
                PrintWriter out = new PrintWriter(quoteSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
        ) {
            String quoteString;
            out.println(stock + "," + callingUser);
            quoteString = in.readLine();
            quote = QuoteObject.fromQuote(quoteString);
            nowMillis = Calendar.getInstance().getTimeInMillis();

            if (!quote.getErrorString().isEmpty()) {
                InternalLog.Log("Quote fetch error. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + nowMillis);
            } else {
                InternalLog.Log("Quote fetch complete. Stock: " + stock + "; User: " + callingUser + "; ID: " + tid + "; Timestamp: " + nowMillis);
                QuoteServerType qst = new QuoteServerType();
                qst.setTimestamp(nowMillis);
                qst.setQuoteServerTime(BigInteger.valueOf(quote.getQuoteInternalTime()));
                qst.setServer(ProxyMain.getServerName());
                qst.setTransactionNum(BigInteger.valueOf(tid));
                qst.setPrice(quote.getPrice());
                qst.setStockSymbol(quote.getStockSymbol());
                qst.setUsername(quote.getUserName());
                qst.setCryptokey(quote.getCryptoKey());
                Logger.getInstance().Log(qst);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logErrorEvent(stock, callingUser, tid, e.getMessage());
            quote = QuoteObject.fromQuote("QUOTE ERROR");
        }

        return quote;
    }

    private static void logErrorEvent(String stock, String callingUser, int tid, String errMessage) {
        ErrorEventType eet = new ErrorEventType();
        eet.setTimestamp(Calendar.getInstance().getTimeInMillis());
        eet.setServer(ProxyMain.getServerName());
        eet.setTransactionNum(BigInteger.valueOf(tid));
        eet.setCommand(CommandType.QUOTE);
        eet.setUsername(callingUser);
        eet.setStockSymbol(stock);
        eet.setErrorMessage(errMessage);
        Logger.getInstance().Log(eet);
    }
}
