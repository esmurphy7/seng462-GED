package teamgid.deploy462;

import teamgid.deploy462.deployments.*;

import java.util.List;

public class DeploymentConfig {

    private String remoteDirectory;
    private Deployments deployments;

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    public Deployments getDeployments() {
        return deployments;
    }

    public void setDeployments(Deployments deployments) {
        this.deployments = deployments;
    }

    public class Deployments {

        private WorkloadGeneratorDeployment workloadGenerator;
        private WebLoadBalancerDeployment webLoadBalancer;
        private WebServerDeployment webServers;
        private TransactionServerDeployment transactionServers;
        private AuditServerDeployment auditServer;
        private CacheServerDeployment cacheServer;
        private QuoteFetchServerDeployment fetchServer;

        public WorkloadGeneratorDeployment getWorkloadGenerator() {
            return workloadGenerator;
        }

        public void setWorkloadGenerator(WorkloadGeneratorDeployment workloadGenerator) {
            this.workloadGenerator = workloadGenerator;
        }

        public WebLoadBalancerDeployment getWebLoadBalancer() {
            return webLoadBalancer;
        }

        public void setWebLoadBalancer(WebLoadBalancerDeployment webLoadBalancer) {
            this.webLoadBalancer = webLoadBalancer;
        }

        public WebServerDeployment getWebServers() {
            return webServers;
        }

        public void setWebServers(WebServerDeployment webServers) {
            this.webServers = webServers;
        }

        public TransactionServerDeployment getTransactionServers() {
            return transactionServers;
        }

        public void setTransactionServers(TransactionServerDeployment transactionServers) {
            this.transactionServers = transactionServers;
        }

        public AuditServerDeployment getAuditServer() {
            return auditServer;
        }

        public void setAuditServer(AuditServerDeployment auditServer) {
            this.auditServer = auditServer;
        }

        public CacheServerDeployment getCacheServer() {
            return cacheServer;
        }

        public void setCacheServer(CacheServerDeployment cacheServer) {
            this.cacheServer = cacheServer;
        }

        public QuoteFetchServerDeployment getFetchServer() {
            return fetchServer;
        }

        public void setFetchServer(QuoteFetchServerDeployment fetchServer) {
            this.fetchServer = fetchServer;
        }
    }
}
