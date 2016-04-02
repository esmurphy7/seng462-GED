package com.teamged.txserver.transactions;

import com.teamged.comms.ServerMessage;
import com.teamged.txserver.InternalLog;
import com.teamged.txserver.TransactionMonitor;
import com.teamged.txserver.database.QuoteCache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by DanielF on 2016-02-01.
 */
public class RequestProcessingHandler implements Runnable {
    private final ServerMessage serverMessage;

    public RequestProcessingHandler(ServerMessage serverMessage) {
        this.serverMessage = serverMessage;
        InternalLog.Log("Connection procesor received data: " + serverMessage.getData());
    }

    @Override
    public void run() {
        TransactionObject to = new TransactionObject(serverMessage);
        if (!to.getErrorString().isEmpty()) {
            InternalLog.Critical("Request Error for [" + to.getWorkloadSeqNum() + "]: " + to.getErrorString());
            // Even though the transaction is in error, we still need to let the system see it.
            if (to.getUserName() != null) {
                TransactionMonitor.AddTransactionObject(to);
                TransactionMonitor.PutRequestQueue(to.getUserName());
            }
        } else {
            InternalLog.Log("[REQUEST] " + to.toString());
            TransactionMonitor.AddTransactionObject(to);
            TransactionMonitor.PutRequestQueue(to.getUserName());

            // Prefetch the quote if the prepared command will result in one when it is run
            switch (to.getUserCommand()) {
                case QUOTE:
                case SET_BUY_TRIGGER:
                case SET_SELL_TRIGGER:
                case BUY:
                case SELL:
                    QuoteCache.prefetchQuote(to.getStockSymbol(), to.getUserName(), to.getWorkloadSeqNum());
                    break;
                default:
                    // do nothing - this transaction will not produce a quote
                    break;
            }
        }
    }
}
