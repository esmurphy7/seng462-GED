package com.teamged.txserver.transactions;

import com.teamged.txserver.InternalLog;
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
        InternalLog.Log("Request processor connected to client on port " + socket.getLocalPort());
    }

    @Override
    public void run() {
        String request = receiveRequest();
        TransactionObject to = new TransactionObject(request);
        if (!to.getErrorString().isEmpty()) {
            InternalLog.Log("Error processing request: " + to.toString() + "; Error msg: " + to.getErrorString());
        } else {
            InternalLog.Log("[REQUEST] " + request);
            // This has the potential to block for a while (if the request queue is full)
            // TODO: This does not properly deal with transactions that have no user name (DUMPLOG_ROOT)
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
