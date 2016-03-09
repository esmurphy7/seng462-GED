package com.teamged.deployment.base;

import java.util.ArrayList;
import java.util.List;

public class BaseDeployment {

    protected Integer port;
    protected List<String> resources;

    public BaseDeployment() {
        this.resources = new ArrayList<>();
    }

    public Integer getPort() {
        return port;
    }

    public List<String> getResources() {
        return resources;
    }
}
