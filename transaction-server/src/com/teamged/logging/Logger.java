package com.teamged.logging;

import com.teamged.ServerConstants;

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
        System.out.println("Connecting: " + ServerConstants.AUDIT_SERVERS[0]);
        try (Socket s = new Socket(ServerConstants.AUDIT_SERVERS[0], ServerConstants.AUDIT_DUMP_PORT)) {
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.println("DUMPLOG," + tid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
