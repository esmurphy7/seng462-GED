package org.seng462.webapp.logging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Evan on 2/3/2016.
 */
// Singleton class used to manage system logs
public class Logger
{
    private static Logger instance = null;
    //private final BlockingQueue<Object> logs;

    private Logger() {
        //logs = new LinkedBlockingQueue<>();
        //new Thread(new LogProcessingThread(logs)).start();
    }

    public static synchronized Logger getInstance()
    {
        if (instance == null)
        {
            instance = new Logger();
        }
        return instance;
    }

    // Store a log in a list
    public void Log(Object logInstance) {
        try {
            //logs.add(logInstance);
            new Thread(new LogProcessingHandler(logInstance)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
