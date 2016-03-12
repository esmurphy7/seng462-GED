package com.teamged.logging;

import com.teamged.deployment.deployments.AuditServerDeployment;
import com.teamged.logging.xmlelements.LogType;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Evan on 2/3/2016.
 *
 * Singleton class used to manage system logs.
 */
public class Logger
{
    private static AuditServerDeployment AUDIT_DEPLOY = null;
    private static Logger instance = null;
    private final BlockingQueue<LogType> logs;

    /**
     * Private constructor for the Logger singleton.
     */
    private Logger() {
        logs = new LinkedBlockingQueue<>();
        new Thread(new LogProcessingThread(logs)).start();
    }

    /**
     * Gets the Logger instance. Note that the log destination MUST BE SET before calling this, or it will always
     * return null.
     *
     * @return The Logger instance for logging to the audit server.
     */
    public static synchronized Logger getInstance()
    {
        if (instance == null && AUDIT_DEPLOY != null)
        {
            instance = new Logger();
        }
        return instance;
    }

    /**
     * Sets the audit server deployment configuration so that the Logger has a destination to send logs to. This
     * MUST BE SET before an instance of the Logger can be instantiated.
     *
     * @param deployment The audit server deployment configuration.
     */
    public static void SetLogDestination(AuditServerDeployment deployment) {
        AUDIT_DEPLOY = deployment;
    }

    /**
     * Gets the audit server deployment configuration.
     *
     * @return The deployment configuration.
     */
    public static AuditServerDeployment GetLogDestination() {
        return AUDIT_DEPLOY;
    }

    /**
     * Prepares a log for sending to the audit server.
     *
     * @param logInstance The log object.
     */
    public void Log(LogType logInstance) {
        try {
            logs.add(logInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Instructs the audit server to prepare a full dump log when ready.
     *
     * @param tid The sequence number of the dump log command. Used by the audit server to determine when the logs
     *            are ready to be saved.
     */
    public void SaveLog(int tid) {
        try (Socket s = new Socket(AUDIT_DEPLOY.getServer(), AUDIT_DEPLOY.getInternals().getDumpPort())) {
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.println("DUMPLOG," + tid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
