package com.teamged.proxyserver;

import com.teamged.deployment.deployments.QuoteProxyServerDeployment;
import com.teamged.proxyserver.serverthreads.ProxyServerThread;

import java.util.ArrayList;

/**
 * Created by DanielF on 2016-03-09.
 */
public class ProxyMonitor {

    private static final Object syncObject = new Object();
    private static final QuoteProxyServerDeployment PROXY_DEPLOY = ProxyMain.Deployment.getProxyServer();

    private static final ArrayList<ProxyServerThread> proxyThread = new ArrayList<>();
    // TODO: Pre-fetch threads

    public static void runServer() {
        InternalLog.Log("Launching quote proxy server socket listeners");

    }
}
