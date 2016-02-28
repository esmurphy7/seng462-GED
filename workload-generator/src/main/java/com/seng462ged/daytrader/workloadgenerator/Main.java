package com.seng462ged.daytrader.workloadgenerator;

import org.jooq.lambda.Unchecked;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("You must specify a workload file");
            return;
        }

        String workloadFile = args[0];

        // Default web server is localhost
        String webServer = "localhost:8080";

        if (args.length == 2) {
            webServer = args[1];
        }

        try {

            // Import transactions from file
            List<Transaction> transactions = Importer.ImportTransactions(workloadFile);

            // Group transactions by users
            Collection<List<Transaction>> transactionsByUser = transactions.stream()
                    .filter(transaction -> transaction.getUserId() != null)
                    .collect(Collectors.groupingBy(transaction -> transaction.getUserId()))
                    .values();

            // Find dumplog transactions with no user id (normally only 1 at the end)
            List<Transaction> dumplogTransactions = transactions.stream()
                    .filter(transaction -> transaction.getUserId() == null)
                    .filter(transaction -> transaction.getCommand().equals("DUMPLOG"))
                    .collect(Collectors.toList());

            // Create thread pool
            ExecutorService taskExecutor = Executors.newFixedThreadPool(10);

            final String tempWebServer = webServer;

            // Concurrently send sets of transactions to the web server
            List<Future> results = transactionsByUser.stream()
                    .map(transactionSet -> taskExecutor.submit(() -> Requester.SendTransactions(tempWebServer, transactionSet)))
                    .collect(Collectors.toList());

            // Wait for all threads to finish
            results.forEach(Unchecked.consumer(future -> future.get()));

            // Manually run dumplog transaction at the end
            Requester.SendTransactions(tempWebServer, dumplogTransactions);

            taskExecutor.shutdown();

        } catch (FileNotFoundException e) {

            System.out.println(String.format("Could not find file '%s'", workloadFile));

        } catch (IOException e) {

            System.out.println(String.format("There were problems reading file '%s'", workloadFile));
        }
    }
}
