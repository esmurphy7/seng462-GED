package com.teamged.fetchserver.serverthreads;

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
    private final Socket socket;

    public ConnectionProcessingHandler(Socket socket) {
        this.socket = socket;
        InternalLog.Log("Connection processor connected to client on port " + socket.getLocalPort());
    }

    @Override
    public void run() {
        String request;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream())
        ) {
            request = in.readLine();

            Calendar cal = Calendar.getInstance();
            long quoteStartTime = cal.getTimeInMillis();
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

                quoteEndTime = cal.getTimeInMillis();
                //quoteString += "," + quoteEndTime;
                gotQuote = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (gotQuote) {
                InternalLog.Log("Returning response: " + quoteString);
                out.println(quoteString);
                //sendResponse(quoteString);
                long quoteFetchTime = quoteEndTime - quoteStartTime;
                // TODO: Record fetch time
                // TODO: Log quote fetch event
            } else {
                out.println("ERROR");
                //sendResponse("ERROR");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
