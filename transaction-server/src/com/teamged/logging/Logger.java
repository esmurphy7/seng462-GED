package com.teamged.logging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Evan on 2/3/2016.
 */
// Singleton class used to manage system logs
public class Logger
{
    private static Logger instance = null;
    private final BlockingQueue<Object> logs;

    private static final String OUTPUT_LOGFILE = "outputLog.xml";

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
}
