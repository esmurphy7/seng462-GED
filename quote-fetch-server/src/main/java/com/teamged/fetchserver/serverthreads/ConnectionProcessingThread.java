package com.teamged.fetchserver.serverthreads;

import com.teamged.comms.CommsInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DanielF on 2016-03-08.
 */
public class ConnectionProcessingThread extends FetchServerThread {
    private final ExecutorService pool;
    private final Object syncObject;
    private boolean running;

    public ConnectionProcessingThread(int poolSize, Object syncObject) {
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.syncObject = syncObject;
        this.running = true;
        System.out.println("Opened server connection processing thread");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        System.out.println("Server connection processing thread running");
        while (true) {
            pool.execute(new ConnectionProcessingHandler(CommsInterface.getNextServerRequest()));
        }
    }
}
