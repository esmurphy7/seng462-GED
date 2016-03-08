package com.teamged.logging;

import com.teamged.deployment.deployments.AuditServerDeployment;
import com.teamged.fetchserver.FetchMain;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Evan on 2/3/2016.
 */
// Singleton class used to manage system logs
public class Logger
{
    public static final AuditServerDeployment AUDIT_DEPLOY = FetchMain.Deployment.getAuditServer();
    private static Logger instance = null;
    private final BlockingQueue<Object> logs;

    private Logger() {
        logs = new LinkedBlockingQueue<>();
        new Thread(new LogProcessingThread(logs)).start();
    }

    public static synchronized Logger getInstance()
    {
        if (instance == null)
        {
            instance = new Logger();
        }
        return instance;
    }

    // Marshall the log object and send it over the socket to the audit server
    public void Log(Object logInstance) {
        try {
            logs.add(logInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SaveLog(int tid) {
        System.out.println("Connecting: " + FetchMain.Deployment.getAuditServer().getServer());
        try (Socket s = new Socket(AUDIT_DEPLOY.getServer(), AUDIT_DEPLOY.getInternals().getDumpPort())) {
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.println("DUMPLOG," + tid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
