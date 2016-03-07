package com.teamged.deployment.deployments;

import com.teamged.deployment.base.SingleDeployment;
import com.teamged.deployment.internals.AuditInternals;

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
