package com.teamged.fetchserver;

import com.teamged.deployment.deployments.QuoteFetchServerDeployment;
import com.teamged.fetchserver.serverthreads.ConnectionProcessingThread;
import com.teamged.fetchserver.serverthreads.FetchServerThread;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DanielF on 2016-03-08.
 */
public class FetchMonitor {

    private static final Object syncObject = new Object();
    private static final QuoteFetchServerDeployment FETCH_DEPLOY = FetchMain.Deployment.getFetchServers();
    private static final ArrayList<FetchServerThread> connThreads = new ArrayList<>();

    public static void runServer() {
        InternalLog.Log("Launching connection server socket listeners.");
        ConnectionProcessingThread cpThread;
        try {
            cpThread = new ConnectionProcessingThread(FETCH_DEPLOY.getPort(), FETCH_DEPLOY.getInternals().getThreadPoolSize(), syncObject);
            connThreads.add(cpThread);
            new Thread(cpThread).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        do {
            synchronized (syncObject) {
                try {
                    syncObject.wait();

                    /*
                    check thread status, restart threads if necessary
                     */
                } catch (InterruptedException e) {
                    // Close threads?
                    e.printStackTrace();
                    break;
                }
            }
        } while (!connThreads.isEmpty());
    }
}
