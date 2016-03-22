package com.teamged.txserver.transactions;

import com.teamged.comms.CommsInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DanielF on 2016-01-31.
 */
public class RequestProcessingThread extends TransactionServerThread {
    private final ExecutorService pool;
    private final Object syncObject;
    private boolean running;

    /**
     * @param poolSize
     * @param syncObject
     * @throws IOException
     */
    public RequestProcessingThread(int poolSize, Object syncObject) {
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.syncObject = syncObject;
        this.running = true;
    }

    /**
     *
     */
    @Override
    public void run() {
        System.out.println("Transaction message processing task running");
        while (true) {
            pool.execute(new RequestProcessingHandler(CommsInterface.getNextServerRequest()));
        }
    }

    /**
     * @return
     */
    public boolean isRunning() {
        return running;
    }
}
