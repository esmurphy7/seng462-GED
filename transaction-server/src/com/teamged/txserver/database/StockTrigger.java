package com.teamged.txserver.database;

/**
 * Created by DanielF on 2016-02-05.
 */
public class StockTrigger {
    private final StockRequest setAmount;
    private final int dollarsTrigger;
    private final int centsTrigger;

    public StockTrigger(StockRequest setAmount, int dollarsTrigger, int centsTrigger) {
        this.setAmount = setAmount;
        this.dollarsTrigger = dollarsTrigger;
        this.centsTrigger = centsTrigger;
    }

    public StockRequest getSetAmount() {
        return setAmount;
    }

    public int getDollarsTrigger() {
        return dollarsTrigger;
    }

    public int getCentsTrigger() {
        return centsTrigger;
    }
}
