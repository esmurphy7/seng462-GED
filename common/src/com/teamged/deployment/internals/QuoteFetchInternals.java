package com.teamged.deployment.internals;

/**
 * Created by DanielF on 2016-03-08.
 */
public class QuoteFetchInternals {

    private Integer threadPool;
    private Integer commThreads;

    public Integer getThreadPoolSize() {
        return threadPool;
    }

    public Integer getCommunicationThreads() {
        return commThreads;
    }
}
