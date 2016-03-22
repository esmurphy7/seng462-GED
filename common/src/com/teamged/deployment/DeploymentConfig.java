package com.teamged.deployment;

public class DeploymentConfig {

    private String remoteDirectory;
    private Integer commsTimeout;
    private DeploymentSettings deployments;

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public Integer getCommsTimeout() {
        return commsTimeout;
    }

    public DeploymentSettings getDeployments() {
        return deployments;
    }
}
