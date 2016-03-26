package com.teamged.txserver.database;

import org.mongodb.morphia.annotations.Embedded;

@Embedded
public class StockRequest {

    private final String stock;
    private final int shares;
    private final int dollars;
    private final int cents;
    private final long expiry;

    public StockRequest(String stock, int shares, int dollars, int cents, long expiry) {
        this.stock = stock;
        this.shares = shares;
        this.dollars = dollars;
        this.cents = cents;
        this.expiry = expiry;
    }

    public String getStock() {
        return stock;
    }

    public int getShares() {
        return shares;
    }

    public int getDollars() {
        return dollars;
    }

    public int getCents() {
        return cents;
    }

    public long getExpiry() {
        return expiry;
    }
}
