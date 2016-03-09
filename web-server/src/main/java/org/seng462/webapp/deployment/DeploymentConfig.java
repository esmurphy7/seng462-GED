package org.seng462.webapp.deployment;

/**
 * Deployment configuration class.
 */
public class DeploymentConfig {
    private String remoteDirectory;
    private DeploymentSettings deployments;

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public DeploymentSettings getDeployments() {
        return deployments;
    }

    public void setDeployments(DeploymentSettings deployments) {
        this.deployments = deployments;
    }
}
