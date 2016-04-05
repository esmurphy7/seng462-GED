package org.seng462.webapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DanielF on 2016-04-03.
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
    private List<String> history;
    private List<String> dumplog;
    private String errorMsg;

    private TransactionResponse(String data) {
        // TODO: parse values
        username = "test";
        userDollars = 0;
        userCents = 0;
        stockDollars = 9001;
        stockCents = 0;
        setAsideDollars = 0;
        setAsideCents = 0;
        sellValueDollars = 0;
        sellValueCents = 0;
        stock = "MEH";
        errorMsg = "";
        history = new ArrayList<>();
        dumplog = new ArrayList<>();
    }

    static TransactionResponse fromDataMessage(String data) {
        return new TransactionResponse(data);
    }

    public String getUsername() {
        return username;
    }

    public long getUserDollars() {
        return userDollars;
    }

    public int getUserCents() {
        return userCents;
    }

    public long getStockDollars() {
        return stockDollars;
    }

    public int getStockCents() {
        return stockCents;
    }

    public long getSetAsideDollars() {
        return setAsideDollars;
    }

    public int getSetAsideCents() {
        return setAsideCents;
    }

    public long getSellValueDollars() {
        return sellValueDollars;
    }

    public int getSellValueCents() {
        return sellValueCents;
    }

    public String getStock() {
        return stock;
    }

    public List<String> getHistory() {
        return history;
    }

    public List<String> getDumplog() {
        return dumplog;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean hasError() {
        return errorMsg != null && !errorMsg.isEmpty();
    }
}

