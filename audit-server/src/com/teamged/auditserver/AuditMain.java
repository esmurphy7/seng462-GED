package com.teamged.auditserver;

import com.teamged.ServerConstants;
import com.teamged.auditserver.threads.AuditDumpThread;
import com.teamged.auditserver.threads.AuditProcessingHandler;
import com.teamged.auditserver.threads.AuditProcessingThread;
import com.teamged.auditserver.threads.AuditServerThread;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DanielF on 2016-02-23.
 */
public class AuditMain {

    private static final int DUMP_TOKEN = 0xF4F3F2F1;

    private static Object syncObject = new Object();
    private static final ArrayList<AuditServerThread> auditProcThreads = new ArrayList<>();
    private static final ArrayList<AuditServerThread> auditDumpThreads = new ArrayList<>();

    public static void main(String[] args) {
        defineServerTopology(args);
        runServer();
        InternalLog.Log("Exiting audit server");
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
            AuditServerThread audThread = null;
            try {
                audThread = new AuditProcessingThread(ServerConstants.AUDIT_LOG_PORT_RANGE[i], ServerConstants.THREAD_POOL_SIZE, syncObject);
            } catch (IOException e) {
                e.printStackTrace();
            }

            auditProcThreads.add(audThread);
            new Thread(audThread).start();

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
    }
}
