package com.teamged.txserver.database;

import com.teamged.txserver.InternalLog;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Created by DanielF on 2016-02-16.
 */
public class QuoteObject {
    private static final long QUOTE_LIFE_MILLIS = 59500;
    private static final long QUOTE_SHORT_LIFE_MILLIS = 750;
    private static final int QUOTE_STATEMENT_ARGS = 5;

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
        quoteString = args;

        if (args == null) {
            errorString = "Null argument";
        } else if (args.isEmpty()) {
            errorString = "Empty argument";
        } else {
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
                    quoteTime = Calendar.getInstance().getTimeInMillis();
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
