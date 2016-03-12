package com.teamged.logging;

import com.teamged.logging.xmlelements.LogType;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by DanielF on 2016-03-03.
 */
public class LogProcessingThread implements Runnable {
    private final BlockingQueue<LogType> logs;
    private final ExecutorService pool;

    public LogProcessingThread(BlockingQueue<LogType> queue) {
        this.logs = queue;
        pool = Executors.newFixedThreadPool(Logger.GetLogDestination().getInternals().getCommunicationThreads());
    }

    @Override
    public void run() {
        try {
            while (true) {
                StringBuilder sb = new StringBuilder(100);
                for (int i = 0; i < 100; i++) {
                    LogType log = logs.poll(5, TimeUnit.SECONDS);
                    if (log == null) {
                        break;
                    }
                    sb.append(log.simpleSerialize());
                    sb.append(";");
                }
                String str = sb.toString();
                if (!str.isEmpty()) {
                    pool.execute(new LogProcessingHandler(str));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
