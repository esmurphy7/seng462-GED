package com.teamged.proxyserver;

import com.teamged.comms.CommsInterface;
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
        /*
        InternalLog.Log("Launching quote proxy server socket listeners");
        QuoteProxyProcessingThread qppThread;
        QuotePrefetchProcessingThread qpfpThread;
        try {
            qppThread = new QuoteProxyProcessingThread(PROXY_DEPLOY.getPort(), PROXY_DEPLOY.getInternals().getThreadPoolSize(), syncObject);
            proxyThreads.add(qppThread);
            new Thread(qppThread).start();

            qpfpThread = new QuotePrefetchProcessingThread(PROXY_DEPLOY.getInternals().getPrefetchPort(), PROXY_DEPLOY.getInternals().getSmallPoolSize(), syncObject);
            proxyThreads.add(qpfpThread);
            new Thread(qpfpThread).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        CommsInterface.startServerCommunications(PROXY_DEPLOY.getPort());

        QuoteMessageProcessingThread qmpt = new QuoteMessageProcessingThread(PROXY_DEPLOY.getInternals().getCommunicationThreads(), syncObject);
        proxyThreads.add(qmpt);
        new Thread(qmpt).start();

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
