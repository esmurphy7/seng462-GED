package com.teamged.txserver.transactions;

import com.teamged.ServerConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by DanielF on 2016-01-30.
 */
public class QuoteBoundTransactionHandler implements Runnable {
    private final Socket socket;

    public QuoteBoundTransactionHandler(final Socket socket) {
        this.socket = socket;
        System.out.println("Connected to socket on port " + socket.getLocalPort());
    }

    @Override
    public void run() {
        String request = receiveRequest();
        String response = receiveResponse(request);
        // Add to response queue
        System.out.println("REQUEST: " + request + "\nRESPONSE: " + response);
    }

    private String receiveRequest() {
        String request = "";
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            request = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request;
    }

    private String receiveResponse(String request) {
        String response = "";
        try (
            Socket quoteSocket = new Socket(ServerConstants.QUOTE_SERVER, ServerConstants.QUOTE_PORT);
            PrintWriter out = new PrintWriter(quoteSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
        ) {
            out.println(request);
            response = in.readLine();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
