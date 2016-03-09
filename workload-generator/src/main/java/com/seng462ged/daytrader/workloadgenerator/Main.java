package com.seng462ged.daytrader.workloadgenerator;

import com.teamged.deployment.DeployParser;
import com.teamged.deployment.DeploymentSettings;
import com.teamged.deployment.deployments.WebLoadBalancerDeployment;
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

        // Default web server
        String serverAddress = "localhost";
        Integer serverPort = 8080;

        // Get load balancer address and port from config file if they exists
        DeploymentSettings deploymentSettings = DeployParser.parseConfig("config.json");
        WebLoadBalancerDeployment loadBalancerDeployment = deploymentSettings.getWebLoadBalancer();

        if (loadBalancerDeployment != null) {

            if (loadBalancerDeployment.getServer() != null) {
                serverAddress = loadBalancerDeployment.getServer();
            }

            if (loadBalancerDeployment.getPort() != null) {
                serverPort = loadBalancerDeployment.getPort();
            }
        }

        // If user manually entered a web server, use that
        if (args.length == 2) {

            String webServer = args[1];
            String[] addressPortSplit = webServer.split(":");

            serverAddress = addressPortSplit[0];
            serverPort = Integer.valueOf(addressPortSplit[1]);
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

            String webServer = String.format("%s:%d", serverAddress, serverPort);

            // Concurrently send sets of transactions to the web server
            List<Future> results = transactionsByUser.stream()
                    .map(transactionSet -> taskExecutor.submit(() -> Requester.SendTransactions(webServer, transactionSet)))
                    .collect(Collectors.toList());

            // Wait for all threads to finish
            results.forEach(Unchecked.consumer(future -> future.get()));

            // Manually run dumplog transaction at the end
            Requester.SendTransactions(webServer, dumplogTransactions);

            taskExecutor.shutdown();

        } catch (FileNotFoundException e) {

            System.out.println(String.format("Could not find file '%s'", workloadFile));

        } catch (IOException e) {

            System.out.println(String.format("There were problems reading file '%s'", workloadFile));
        }
    }
}
