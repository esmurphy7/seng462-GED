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
public class QuoteProxyProcessingHandler implements Runnable {

    // Stock, User, Sequence (tid), UseShortTimeout
    private static Pattern quoteReqPattern = Pattern.compile("^([^,]+),([^,]+),(\\d+),(\\d+)$");

    private final Socket socket;

    public QuoteProxyProcessingHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String request;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            request = in.readLine();
            Matcher m = quoteReqPattern.matcher(request);
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
                    out.println(quote.getErrorString());
                } else {
                    out.println(quote.toString());
                }
            } else {
                out.println("REQUEST ERROR," + request);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
