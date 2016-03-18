package com.seng462ged.daytrader.workloadgenerator;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Requester {

    public static void SendTransactions(String webServer, List<Transaction> transactions) {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(200, TimeUnit.SECONDS)
                .connectTimeout(200, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(String.format("http://%s/api/", webServer))
                .client(okHttpClient)
                .build();

        TransactionService transactionService = retrofit.create(TransactionService.class);

        for (int i = 0; i < transactions.size(); i++) {

            Transaction transaction = transactions.get(i);
            int userSequenceId = i + 1;

            System.out.println(String.format("[%d] User: %s [%d], Command: %s", transaction.getId(), transaction.getUserId(), userSequenceId, transaction.getCommand()));

            try {

                if (transaction.getCommand().equals("ADD")) {

                    transactionService.Add(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("QUOTE")) {

                    transactionService.Quote(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getStockSymbol()).execute();

                } else if (transaction.getCommand().equals("BUY")) {

                    transactionService.Buy(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("COMMIT_BUY")) {

                    transactionService.CommitBuy(transaction.getId(), userSequenceId, transaction.getUserId()).execute();

                } else if (transaction.getCommand().equals("CANCEL_BUY")) {

                    transactionService.CancelBuy(transaction.getId(), userSequenceId, transaction.getUserId()).execute();

                } else if (transaction.getCommand().equals("SET_BUY_AMOUNT")) {

                    transactionService.SetBuyAmount(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("SET_BUY_TRIGGER")) {

                    transactionService.SetBuyTrigger(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("CANCEL_SET_BUY")) {

                    transactionService.CancelSetBuy(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getStockSymbol()).execute();

                } else if (transaction.getCommand().equals("SELL")) {

                    transactionService.Sell(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("COMMIT_SELL")) {

                    transactionService.CommitSell(transaction.getId(), userSequenceId, transaction.getUserId()).execute();

                } else if (transaction.getCommand().equals("CANCEL_SELL")) {

                    transactionService.CancelSell(transaction.getId(), userSequenceId, transaction.getUserId()).execute();

                } else if (transaction.getCommand().equals("SET_SELL_AMOUNT")) {

                    transactionService.SetSellAmount(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("SET_SELL_TRIGGER")) {

                    transactionService.SetSellTrigger(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("CANCEL_SET_SELL")) {

                    transactionService.CancelSetSell(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getStockSymbol()).execute();

                } else if (transaction.getCommand().equals("DUMPLOG") && transaction.getUserId() != null) {

                    transactionService.Dumplog(transaction.getId(), userSequenceId, transaction.getUserId(), transaction.getFilename()).execute();

                } else if (transaction.getCommand().equals("DUMPLOG")) {

                    transactionService.Dumplog(transaction.getId(), transaction.getFilename()).execute();

                } else if (transaction.getCommand().equals("DISPLAY_SUMMARY")) {

                    transactionService.DisplaySummary(transaction.getId(), userSequenceId, transaction.getUserId()).execute();
                }

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}
