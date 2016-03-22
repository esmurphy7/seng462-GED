package com.teamged.txserver.database;

import com.teamged.txserver.InternalLog;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Created by DanielF on 2016-02-16.
 */
public class QuoteObject {
    // If a user just wants a regular quote, then we just want it to be valid when sent back
    private static final long QUOTE_LIFE_MILLIS = 59500;

    // If a user wants to make a buy or sell, we want the system to have time to realize an update needs to be sent soon
    private static final long QUOTE_SHORT_LIFE_MILLIS = 55000;

    private static final long MILLIS_PADDING = 15;

    // Number of arguments the quote proxy server should return
    private static final int QUOTE_STATEMENT_ARGS = 6;

    private int dollars;
    private int cents;
    private BigDecimal price;
    private String stockSymbol;
    private String userName;
    private long quoteTime;
    private long quoteInternalTime;
    private long quoteShortTimeout;
    private long quoteTimeout;
    private String cryptoKey;
    private String errorString;
    private String quoteString;

    private QuoteObject() {
        dollars = 0;
        cents = 0;
        price = BigDecimal.ZERO;
        stockSymbol = "";
        userName = "";
        quoteTime = 0;
        quoteShortTimeout = 0;
        quoteTimeout = 0;
        cryptoKey = "";
        errorString = "";
        quoteString = "";
    }

    public static QuoteObject fromQuote(String quote) {
        QuoteObject qo = new QuoteObject();
        qo.parseArgs(quote);
        return qo;
    }

    public int getDollars() {
        return dollars;
    }

    public int getCents() {
        return cents;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public String getUserName() {
        return userName;
    }

    public long getQuoteTime() {
        return quoteTime;
    }

    public long getQuoteShortTimeout() {
        return quoteShortTimeout;
    }

    public long getQuoteInternalTime() {
        return quoteInternalTime;
    }

    public long getQuoteTimeout() {
        return quoteTimeout;
    }

    public String getCryptoKey() {
        return cryptoKey;
    }

    public String getErrorString() {
        return errorString;
    }

    @Override
    public String toString() {
        return quoteString;
    }

    private void parseArgs(String args) {
        boolean parsed = true;

        if (args == null) {
            errorString = "Null argument";
        } else if (args.isEmpty()) {
            errorString = "Empty argument";
            quoteString = "";
        } else {
            quoteString = args.substring(0, args.lastIndexOf(','));
            String[] argsArray = args.split(",");
            if (argsArray.length != QUOTE_STATEMENT_ARGS) {
                errorString = "Incorrect number of arguments in " + args;
                parsed = false;
            }

            if (parsed) {
                try {
                    String[] stockPrice = argsArray[0].split("\\.", 2);
                    dollars = Integer.parseInt(stockPrice[0]);
                    cents = Integer.parseInt(stockPrice[1]);
                    price = new BigDecimal(argsArray[0]);
                } catch (Exception e) {
                    errorString = "Unable to parse quote value: " + argsArray[0];
                    parsed = false;
                }
            }

            if (parsed) {
                try {
                    quoteInternalTime = Long.parseLong(argsArray[3]);
                    long quoteAgeTime = Long.parseLong(argsArray[5]);
                    quoteTime = Calendar.getInstance().getTimeInMillis() - quoteAgeTime - MILLIS_PADDING;
                    quoteTimeout = quoteTime + QUOTE_LIFE_MILLIS;
                    quoteShortTimeout = quoteTime + QUOTE_SHORT_LIFE_MILLIS;
                } catch (NumberFormatException e) {
                    errorString = "Unable to parse server time: " + argsArray[3];
                    parsed = false;
                }
            }

            if (parsed) {
                stockSymbol = argsArray[1];
                userName = argsArray[2];
                cryptoKey = argsArray[4];
                InternalLog.Log("Quote fetched for stock " + stockSymbol + " with value $" + price.toString());
            }
        }
    }
}
