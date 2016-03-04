package com.teamged.auditserver.threads;

import com.teamged.auditserver.InternalLog;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DanielF on 2016-02-23.
 */
public class LogConnectionThread extends AuditServerThread {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private final Object syncObject;
    private boolean running;

    public LogConnectionThread(int port, int poolSize, Object syncObject) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.syncObject = syncObject;
        running = true;
        InternalLog.Log("Opened audit log socket on port " + port);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        InternalLog.Log("Audit log listener running on port " + serverSocket.getLocalPort());
        try {
            while (true) {
                pool.execute(new LogConnectionHandler(serverSocket.accept()));
            }
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
