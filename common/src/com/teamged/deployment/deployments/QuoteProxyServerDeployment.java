package com.teamged.deployment.deployments;

import com.teamged.deployment.base.SingleDeployment;
import com.teamged.deployment.internals.QuoteFetchInternals;
import com.teamged.deployment.internals.QuoteProxyInternals;

/**
 * Created by DanielF on 2016-03-08.
 */
public class QuoteProxyServerDeployment extends SingleDeployment {

    private QuoteProxyInternals internal;

    public QuoteProxyServerDeployment() {
        this.internal = new QuoteProxyInternals();
    }

    public QuoteProxyInternals getInternals() {
        return internal;
    }
}
