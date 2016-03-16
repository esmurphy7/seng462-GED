package com.teamged.txserver.transactions;

import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.TransactionCompleteType;
import com.teamged.txserver.InternalLog;
import com.teamged.txserver.TransactionMonitor;
import com.teamged.txserver.database.QuoteCache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
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
            InternalLog.Critical("Error processing request: " + to.toString() + "; Error msg: " + to.getErrorString());

            // When the transaction request is in error, then that transaction is by definition complete (cannot
            // be processed any further). We log the completion.
            TransactionCompleteType tcType = new TransactionCompleteType();
            tcType.setTransactionNum(BigInteger.valueOf(to.getWorkloadSeqNum()));
            Logger.getInstance().Log(tcType);
        } else {
            InternalLog.Log("[REQUEST] " + request);
            // This has the potential to block for a while (if the request queue is full)
            // TODO: This does not properly deal with transactions that have no user name (DUMPLOG_ROOT)
            TransactionMonitor.AddTransactionObject(to);
            TransactionMonitor.PutRequestQueue(to.getUserName());

            // Prefetch the quote if the prepared command will result in one when it is run
            switch (to.getUserCommand()) {
                case QUOTE:
                case SET_BUY_TRIGGER:
                case SET_SELL_TRIGGER:
                    QuoteCache.prefetchQuote(to.getStockSymbol(), to.getUserName(), to.getWorkloadSeqNum());
                    break;
                case BUY:
                case SELL:
                    QuoteCache.prefetchShortQuote(to.getStockSymbol(), to.getUserName(), to.getWorkloadSeqNum());
                    break;
                default:
                    // do nothing
                    break;
            }
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
