package org.seng462.webapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DanielF on 2016-04-03.
 */
public class TransactionResponse {
    private String username = "";
    private long userDollars = 0;
    private int userCents = 0;
    private long stockDollars = 0;
    private int stockCents = 0;
    private long setAsideDollars = 0;
    private int setAsideCents = 0;
    private long sellValueDollars = 0;
    private int sellValueCents = 0;
    private String stock = "";
    private int stockCount = 0;
    private int totalStockCount = 0;
    private List<String> history = new ArrayList<>();
    private List<String> dumplog = new ArrayList<>();
    private String errorMsg = "";

    private TransactionResponse(String data) {
        String[] vals = data.split(";", -1);
        if (vals.length != 15) {
            errorMsg = "ERROR: Response could not be parsed";
        } else {
            try {
                int idx = 0;
                username = vals[idx++];
                userDollars = Long.parseLong(vals[idx++]);
                userCents = Integer.parseInt(vals[idx++]);
                stockDollars = Long.parseLong(vals[idx++]);
                stockCents = Integer.parseInt(vals[idx++]);
                setAsideDollars = Long.parseLong(vals[idx++]);
                setAsideCents = Integer.parseInt(vals[idx++]);
                sellValueDollars = Long.parseLong(vals[idx++]);
                sellValueCents = Integer.parseInt(vals[idx++]);
                stock = vals[idx++];
                stockCount = Integer.parseInt(vals[idx++]);
                totalStockCount = Integer.parseInt(vals[idx++]);
                String[] histargs = vals[idx++].split("\\|", -1);
                for (String h : histargs) {
                    history.add(h);
                }
                String[] dumpargs = vals[idx++].split("\\|", -1);
                for (String d : dumpargs) {
                    dumplog.add(d);
                }
                errorMsg = vals[idx++];
            } catch (NumberFormatException nfe) {
                errorMsg = "ERROR: Number is response could not be parsed";
            }
        }
    }

    public static TransactionResponse fromDataMessage(String data) {
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

    public int getStockCount() {
        return stockCount;
    }

    public int getTotalStockCount() {
        return totalStockCount;
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

