package com.teamged.fetchserver.serverthreads;

import com.teamged.comms.CommsInterface;
import com.teamged.fetchserver.FetchMonitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.concurrent.*;

/**
 * Created by DanielF on 2016-03-08.
 */
public class ConnectionProcessingThread extends FetchServerThread {
    private static final ScheduledExecutorService timingLogger = Executors.newSingleThreadScheduledExecutor();
    private static final String OUTPUT_FILE = "quote_timings";
    private static final ConcurrentLinkedQueue<String> timingQueue = new ConcurrentLinkedQueue<>();

    static {
        timingLogger.scheduleWithFixedDelay(
                (Runnable) () -> {
                    System.out.println("Writing command timings to file...");
                    URL url = FetchMonitor.class.getResource("");
                    File outfile = new File(url.getPath() + OUTPUT_FILE);
                    try (FileWriter fw = new FileWriter(outfile, true)) {
                        String nextTiming;
                        while ((nextTiming = timingQueue.poll()) != null) {
                            fw.write(nextTiming);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Done writing command timings to file.");
                },
                30,
                10,
                TimeUnit.SECONDS
        );
    }

    private final ExecutorService pool;
    private final Object syncObject;
    private boolean running;

    public ConnectionProcessingThread(int poolSize, Object syncObject) {
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.syncObject = syncObject;
        this.running = true;
        System.out.println("Opened server connection processing thread");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        System.out.println("Server connection processing thread running");
        while (true) {
            pool.execute(new ConnectionProcessingHandler(CommsInterface.getNextServerRequest(), timingQueue));
        }
    }
}
