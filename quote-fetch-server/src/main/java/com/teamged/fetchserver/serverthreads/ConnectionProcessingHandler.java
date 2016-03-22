package com.teamged.fetchserver.serverthreads;

import com.teamged.comms.ServerMessage;
import com.teamged.fetchserver.FetchMain;
import com.teamged.fetchserver.InternalLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

/**
 * Created by DanielF on 2016-03-08.
 */
public class ConnectionProcessingHandler implements Runnable {
    private final ServerMessage serverMessage;

    public ConnectionProcessingHandler(ServerMessage serverMessage) {
        this.serverMessage = serverMessage;
        InternalLog.Log("Connection processor received data: " + serverMessage.getData());
    }

    @Override
    public void run() {
        String request = serverMessage.getData();
        long quoteStartTime = System.nanoTime();
        long quoteEndTime = 0;
        boolean gotQuote = false;
        String quoteString = null;

        try (
                Socket quoteSocket = new Socket(FetchMain.Deployment.getQuoteServer().getServer(), FetchMain.Deployment.getQuoteServer().getPort());
                PrintWriter quoteOut = new PrintWriter(quoteSocket.getOutputStream(), true);
                BufferedReader quoteIn = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
        ) {
            InternalLog.Log("Fetching request: " + request + " from [" + FetchMain.Deployment.getQuoteServer().getServer() + ":" + FetchMain.Deployment.getQuoteServer().getPort() + "]");
            quoteOut.println(request);
            quoteString = quoteIn.readLine();

            quoteEndTime = System.nanoTime();
            gotQuote = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gotQuote) {
            long quoteFetchTime = quoteEndTime - quoteStartTime;
            InternalLog.Log("Returning response: " + quoteString + "; fetched in " + quoteFetchTime + "ns");
            serverMessage.setResponse(quoteString);

            // TODO: Record fetch time
            // TODO: Log quote fetch event
        } else {
            serverMessage.setResponse("ERROR");
        }
    }
}
