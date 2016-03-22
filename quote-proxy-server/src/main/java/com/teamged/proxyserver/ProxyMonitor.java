package com.teamged.proxyserver;

import com.teamged.comms.CommsInterface;
import com.teamged.deployment.deployments.QuoteFetchServerDeployment;
import com.teamged.deployment.deployments.QuoteProxyServerDeployment;
import com.teamged.proxyserver.serverthreads.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DanielF on 2016-03-09.
 */
public class ProxyMonitor {

    private static final Object syncObject = new Object();
    private static final QuoteProxyServerDeployment PROXY_DEPLOY = ProxyMain.Deployment.getProxyServer();

    private static final ArrayList<ProxyServerThread> proxyThreads = new ArrayList<>();
    // TODO: Pre-fetch threads

    public static void runServer() {
        CommsInterface.startServerCommunications(PROXY_DEPLOY.getPort());
        QuoteMessageProcessingThread qmpt = new QuoteMessageProcessingThread(PROXY_DEPLOY.getInternals().getCommunicationThreads(), syncObject);
        proxyThreads.add(qmpt);
        new Thread(qmpt).start();

        QuoteFetchServerDeployment fetchServer = ProxyMain.Deployment.getFetchServers();
        for (String server : fetchServer.getServers()) {
            CommsInterface.startClientCommunications(server, fetchServer.getPort(), fetchServer.getInternals().getCommunicationThreads());
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
        } while (!proxyThreads.isEmpty());
    }
}
