package com.teamged.txserver.transactions;

import com.teamged.ServerConstants;
import com.teamged.txserver.TransactionMonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by DanielF on 2016-02-01.
 */
public class RequestProcessingHandler implements Runnable {
    private final Socket socket;

    public RequestProcessingHandler(Socket socket) {
        this.socket = socket;
        System.out.println("Request processor connected to client on port " + socket.getLocalPort());
    }

    @Override
    public void run() {
        String request = receiveRequest();
        TransactionObject to = new TransactionObject(request);
        if (!to.getErrorString().isEmpty()) {
            System.out.println("Error processing request: " + to.toString() + "; Error msg: " + to.getErrorString());
        } else {
            // This has the potential to block for a while (if the request queue is full)
            TransactionMonitor.AddTransactionObject(to);
            TransactionMonitor.PutRequestQueue(to.getUserName());
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
}
