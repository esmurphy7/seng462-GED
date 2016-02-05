package com.teamged.txserver.database;

import com.teamged.ServerConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by DanielF on 2016-02-02.
 */
public class UserDatabaseObject {
    private static final int CENT_CAP = 100;

    private final Object lock = new Object();
    private final ArrayList<String> history = new ArrayList<>();
    private final ArrayList<String> sellList = new ArrayList<>();
    private final ArrayList<String> buyList = new ArrayList<>();
    private final String userName;
    private int dollars = 0;
    private int cents = 0;

    public UserDatabaseObject(String user) {
        userName = user;
    }

    public String add(int dollars, int cents) {
        synchronized (lock) {
            this.dollars += dollars;
            this.cents += cents;
            if (this.cents >= CENT_CAP) {
                this.cents -= CENT_CAP;
                this.dollars++;
            }
            history.add("ADD," + dollars + "." + cents);
            // TODO: Update database
        }

        return userName + ", " + this.dollars + "." + this.cents;
    }

    /**
     * Gets a raw quote string from the request server.
     *
     * @param stock
     * @return
     */
    public String quote(String stock) {
        String quote;
        synchronized (lock) {
            try (
                    Socket quoteSocket = new Socket(ServerConstants.QUOTE_SERVER, ServerConstants.QUOTE_PORT);
                    PrintWriter out = new PrintWriter(quoteSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(quoteSocket.getInputStream()))
            ) {
                out.println(stock + "," + userName);
                quote = in.readLine();
                history.add("QUOTE," + stock);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                quote = "QUOTE ERROR";
            } catch (IOException e) {
                e.printStackTrace();
                quote = "QUOTE ERROR";
            }
        }

        return quote;
    }

    public String buy(String stock, int dollars, int cents) {
        String buyRes;
        synchronized (lock) {
            if (dollars > this.dollars || (dollars == this.dollars && cents >= this.cents)) {
                String quote = quote(stock);
                buyRes = "";
                // Verify stock name
                // Verify the user name
                // Get the cost per share
                // Convert cost to single (long) number (d * 100 + c)
                // Convert buy money to single (long) number
                // Find maximum number of times cost goes into buy money
                // Spending amount equals above # times
                // Subtract that amount (in dollars and cents) from user money
                // Add the buy request to an array
                // Launch a 60 second buy cleanup timer
                // Return response
            } else {
                buyRes = "BUY ERROR," + userName + ", " + this.dollars + "." + this.cents;
            }
        }

        return buyRes;
    }
}
