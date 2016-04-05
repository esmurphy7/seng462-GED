package com.teamged.fetchserver.serverthreads;

import com.teamged.comms.ServerMessage;
import com.teamged.fetchserver.FetchMain;
import com.teamged.fetchserver.InternalLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by DanielF on 2016-03-08.
 */
public class ConnectionProcessingHandler implements Runnable {
    private final ServerMessage serverMessage;
    private final ConcurrentLinkedQueue<String> timingQueue;

    public ConnectionProcessingHandler(ServerMessage serverMessage, ConcurrentLinkedQueue<String> timingQueue) {
        this.serverMessage = serverMessage;
        this.timingQueue = timingQueue;
        InternalLog.Log("Connection processor received data: " + serverMessage.getData());
    }

    @Override
    public void run() {
        String request = serverMessage.getData();
        long quoteStartTime = System.nanoTime();
        long quoteEndTime = 0;
        boolean gotQuote = false;
        boolean retryQuote = false;
        String quoteString = null;

        do {
            try (
                    Socket quoteSocket = new Socket(FetchMain.Deployment.getQuoteServer().getServer(), FetchMain.Deployment.getQuoteServer().getPort());
                    PrintWriter quoteOut = new PrintWriter(quoteSocket.getOutputStream(), true);
                    BufferedReader quoteIn = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
            ) {
                quoteSocket.setSoTimeout(350);
                if (retryQuote) {
                    InternalLog.Log("Refetching request: " + request + " from [" + FetchMain.Deployment.getQuoteServer().getServer() + ":" + FetchMain.Deployment.getQuoteServer().getPort() + "]");
                } else {
                    InternalLog.Log("Fetching request: " + request + " from [" + FetchMain.Deployment.getQuoteServer().getServer() + ":" + FetchMain.Deployment.getQuoteServer().getPort() + "]");
                }
                retryQuote = false;

                quoteOut.println(request);
                quoteString = quoteIn.readLine();

                quoteEndTime = System.nanoTime();
                gotQuote = true;
            } catch (SocketTimeoutException ste) {
                InternalLog.Log("Quote timed out for: " + request + " - will retry!");
                retryQuote = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (gotQuote || retryQuote) {
                long quoteFetchTime = quoteEndTime - quoteStartTime;
                timingQueue.add(quoteFetchTime + "\n");
                if (!retryQuote) {
                    InternalLog.Log("Returning response: " + quoteString + "; fetched in " + quoteFetchTime + "ns");
                    serverMessage.setResponse(quoteString);
                }
            } else if (!retryQuote) {
                serverMessage.setResponse("ERROR");
            }
        } while (retryQuote);
    }
}
