package com.teamged.auditserver.threads;

import com.teamged.auditserver.AuditMain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DanielF on 2016-03-03.
 */
public class AuditQueueThread extends AuditServerThread {
    private final ExecutorService pool;
    private boolean running = false;

    public AuditQueueThread(int poolSize) {
        pool = Executors.newFixedThreadPool(poolSize);
        running = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        while (true) {
            pool.execute(new AuditQueueHandler(AuditMain.TakeLogQueue()));
        }
    }
}
