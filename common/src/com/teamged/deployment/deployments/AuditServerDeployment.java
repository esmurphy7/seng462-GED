package com.teamged.deployment.deployments;

import com.teamged.deployment.base.SingleDeployment;
import com.teamged.deployment.internals.AuditInternals;

public class AuditServerDeployment extends SingleDeployment {

    private AuditInternals internal;

    public AuditServerDeployment() {
        this.internal = new AuditInternals();
    }

    public AuditInternals getInternals() {
        return internal;
    }
}
