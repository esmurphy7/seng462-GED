package com.teamged.auditserver.threads;

import com.teamged.auditserver.AuditMain;

/**
 * Created by DanielF on 2016-02-23.
 */
public class LogDumpHandler implements Runnable {
    private final String message;

    public LogDumpHandler(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        if (message != null && message.contains("DUMPLOG,")) {
            try {
                int tid = Integer.parseInt(message.split(",")[1]);
                AuditMain.enableLogDumpRequest(tid);
                AuditMain.dumpIfReady();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
