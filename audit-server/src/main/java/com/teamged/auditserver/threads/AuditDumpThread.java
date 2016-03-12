package com.teamged.auditserver.threads;

import com.teamged.auditserver.InternalLog;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DanielF on 2016-02-23.
 */
public class AuditDumpThread extends AuditServerThread
{
    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private final Object syncObject;
    private boolean running;

    public AuditDumpThread(int port, int poolSize, Object syncObject) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.syncObject = syncObject;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void run() {
        InternalLog.Log("Audit dump thread running...");
        while (true) {
            try {
                pool.execute(new AuditDumpHandler(serverSocket.accept()));
            } catch (IOException e) {
                running = false;
                e.printStackTrace();
                InternalLog.Log(e.getMessage());
                pool.shutdown();
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }
        }
    }
}
