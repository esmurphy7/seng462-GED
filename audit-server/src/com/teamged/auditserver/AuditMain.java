package com.teamged.auditserver;

import com.teamged.ServerConstants;
import com.teamged.auditserver.threads.AuditDumpThread;
import com.teamged.auditserver.threads.AuditQueueThread;
import com.teamged.auditserver.threads.LogConnectionThread;
import com.teamged.auditserver.threads.AuditServerThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by DanielF on 2016-02-23.
 */
public class AuditMain {
    private static int highestSeqNum = 0;
    private static Object dumpLockObject = new Object();
    private static Object syncObject = new Object();

    private static final ArrayList<AuditServerThread> auditConnThreads = new ArrayList<>();
    private static final ArrayList<AuditServerThread> auditDumpThreads = new ArrayList<>();
    private static final ArrayList<AuditServerThread> auditQueueThreads = new ArrayList<>();

    private static final BlockingQueue<String> auditQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        defineServerTopology(args);
        runServer();
        InternalLog.Log("Exiting audit server");
    }

    public static void updateHighestSequenceNumber(int number) {

    }

    public static void PutLogQueue(String log) {
        try {
            auditQueue.put(log);
        } catch (InterruptedException e) {
            e.printStackTrace();
            InternalLog.Log("Operation was interrupted: log will not be queued: " + log);
        } catch (NullPointerException e) {
            e.printStackTrace();
            InternalLog.Log("Attempted to add null log to log queue");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            InternalLog.Log("Log experienced an unexpected error while queueing: " + log);
        }
    }

    public static String TakeLogQueue() {
        String log = null;
        try {
            log = auditQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            InternalLog.Log("Operation was interrupted while attempting to dequeue a log");
        }

        return log;
    }

    private static void defineServerTopology(String[] args) {
        // TODO: Define server connections from config file or command line args
        if (args == null) {
            // Use default config file
        } else if (args.length == 1) {
            // Use arg as path to config file
        } else {
            // Use default config file (or resort to default values if it's unfindable
        }
    }

    private static void runServer() {
        InternalLog.Log("Launching audit server socket listeners.");
        for (int i = 0; i < ServerConstants.AUDIT_LOG_PORT_RANGE.length; i++) {
            AuditServerThread connThread = null;
            try {
                connThread = new LogConnectionThread(ServerConstants.AUDIT_LOG_PORT_RANGE[i], ServerConstants.THREAD_POOL_SIZE, syncObject);
            } catch (IOException e) {
                e.printStackTrace();
            }
            auditConnThreads.add(connThread);
            new Thread(connThread).start();
        }

        for (int i = 0; i < ServerConstants.PROCESSING_THREAD_COUNT; i++) {
            AuditServerThread queueThread = new AuditQueueThread(ServerConstants.THREAD_POOL_SIZE);
            auditQueueThreads.add(queueThread);
            new Thread(queueThread).start();
        }

        InternalLog.Log("Launching audit server dump thread.");
        AuditServerThread audThread = null;
        try {
            audThread = new AuditDumpThread(ServerConstants.AUDIT_DUMP_PORT, 1, syncObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        auditDumpThreads.add(audThread);
        new Thread(audThread).start();

        do {
            synchronized (syncObject) {
                try {
                    syncObject.wait();

                    /*
                    check thread statuses, restart threads if necessary
                     */
                } catch (InterruptedException e) {
                    // Close threads?
                    e.printStackTrace();
                    break;
                }
            }
        } while (!auditConnThreads.isEmpty());
    }
}
