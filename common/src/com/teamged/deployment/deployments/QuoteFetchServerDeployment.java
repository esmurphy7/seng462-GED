package com.teamged.deployment.deployments;

import com.teamged.deployment.base.MultipleDeployment;
import com.teamged.deployment.internals.QuoteFetchInternals;

/**
 * Created by DanielF on 2016-03-08.
 */
public class QuoteFetchServerDeployment extends MultipleDeployment {

    private QuoteFetchInternals internal;

    public QuoteFetchServerDeployment() {
        this.internal = new QuoteFetchInternals();
    }

    public QuoteFetchInternals getInternals() {
        return internal;
    }
}
