package com.teamged.auditserver;

import com.teamged.auditlogging.LogManager;
import com.teamged.auditserver.threads.AuditServerThread;
import com.teamged.auditserver.threads.LogConnectionThread;
import com.teamged.comms.CommsInterface;
import com.teamged.deployment.DeployParser;
import com.teamged.deployment.DeploymentSettings;

import java.util.ArrayList;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by DanielF on 2016-02-23.
 */
public class AuditMain {
    private static final Object dumpLockObject = new Object();
    private static final Object syncObject = new Object();
    private static final ArrayList<AuditServerThread> auditConnThreads = new ArrayList<>();
    private static final ArrayList<AuditServerThread> auditDumpThreads = new ArrayList<>();
    public static DeploymentSettings Deployment;
    private static String hostname;
    private static LongAdder userSequenceTotal = new LongAdder();
    private static boolean dumpReady = false;
    private static long expectedSequenceTotal = 0;

    public static void main(String[] args) {
        parseArgs(args);
        Deployment = DeployParser.parseConfig();
        if (Deployment != null) {
            runServer();
        }
        InternalLog.Log("Exiting audit server");
    }

    public static String getServerName() {
        return hostname;
    }

    /**
     * Updates the audit tracker with a workload sequence identifier. This will be used to verify when
     * a log dump can be performed on a workload test. This operates on the assumption that each sequence
     * identifier will only be called once (e.g. they are only called when a userCommand is logged)
     *
     * @param tid The workload sequence identifier to add to the tracker.
     */
    public static void updateSequenceId(long tid) {
        userSequenceTotal.add(tid);
    }

    /**
     * Sets a flag indicating that a log dump has been requested. Requires the expected workload sequence
     * identifier end count (e.g. the sequence number of the log dump command). Once this is set, the
     * log server will perform a log dump as soon as all sequence numbers prior to it have arrived. This
     * assumes that there will be no sequence numbers that follow the log dump identifier (i.e. if the
     * DUMPLOG command has ID 10000, then only 1-9999 should be user commands, and 10001+ should not exist).
     *
     * @param tid The workload sequence identifier of the DUMPLOG command.
     */
    public static void enableLogDumpRequest(long tid) {
        synchronized (dumpLockObject) {
            dumpReady = true;
            boolean tidOdd = tid % 2 != 0;
            long odd = tidOdd ? tid : tid + 1;
            long even = (tidOdd ? tid + 1 : tid) / 2;

            expectedSequenceTotal = odd * even;
        }
    }

    /**
     * Gets if a log dump request has been made.
     *
     * @return If a log dump request has been made.
     */
    public static boolean dumpIsQueued() {
        return dumpReady;
    }

    /**
     * Gets if a log dump request has been made and the user commands prior to the request have all
     * arrived.
     *
     * @return If the log dump can now be safely run for a workload test1.
     */
    public static boolean dumpIsReady() {
        boolean ready = dumpReady;
        if (ready) {
            synchronized (dumpLockObject) {
                long curr = userSequenceTotal.longValue();
                ready = curr >= expectedSequenceTotal;
                InternalLog.Log("Current: " + curr + "; Expected: " + expectedSequenceTotal);
            }
        }

        return ready;
    }

    /**
     * Performs a log dump if a request for one was made and the user commands prior to the log dump
     * request have all arrived.
     */
    public static void dumpIfReady() {
        synchronized (dumpLockObject) {
            if (dumpIsReady()) {
                dumpReady = false;
                expectedSequenceTotal = 0;
                userSequenceTotal.reset();
                InternalLog.Log("Beginning log dump!");
                LogManager.DumpLog();
                InternalLog.Log("Log dump ended!");
            }
        }
    }

    private static void parseArgs(String[] args) {
        if (args != null && args.length != 0) {
            hostname = args[0];
        }
    }

    private static void runServer() {
        InternalLog.Log("Launching audit server communications and processing handlers.");
        CommsInterface.startServerCommunications(Deployment.getAuditServer().getPort());
        AuditServerThread connThread = new LogConnectionThread(Deployment.getAuditServer().getInternals().getCommunicationThreads(), syncObject);
        auditConnThreads.add(connThread);
        new Thread(connThread).start();

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
