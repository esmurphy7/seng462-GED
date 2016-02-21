package com.seng462ged.daytrader.workloadgenerator;

import retrofit2.Retrofit;

import java.io.IOException;
import java.util.List;

public class Requester {

    public static void SendTransactions(List<Transaction> transactions) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/api/")
                .build();

        TransactionService transactionService = retrofit.create(TransactionService.class);

        for (Transaction transaction : transactions) {

            System.out.println(String.format("User: %s, Command: %s", transaction.getUserId(), transaction.getCommand()));

            try {

                if (transaction.getCommand().equals("ADD")) {

                    transactionService.Add(transaction.getUserId(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("QUOTE")) {

                    transactionService.Quote(transaction.getUserId(), transaction.getStockSymbol()).execute();

                } else if (transaction.getCommand().equals("BUY")) {

                    transactionService.Buy(transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("COMMIT_BUY")) {

                    transactionService.CommitBuy(transaction.getUserId()).execute();

                } else if (transaction.getCommand().equals("CANCEL_BUY")) {

                    transactionService.CancelBuy(transaction.getUserId()).execute();

                } else if (transaction.getCommand().equals("SET_BUY_AMOUNT")) {

                    transactionService.SetBuyAmount(transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("SET_BUY_TRIGGER")) {

                    transactionService.SetBuyTrigger(transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("CANCEL_SET_BUY")) {

                    transactionService.CancelSetBuy(transaction.getUserId(), transaction.getStockSymbol()).execute();

                } else if (transaction.getCommand().equals("SELL")) {

                    transactionService.Sell(transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("COMMIT_SELL")) {

                    transactionService.CommitSell(transaction.getUserId()).execute();

                } else if (transaction.getCommand().equals("CANCEL_SELL")) {

                    transactionService.CancelSell(transaction.getUserId()).execute();

                } else if (transaction.getCommand().equals("SET_SELL_AMOUNT")) {

                    transactionService.SetSellAmount(transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("SET_SELL_TRIGGER")) {

                    transactionService.SetSellTrigger(transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                } else if (transaction.getCommand().equals("CANCEL_SET_SELL")) {

                    transactionService.CancelSetSell(transaction.getUserId(), transaction.getStockSymbol()).execute();

                } else if (transaction.getCommand().equals("DUMPLOG") && transaction.getUserId() != null) {

                    transactionService.Dumplog(transaction.getUserId(), transaction.getFilename()).execute();

                } else if (transaction.getCommand().equals("DUMPLOG")) {

                    transactionService.Dumplog(transaction.getFilename()).execute();

                } else if (transaction.getCommand().equals("DISPLAY_SUMMARY")) {

                    transactionService.DisplaySummary(transaction.getUserId()).execute();
                }

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}
