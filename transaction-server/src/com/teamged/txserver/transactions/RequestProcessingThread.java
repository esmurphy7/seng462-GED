package com.teamged.txserver.transactions;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DanielF on 2016-01-31.
 */
public class RequestProcessingThread extends TransactionServerThread {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private final Object syncObject;
    private boolean running;

    /**
     * @param port
     * @param poolSize
     * @param syncObject
     * @throws IOException
     */
    public RequestProcessingThread(int port, int poolSize, Object syncObject) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.syncObject = syncObject;
        this.running = true;
        System.out.println("Opened server socket on port " + port);
    }

    /**
     *
     */
    @Override
    public void run() {
        System.out.println("Thread task running on port " + serverSocket.getLocalPort());
        try {
            while (true) {
                pool.execute(new RequestProcessingHandler(serverSocket.accept()));
            }
        } catch (IOException e) {
            running = false;
            e.printStackTrace();
            System.out.println("RequestProcessingThread encountered error while connecting with client. Shutting down.");
            pool.shutdown();
            synchronized (syncObject) {
                syncObject.notify();
            }
        }
    }

    /**
     * @return
     */
    public boolean isRunning() {
        return running;
    }
}
