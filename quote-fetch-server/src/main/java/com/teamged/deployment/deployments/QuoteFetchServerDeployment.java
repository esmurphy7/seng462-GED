package com.teamged.deployment.deployments;

import com.teamged.deployment.base.MultipleDeployment;
import com.teamged.deployment.internals.QuoteFetchInternals;
import com.teamged.deployment.internals.TransactionInternals;

/**
 * Created by DanielF on 2016-03-06.
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
