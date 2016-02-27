package com.seng462ged.daytrader.workloadgenerator;

import org.jooq.lambda.Unchecked;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("You must specify a workload file");
            return;
        }

        String workloadFile = args[0];

        try {

            // Import transactions from file
            List<Transaction> transactions = Importer.ImportTransactions(workloadFile);

            // Group transactions by users
            Collection<List<Transaction>> transactionsByUser = transactions.stream()
                    .filter(transaction -> transaction.getUserId() != null)
                    .collect(Collectors.groupingBy(transaction -> transaction.getUserId()))
                    .values();

            // Create thread pool
            ExecutorService taskExecutor = Executors.newFixedThreadPool(10);

            // Concurrently send sets of transactions to the web server
            List<Future> results = transactionsByUser.stream()
                    .map(transactionSet -> taskExecutor.submit(() -> Requester.SendTransactions(transactionSet)))
                    .collect(Collectors.toList());

            // Print results (currently nothing - no return values)
            results.forEach(Unchecked.consumer(future -> System.out.println(future.get())));

        } catch (FileNotFoundException e) {

            System.out.println(String.format("Could not find file '%s'", workloadFile));

        } catch (IOException e) {

            System.out.println(String.format("There were problems reading file '%s'", workloadFile));
        }
    }
}
