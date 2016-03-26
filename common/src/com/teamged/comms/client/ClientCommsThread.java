package com.teamged.comms.client;

import com.teamged.comms.internal.CommsManager;

import java.util.concurrent.*;

/**
 * Created by DanielF on 2016-03-19.
 */
public class ClientCommsThread implements Runnable {
    private final ExecutorService threadPool;
    private final CompletionService<String> pool;
    private final String serverString;
    private final int serverPort;
    private final int poolSize;
    private int currentPoolSize;
    private boolean shutdown = false;

    public ClientCommsThread(String server, int port, int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.pool = new ExecutorCompletionService<>(this.threadPool);
        this.serverString = server;
        this.serverPort = port;
        this.poolSize = poolSize;
        this.currentPoolSize = 0;
    }

    public void shutdown() {
        CommsManager.CommsLogInfo("Shutting down client communications to server " + serverString + ":" + serverPort);
        shutdown = true;
        threadPool.shutdownNow();
    }

    @Override
    public void run() {
        CommsManager.CommsLogInfo("Initializing client communications with " + serverString + ":" + serverPort);
        while (!shutdown && !threadPool.isShutdown()) {
            while (currentPoolSize < poolSize) {
                CommsManager.CommsLogVerbose("Creating client communication request handler");
                pool.submit(new ClientCommsReqHandler(serverString, serverPort));
                currentPoolSize++;
            }

            try {
                CommsManager.CommsLogVerbose("Waiting for client communication request handler to exit");
                String retVal = pool.take().get();
                currentPoolSize--;
                CommsManager.CommsLogVerbose("Client communication request handler exited with message: " + retVal);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
