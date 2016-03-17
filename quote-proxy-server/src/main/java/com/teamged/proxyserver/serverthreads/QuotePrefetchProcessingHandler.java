package com.teamged.proxyserver.serverthreads;

import com.teamged.proxyserver.quotecache.QuoteCache;
import com.teamged.proxyserver.quotecache.QuoteObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DanielF on 2016-03-09.
 */
public class QuotePrefetchProcessingHandler implements Runnable {

    // Stock, User, Sequence (tid), UseShortTimeout
    private static Pattern quoteReqPattern = Pattern.compile("^([^,]+),([^,]+),(\\d+),(\\d+)$");

    private final Socket socket;

    public QuotePrefetchProcessingHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String request;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            request = in.readLine();
            Matcher m = quoteReqPattern.matcher(request);
            if (m.matches()) {
                String stock = m.group(1);
                String user = m.group(2);
                int tid = Integer.parseInt(m.group(3));
                boolean shortTimeout = Integer.parseInt(m.group(4)) != 0;

                if (shortTimeout) {
                    QuoteCache.prefetchShortQuote(stock, user, tid);
                } else {
                    QuoteCache.prefetchQuote(stock, user, tid);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
