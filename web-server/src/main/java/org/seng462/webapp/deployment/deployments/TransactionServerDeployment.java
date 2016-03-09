package org.seng462.webapp.deployment.deployments;


import org.seng462.webapp.deployment.base.MultipleDeployment;
import org.seng462.webapp.deployment.internals.TransactionInternals;

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
