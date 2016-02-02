package com.teamged.txserver.database;

import com.teamged.ServerConstants;
import com.teamged.txserver.transactions.TransactionObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by DanielF on 2016-02-02.
 */
public class DataProxy {
    public static String dbOperation(TransactionObject tx) {
        String opResult;
        switch (tx.getUserCommand()) {
            case NO_COMMAND:
                opResult = "NO_COMMAND was given";
                break;
            case ADD:
                opResult = addOperation(tx.getUserName(), tx.getAmountDollars(), tx.getAmountCents());
                break;
            case QUOTE:
                opResult = quoteOperation(tx.getUserName(), tx.getStockSymbol());
                break;
            case BUY:
                opResult = buyOperation(tx.getUserName(), tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                break;
            case COMMIT_BUY:
                opResult = commitBuyOperation(tx.getUserName());
                break;
            case CANCEL_BUY:
                opResult = cancelBuyOperation(tx.getUserName());
                break;
            case SELL:
                opResult = sellOperation(tx.getUserName(), tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                break;
            case COMMIT_SELL:
                opResult = commitSellOperation(tx.getUserName());
                break;
            case CANCEL_SELL:
                opResult = cancelSellOperation(tx.getUserName());
                break;
            case SET_BUY_AMOUNT:
                opResult = setBuyAmountOperation(tx.getUserName(), tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                break;
            case CANCEL_SET_BUY:
                opResult = cancelSetBuyOperation(tx.getUserName(), tx.getStockSymbol());
                break;
            case SET_BUY_TRIGGER:
                opResult = setBuyTriggerOperation(tx.getUserName(), tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                break;
            case SET_SELL_AMOUNT:
                opResult = setSellAmountOperation(tx.getUserName(), tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                break;
            case SET_SELL_TRIGGER:
                opResult = setSellTriggerOperation(tx.getUserName(), tx.getStockSymbol(), tx.getAmountDollars(), tx.getAmountCents());
                break;
            case CANCEL_SET_SELL:
                opResult = cancelSetSellOperation(tx.getUserName(), tx.getStockSymbol());
                break;
            case DUMPLOG:
                opResult = dumpLogOperation(tx.getUserName(), tx.getFileName());
                break;
            case DUMPLOG_ROOT:
                opResult = dumpLogRootOperation(tx.getFileName());
                break;
            case DISPLAY_SUMMARY:
                opResult = displaySummaryOperation(tx.getUserName());
                break;
            default:
                opResult = "Unknown command \"" + tx.getUserCommand().toString() + "\" was given";
                break;
        }

        // TODO: Some additional processing and parsing of the result will be needed.
        return "[DATA PROXY RESULT]" + opResult;
    }

    private static String addOperation(String name, int dollars, int cents) {
        return "ADD was given for " + name + ", for $" + dollars + "." + cents;
    }

    private static String quoteOperation(String name, String stock) {
        String resp = "QUOTE was given: ";
        try (
            Socket quoteSocket = new Socket(ServerConstants.QUOTE_SERVER, ServerConstants.QUOTE_PORT);
            PrintWriter out = new PrintWriter(quoteSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
        ){
            out.println(name + "," + stock);
            resp += in.readLine();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            resp += "quote server was not found";
        } catch (IOException e) {
            e.printStackTrace();
            resp += "quote server was uncommunicative";
        }

        return resp;
    }

    private static String buyOperation(String name, String stock, int dollars, int cents) {
        String resp = "BUY was given for " + name + ", for $" + dollars + "." + cents + " of " + stock;
        try (
            Socket quoteSocket = new Socket(ServerConstants.QUOTE_SERVER, ServerConstants.QUOTE_PORT);
            PrintWriter out = new PrintWriter(quoteSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
        ){
            out.println(name + "," + stock);
            resp += ": " + in.readLine();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            resp += ". But quote server was not found";
        } catch (IOException e) {
            e.printStackTrace();
            resp += ". But quote server was uncommunicative";
        }

        return resp;
    }

    private static String commitBuyOperation(String name) {
        return "COMMIT_BUY was given for " + name;
    }

    private static String cancelBuyOperation(String name) {
        return "CANCEL_BUY was given for " + name;
    }

    private static String sellOperation(String name, String stock, int dollars, int cents) {
        String resp = "SELL was given for " + name + ", for $" + dollars + "." + cents + " of " + stock;
        try (
            Socket quoteSocket = new Socket(ServerConstants.QUOTE_SERVER, ServerConstants.QUOTE_PORT);
            PrintWriter out = new PrintWriter(quoteSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
        ){
            out.println(name + "," + stock);
            resp += ": " + in.readLine();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            resp += ". But quote server was not found";
        } catch (IOException e) {
            e.printStackTrace();
            resp += ". But quote server was uncommunicative";
        }

        return resp;
    }

    private static String commitSellOperation(String name) {
        return "COMMIT_SELL was given for " + name;
    }

    private static String cancelSellOperation(String name) {
        return "CANCEL_SELL was given for " + name;
    }

    private static String setBuyAmountOperation(String name, String stock, int dollars, int cents) {
        return "SET_BUY_AMOUNT was given for " + name + ", for $" + dollars + "." + cents + " of " + stock;
    }

    private static String cancelSetBuyOperation(String name, String stock) {
        return "CANCEL_SET_BUY was given for " + name + " for stock " + stock;
    }

    private static String setBuyTriggerOperation(String name, String stock, int dollars, int cents) {
        return "SET_BUY_TRIGGER was given for " + name + ", for $" + dollars + "." + cents + " of " + stock;
    }

    private static String setSellAmountOperation(String name, String stock, int dollars, int cents) {
        return "SET_SELL_AMOUNT was given for " + name + ", for $" + dollars + "." + cents + " of " + stock;
    }

    private static String setSellTriggerOperation(String name, String stock, int dollars, int cents) {
        return "SET_SELL_TRIGGER was given for " + name + ", for $" + dollars + "." + cents + " of " + stock;
    }

    private static String cancelSetSellOperation(String name, String stock) {
        return "CANCEL_SET_SELL was given for " + name + " for stock " + stock;
    }

    private static String dumpLogOperation(String name, String fname) {
        return "DUMP_LOG was given for " + name + " to file " + fname;
    }

    private static String dumpLogRootOperation(String fname) {
        return "DUMP_LOG was given for full dump to file " + fname;
    }

    private static String displaySummaryOperation(String name) {
        return "DISPLAY_SUMMARY was given for " + name;
    }
}
