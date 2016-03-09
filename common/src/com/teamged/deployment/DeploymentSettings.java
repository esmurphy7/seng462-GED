package com.teamged.deployment;

import com.teamged.deployment.deployments.*;

public class DeploymentSettings {
    private QuoteServer quoteServer;
    private WorkloadGeneratorDeployment workloadGenerator;
    private WebLoadBalancerDeployment webLoadBalancer;
    private WebServerDeployment webServers;
    private TransactionServerDeployment transactionServers;
    private AuditServerDeployment auditServer;
    private CacheServerDeployment cacheServer;
    private QuoteFetchServerDeployment fetchServer;

    public QuoteServer getQuoteServer() {
        return quoteServer;
    }

    public WorkloadGeneratorDeployment getWorkloadGenerator() {
        return workloadGenerator;
    }

    public WebLoadBalancerDeployment getWebLoadBalancer() {
        return webLoadBalancer;
    }

    public WebServerDeployment getWebServers() {
        return webServers;
    }

    public TransactionServerDeployment getTransactionServers() {
        return transactionServers;
    }

    public AuditServerDeployment getAuditServer() {
        return auditServer;
    }

    public CacheServerDeployment getCacheServer() {
        return cacheServer;
    }

    public QuoteFetchServerDeployment getFetchServer() {
        return fetchServer;
    }
}
