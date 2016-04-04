package com.seng462ged.daytrader.workloadgenerator;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Requester {

    public static void SendTransactions(String webServer, List<Transaction> transactions, boolean useSSL) {

        try {

            // Create custom OkHttpClient instance using its builder
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                    .readTimeout(200, TimeUnit.SECONDS)
                    .connectTimeout(200, TimeUnit.SECONDS);

            if (useSSL) {

                // Load SSL self signed certificate keystore
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

                try (FileInputStream inputStream = new FileInputStream("ssl/keystore")) {
                    keyStore.load(inputStream, "teamged".toCharArray());
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, "teamged".toCharArray());

                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(keyManagerFactory.getKeyManagers(),trustManagerFactory.getTrustManagers(), new SecureRandom());

                // Add factory to okHttpClientBuilder
                okHttpClientBuilder.sslSocketFactory(sslContext.getSocketFactory());
            }

            // Build OkHttpClient instance
            final OkHttpClient okHttpClient = okHttpClientBuilder.build();

            // Create Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(String.format("%s://%s/api/", useSSL ? "https" : "http", webServer))
                    .client(okHttpClient)
                    .build();

            TransactionService transactionService = retrofit.create(TransactionService.class);

            for (Transaction transaction : transactions) {

                System.out.println(String.format("[%d] User: %s [%d], Command: %s", transaction.getId(), transaction.getUserId(), transaction.getUserSequenceNumber(), transaction.getCommand()));

                try {
                    
                    Response<ResponseBody> response = null;

                    if (transaction.getCommand().equals("ADD")) {

                        response = transactionService.Add(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getAmount()).execute();

                    } else if (transaction.getCommand().equals("QUOTE")) {

                        response = transactionService.Quote(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getStockSymbol()).execute();

                    } else if (transaction.getCommand().equals("BUY")) {

                        response = transactionService.Buy(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                    } else if (transaction.getCommand().equals("COMMIT_BUY")) {

                        response = transactionService.CommitBuy(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId()).execute();

                    } else if (transaction.getCommand().equals("CANCEL_BUY")) {

                        response = transactionService.CancelBuy(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId()).execute();

                    } else if (transaction.getCommand().equals("SET_BUY_AMOUNT")) {

                        response = transactionService.SetBuyAmount(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                    } else if (transaction.getCommand().equals("SET_BUY_TRIGGER")) {

                        response = transactionService.SetBuyTrigger(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                    } else if (transaction.getCommand().equals("CANCEL_SET_BUY")) {

                        response = transactionService.CancelSetBuy(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getStockSymbol()).execute();

                    } else if (transaction.getCommand().equals("SELL")) {

                        response = transactionService.Sell(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                    } else if (transaction.getCommand().equals("COMMIT_SELL")) {

                        response = transactionService.CommitSell(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId()).execute();

                    } else if (transaction.getCommand().equals("CANCEL_SELL")) {

                        response = transactionService.CancelSell(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId()).execute();

                    } else if (transaction.getCommand().equals("SET_SELL_AMOUNT")) {

                        response = transactionService.SetSellAmount(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                    } else if (transaction.getCommand().equals("SET_SELL_TRIGGER")) {

                        response = transactionService.SetSellTrigger(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getStockSymbol(), transaction.getAmount()).execute();

                    } else if (transaction.getCommand().equals("CANCEL_SET_SELL")) {

                        response = transactionService.CancelSetSell(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getStockSymbol()).execute();

                    } else if (transaction.getCommand().equals("DUMPLOG") && transaction.getUserId() != null) {

                        response = transactionService.Dumplog(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId(), transaction.getFilename()).execute();

                    } else if (transaction.getCommand().equals("DUMPLOG")) {

                        response = transactionService.Dumplog(transaction.getId(), transaction.getFilename()).execute();

                    } else if (transaction.getCommand().equals("DISPLAY_SUMMARY")) {

                        response = transactionService.DisplaySummary(transaction.getId(), transaction.getUserSequenceNumber(), transaction.getUserId()).execute();
                    }

                    if (!response.isSuccess()) {

                        System.out.println("################### HTTP ERROR ###################");
                        System.out.println(response.errorBody().string());
                        System.out.println();
                    }

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
