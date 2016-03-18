package com.seng462ged.daytrader.workloadgenerator;

public class Transaction {

    private int id;
    private int userSequenceNumber;
    private String command;
    private String userId;
    private String stockSymbol;
    private String amount;
    private String filename;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserSequenceNumber() {
        return userSequenceNumber;
    }

    public void setUserSequenceNumber(int userSequenceNumber) {
        this.userSequenceNumber = userSequenceNumber;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
