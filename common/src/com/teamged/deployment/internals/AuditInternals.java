package com.teamged.deployment.internals;

public class AuditInternals {

    private Integer threadPool;
    private Integer commThreads;
    private Integer dumpPort;

    public Integer getThreadPoolSize() {
        return threadPool;
    }

    public Integer getCommunicationThreads() {
        return commThreads;
    }

    public Integer getDumpPort() {
        return dumpPort;
    }
}
