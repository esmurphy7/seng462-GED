package com.seng462ged.daytrader.workloadgenerator;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TransactionService {

    @POST("trade/command/add")
    Call<Void> Add(@Query("userId") String userId, @Query("amount") String amount);

    @GET("trade/command/quote")
    Call<Void> Quote(@Query("userId") String userId, @Query("stockSymbol") String stockSymbol);

    @POST("trade/command/buy")
    Call<Void> Buy(@Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/buy/commit")
    Call<Void> CommitBuy(@Query("userId") String userId);

    @POST("trade/command/buy/cancel")
    Call<Void> CancelBuy(@Query("userId") String userId);

    @POST("trade/command/buy/trigger/amount")
    Call<Void> SetBuyAmount(@Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/buy/trigger/stockprice")
    Call<Void> SetBuyTrigger(@Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/buy/trigger/cancel")
    Call<Void> CancelSetBuy(@Query("userId") String userId, @Query("stockSymbol") String stockSymbol);

    @POST("trade/command/sell")
    Call<Void> Sell(@Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/sell/commit")
    Call<Void> CommitSell(@Query("userId") String userId);

    @POST("trade/command/sell/cancel")
    Call<Void> CancelSell(@Query("userId") String userId);

    @POST("trade/command/sell/trigger/amount")
    Call<Void> SetSellAmount(@Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/sell/trigger/stockprice")
    Call<Void> SetSellTrigger(@Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/sell/trigger/cancel")
    Call<Void> CancelSetSell(@Query("userId") String userId, @Query("stockSymbol") String stockSymbol);

    @GET("trade/command/dumplog")
    Call<Void> Dumplog(@Query("userId") String userId, @Query("filename") String filename);

    @GET("trade/command/dumplog")
    Call<Void> Dumplog(@Query("filename") String filename);

    @GET("trade/command/summary")
    Call<Void> DisplaySummary(@Query("userId") String userId);
}
