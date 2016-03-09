package com.teamged.deployment.internals;

/**
 * Created by DanielF on 2016-03-06.
 */
public class TransactionInternals {

    private Integer threadPool;
    private Integer procThreads;
    private Integer commThreads;
    private Integer queueSize;

    public Integer getThreadPoolSize() {
        return threadPool;
    }

    public Integer getProcedureThreads() {
        return procThreads;
    }

    public Integer getCommunicationThreads() {
        return commThreads;
    }

    public Integer getQueueSize() {
        return queueSize;
    }
}
