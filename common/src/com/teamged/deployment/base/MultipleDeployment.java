package com.teamged.deployment.base;

import java.util.ArrayList;
import java.util.List;

public abstract class MultipleDeployment extends BaseDeployment {

    protected List<String> servers;

    public MultipleDeployment() {
        this.servers = new ArrayList<>();
    }

    public List<String> getServers() {
        return servers;
    }
}
