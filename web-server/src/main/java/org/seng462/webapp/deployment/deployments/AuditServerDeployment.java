package org.seng462.webapp.deployment.deployments;


import org.seng462.webapp.deployment.base.SingleDeployment;
import org.seng462.webapp.deployment.internals.AuditInternals;

/**
 * Created by DanielF on 2016-03-06.
 */
public class AuditServerDeployment extends SingleDeployment {

    private AuditInternals internal;

    public AuditServerDeployment() {
        this.internal = new AuditInternals();
    }

    public AuditInternals getInternals() {
        return internal;
    }
}
