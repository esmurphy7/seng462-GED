package com.teamged.deployment.deployments;

import com.teamged.deployment.base.MultipleDeployment;
import com.teamged.deployment.internals.TransactionInternals;

/**
 * Created by DanielF on 2016-03-06.
 */
public class TransactionServerDeployment extends MultipleDeployment {

    private TransactionInternals internal;

    public TransactionServerDeployment() {
        this.internal = new TransactionInternals();
    }

    public TransactionInternals getInternals() {
        return internal;
    }
}
