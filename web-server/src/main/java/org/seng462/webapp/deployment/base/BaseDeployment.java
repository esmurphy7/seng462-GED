package org.seng462.webapp.deployment.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DanielF on 2016-03-06.
 */
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
