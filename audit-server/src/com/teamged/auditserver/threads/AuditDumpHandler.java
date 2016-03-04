package com.teamged.auditserver.threads;

import com.teamged.auditserver.AuditMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by DanielF on 2016-02-23.
 */
public class AuditDumpHandler implements Runnable
{
    private final Socket socket;

    public AuditDumpHandler(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
        {
            String message = in.readLine();
            if( message != null && message.contains("DUMPLOG,"))
            {
                try {
                    int tid = Integer.parseInt(message.split(",")[1]);
                    AuditMain.enableLogDumpRequest(tid);
                    AuditMain.dumpIfReady();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
