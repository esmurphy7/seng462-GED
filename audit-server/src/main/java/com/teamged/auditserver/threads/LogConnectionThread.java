package com.teamged.auditserver.threads;

import com.teamged.auditserver.InternalLog;
import com.teamged.comms.CommsInterface;
import com.teamged.comms.ServerMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DanielF on 2016-02-23.
 */
public class LogConnectionThread extends AuditServerThread {
    private final ExecutorService pool;
    private final Object syncObject;
    private boolean running;

    public LogConnectionThread(int poolSize, Object syncObject) {
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.syncObject = syncObject;
        running = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        InternalLog.Log("Audit log message processing task is running");
        while (true) {
            ServerMessage serverMessage = CommsInterface.getNextServerRequest();
            if (serverMessage == null) {
                continue;
            }

            int flagVal = serverMessage.getFlags();
            if (flagVal == 0) {
                // Regular log
                pool.execute(new LogConnectionHandler(serverMessage.getData()));
            } else if (flagVal == 1) {
                // Dumplog command
                pool.execute(new LogDumpHandler(serverMessage.getData()));
            } else {
                // Unknown
                serverMessage.setResponse("FLAG ERROR");
            }
        }
    }
}
