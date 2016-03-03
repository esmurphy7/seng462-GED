package com.teamged.auditserver.threads;

import com.teamged.auditserver.InternalLog;
import com.teamged.logging.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;
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
        // when a dumplog is signalled, save the logs to a file and clear the list
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
        {
            String message = in.readLine();
            if( message != null && message.equals("DUMPLOG"))
            {
                Logger.getInstance().SaveLog();
                Logger.getInstance().ClearLogs();
            }
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
            return;
        }
    }
}
