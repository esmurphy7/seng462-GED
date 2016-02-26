package com.seng462ged.daytrader.workloadgenerator;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TransactionService {

    @POST("trade/command/add")
    Call<Void> Add(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("amount") String amount);

    @GET("trade/command/quote")
    Call<Void> Quote(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol);

    @POST("trade/command/buy")
    Call<Void> Buy(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/buy/commit")
    Call<Void> CommitBuy(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId);

    @POST("trade/command/buy/cancel")
    Call<Void> CancelBuy(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId);

    @POST("trade/command/buy/trigger/amount")
    Call<Void> SetBuyAmount(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/buy/trigger/stockprice")
    Call<Void> SetBuyTrigger(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/buy/trigger/cancel")
    Call<Void> CancelSetBuy(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol);

    @POST("trade/command/sell")
    Call<Void> Sell(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/sell/commit")
    Call<Void> CommitSell(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId);

    @POST("trade/command/sell/cancel")
    Call<Void> CancelSell(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId);

    @POST("trade/command/sell/trigger/amount")
    Call<Void> SetSellAmount(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/sell/trigger/stockprice")
    Call<Void> SetSellTrigger(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/sell/trigger/cancel")
    Call<Void> CancelSetSell(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol);

    @GET("trade/command/dumplog")
    Call<Void> Dumplog(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("filename") String filename);

    @GET("trade/command/dumplog")
    Call<Void> Dumplog(@Query("globalSequence") int globalSequence, @Query("filename") String filename);

    @GET("trade/command/summary")
    Call<Void> DisplaySummary(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId);
}
