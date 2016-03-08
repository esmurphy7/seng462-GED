package com.teamged.logging;

import com.teamged.txserver.TxMain;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DanielF on 2016-03-03.
 */
public class LogProcessingThread implements Runnable {
    private final BlockingQueue<Object> logs;
    private final ExecutorService pool;

    public LogProcessingThread(BlockingQueue<Object> queue) {
        this.logs = queue;
        pool = Executors.newFixedThreadPool(TxMain.Deployment.getTransactionServers().getInternals().getCommunicationThreads());
    }

    @Override
    public void run() {
        try {
            while (true) {
                pool.execute(new LogProcessingHandler(logs.take()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
