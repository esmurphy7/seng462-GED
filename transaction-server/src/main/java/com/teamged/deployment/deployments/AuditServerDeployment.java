package com.teamged.deployment.deployments;

import com.teamged.deployment.base.SingleDeployment;
import com.teamged.deployment.internals.AuditInternals;

/**
 * Created by DanielF on 2016-03-06.
 */
public class AuditServerDeployment extends SingleDeployment {

    public AuditServerDeployment() {
        this.internal = new AuditInternals();
    }

    @Override
    public AuditInternals getInternals() {
        return (AuditInternals) this.internal;
    }
}
