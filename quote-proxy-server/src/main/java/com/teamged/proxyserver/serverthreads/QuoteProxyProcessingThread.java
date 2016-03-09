package com.teamged.proxyserver.serverthreads;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DanielF on 2016-03-09.
 */
public class QuoteProxyProcessingThread extends ProxyServerThread {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private final Object syncObject;
    private boolean running;

    public QuoteProxyProcessingThread(int port, int poolSize, Object syncObject) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.syncObject = syncObject;
        this.running = true;
        System.out.println("Opened server socket on port " + port);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        System.out.println("Thread quote proxy processing task running on port " + serverSocket.getLocalPort());
        try {
            while (true) {
                pool.execute(new QuoteProxyProcessingHandler(serverSocket.accept()));
            }
        } catch (IOException e) {
            running = false;
            e.printStackTrace();
            System.out.println("QuoteProxyProcessingThread encountered an error while connecting. Shutting down.");
            pool.shutdown();
            synchronized (syncObject) {
                syncObject.notify();
            }
        }
    }
}
