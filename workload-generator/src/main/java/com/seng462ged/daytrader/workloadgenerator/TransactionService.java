package com.seng462ged.daytrader.workloadgenerator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TransactionService {

    @POST("trade/command/add")
    Call<ResponseBody> Add(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("amount") String amount);

    @GET("trade/command/quote")
    Call<ResponseBody> Quote(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol);

    @POST("trade/command/buy")
    Call<ResponseBody> Buy(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/buy/commit")
    Call<ResponseBody> CommitBuy(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId);

    @POST("trade/command/buy/cancel")
    Call<ResponseBody> CancelBuy(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId);

    @POST("trade/command/buy/trigger/amount")
    Call<ResponseBody> SetBuyAmount(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/buy/trigger/stockprice")
    Call<ResponseBody> SetBuyTrigger(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/buy/trigger/cancel")
    Call<ResponseBody> CancelSetBuy(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol);

    @POST("trade/command/sell")
    Call<ResponseBody> Sell(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/sell/commit")
    Call<ResponseBody> CommitSell(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId);

    @POST("trade/command/sell/cancel")
    Call<ResponseBody> CancelSell(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId);

    @POST("trade/command/sell/trigger/amount")
    Call<ResponseBody> SetSellAmount(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/sell/trigger/stockprice")
    Call<ResponseBody> SetSellTrigger(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol, @Query("amount") String amount);

    @POST("trade/command/sell/trigger/cancel")
    Call<ResponseBody> CancelSetSell(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("stockSymbol") String stockSymbol);

    @GET("trade/command/dumplog")
    Call<ResponseBody> Dumplog(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId, @Query("filename") String filename);

    @GET("trade/command/dumplog")
    Call<ResponseBody> Dumplog(@Query("globalSequence") int globalSequence, @Query("filename") String filename);

    @GET("trade/command/summary")
    Call<ResponseBody> DisplaySummary(@Query("globalSequence") int globalSequence, @Query("userSequence") int userSequence, @Query("userId") String userId);
}
