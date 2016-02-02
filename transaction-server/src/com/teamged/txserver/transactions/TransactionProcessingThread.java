package com.teamged.txserver.transactions;

import com.teamged.txserver.TransactionMonitor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by danie on 2016-02-01.
 */
public class TransactionProcessingThread extends TransactionServerThread {
    private final ExecutorService pool;
    private final Object syncObject;
    private boolean running;

    public TransactionProcessingThread(int poolSize, Object syncObject) {
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.syncObject = syncObject;
        running = true;
        System.out.println("Transaction queue processor launched");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        System.out.println("Transaction queue processor running");
        try {
            while (true) {
                pool.execute(new TransactionProcessingHandler(TransactionMonitor.TakeRequestQueue()));
            }
        } catch (Exception e) {
            running = false;
            e.printStackTrace();
            pool.shutdown();
            synchronized (syncObject) {
                syncObject.notify();
            }
        }
    }
}
