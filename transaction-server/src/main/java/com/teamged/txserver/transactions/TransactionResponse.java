package com.teamged.txserver.transactions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DanielF on 2016-04-04.
 */
public class TransactionResponse {
    private String username;
    private long userDollars;
    private int userCents;
    private long stockDollars;
    private int stockCents;
    private long setAsideDollars;
    private int setAsideCents;
    private long sellValueDollars;
    private int sellValueCents;
    private String stock;
    private int stockCount;
    private int totalStockCount;
    private List<String> history;
    private List<String> dumplog;
    private String errorMsg;

    public TransactionResponse() {
        username = "";
        userDollars = 0;
        userCents = 0;
        stockDollars = 0;
        stockCents = 0;
        setAsideDollars = 0;
        setAsideCents = 0;
        sellValueDollars = 0;
        sellValueCents = 0;
        stock = "";
        stockCount = 0;
        totalStockCount = 0;
        errorMsg = "";
        history = new ArrayList<>();
        dumplog = new ArrayList<>();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserDollars(long userDollars) {
        this.userDollars = userDollars;
    }

    public void setUserCents(int userCents) {
        this.userCents = userCents;
    }

    public void setStockDollars(long stockDollars) {
        this.stockDollars = stockDollars;
    }

    public void setStockCents(int stockCents) {
        this.stockCents = stockCents;
    }

    public void setSetAsideDollars(long setAsideDollars) {
        this.setAsideDollars = setAsideDollars;
    }

    public void setSetAsideCents(int setAsideCents) {
        this.setAsideCents = setAsideCents;
    }

    public void setSellValueDollars(long sellValueDollars) {
        this.sellValueDollars = sellValueDollars;
    }

    public void setSellValueCents(int sellValueCents) {
        this.sellValueCents = sellValueCents;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public void setTotalStockCount(int totalStockCount) {
        this.totalStockCount = totalStockCount;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }

    public void setDumplog(List<String> dumplog) {
        this.dumplog = dumplog;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(username);
        sb.append(";");
        sb.append(userDollars);
        sb.append(";");
        sb.append(userCents);
        sb.append(";");
        sb.append(stockDollars);
        sb.append(";");
        sb.append(stockCents);
        sb.append(";");
        sb.append(setAsideDollars);
        sb.append(";");
        sb.append(setAsideCents);
        sb.append(";");
        sb.append(sellValueDollars);
        sb.append(";");
        sb.append(sellValueCents);
        sb.append(";");
        sb.append(stock);
        sb.append(";");
        sb.append(stockCount);
        sb.append(";");
        sb.append(totalStockCount);
        sb.append(";");
        sb.append(String.join(",", history));
        sb.append(";");
        sb.append(String.join(",", dumplog));
        sb.append(";");
        sb.append(errorMsg);
        return sb.toString();
    }
}
