package com.seng462ged.daytrader.workloadgenerator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("You must specify a workload file");
        }

        String workloadFile = args[0];

        try {

            List<Transaction> transactions = Importer.ImportTransactions(workloadFile);

            for (Transaction transaction : transactions) {

                System.out.println(transaction.getId());
                System.out.println(transaction.getCommand());
                System.out.println(transaction.getUserId());
                System.out.println(transaction.getStockSymbol());
                System.out.println(transaction.getAmount());
                System.out.println(transaction.getFilename());
                System.out.println();
            }

            //List<Transaction> transactions2 = new ArrayList<Transaction>();
            //transactions2.add(transactions.get(0));

            Requester.SendTransactions(transactions);

        } catch (FileNotFoundException e) {
            System.out.println(String.format("Could not find file '%s'", workloadFile));
        } catch (IOException e) {
            System.out.println(String.format("There were problems reading file '%s'", workloadFile));
        }
    }
}
