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
        String request = receiveRequest();
        Calendar cal = Calendar.getInstance();
        long quoteStartTime = cal.getTimeInMillis();
        long quoteEndTime = 0;
        boolean gotQuote = false;
        String quoteString = null;

        try (
                Socket quoteSocket = new Socket(FetchMain.Deployment.getQuoteServer().getServer(), FetchMain.Deployment.getQuoteServer().getPort());
                PrintWriter quoteOut = new PrintWriter(quoteSocket.getOutputStream());
                BufferedReader quoteIn = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
        ) {
            quoteOut.println(request);
            quoteString = quoteIn.readLine();

            quoteEndTime = cal.getTimeInMillis();
            quoteString += "," + quoteEndTime;
            gotQuote = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (gotQuote) {
            sendResponse(quoteString);
            long quoteFetchTime = quoteEndTime - quoteStartTime;
            // TODO: Record fetch time
            // TODO: Log quote fetch event
        } else {
            sendResponse("ERROR");
        }
    }

    private String receiveRequest() {
        String request = "";
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            request = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request;
    }

    private void sendResponse(String resp) {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream())) {
            out.println(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
