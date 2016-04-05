package com.teamged.proxyserver.serverthreads;

import com.teamged.comms.CommsInterface;
import com.teamged.comms.ServerMessage;
import com.teamged.proxyserver.quotecache.QuoteCache;
import com.teamged.proxyserver.quotecache.QuoteObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DanielF on 2016-03-19.
 */
public class QuoteMessageProcessingThread extends ProxyServerThread {
    // Stock, User, Sequence (tid), UseShortTimeout
    private static Pattern quoteReqPattern = Pattern.compile("^([^,]+),([^,]+),(\\d+),(\\d+)$");

    private final ExecutorService pool;
    private final Object syncObject;
    private boolean running = false;

    public QuoteMessageProcessingThread(int poolSize, Object syncObject) {
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.syncObject = syncObject;
        this.running = true;
    }

    private static void proxyProcess(ServerMessage serverMessage) {
        System.out.println("Proxy process servicing quote request: " + serverMessage.getData()); // TODO: Debugging line
        String serverResp;
        try {
            Matcher m = quoteReqPattern.matcher(serverMessage.getData());
            if (m.matches()) {
                String stock = m.group(1);
                String user = m.group(2);
                int tid = Integer.parseInt(m.group(3));
                boolean shortTimeout = Integer.parseInt(m.group(4)) != 0;

                QuoteObject quote;
                if (shortTimeout) {
                    quote = QuoteCache.fetchShortQuote(stock, user, tid);
                } else {
                    quote = QuoteCache.fetchQuote(stock, user, tid);
                }

                if (!quote.getErrorString().isEmpty()) {
                    serverResp = quote.getErrorString();
                } else {
                    serverResp = quote.toString();
                }
            } else {
                serverResp = "ERROR";
            }
        } catch (NumberFormatException nfe) {
            serverResp = "ERROR";
        }

        serverMessage.setResponse(serverResp);
    }

    private static void prefetchProcess(String quoteRequest) {
        System.out.println("Prefetch process servicing quote request: " + quoteRequest); // TODO: Debugging line
        Matcher m = quoteReqPattern.matcher(quoteRequest);
        try {
            if (m.matches()) {
                String stock = m.group(1);
                String user = m.group(2);
                int tid = Integer.parseInt(m.group(3));
                QuoteCache.prefetchQuote(stock, user, tid);
            }
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        System.out.println("Quote message processing task running");
        while (true) {
            ServerMessage serverMessage = CommsInterface.getNextServerRequest();
            if (serverMessage == null) {
                continue;
            }

            int flagVal = serverMessage.getFlags();
            if (flagVal == 0) {
                // Regular quote fetch
                pool.execute(() -> proxyProcess(serverMessage));
            } else if (flagVal == 1) {
                // Quote pre-fetch
                pool.execute(() -> prefetchProcess(serverMessage.getData()));
            } else {
                // Unknown
                serverMessage.setResponse("FLAG ERROR");
            }
        }
    }
}
