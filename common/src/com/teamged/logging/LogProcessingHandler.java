package com.teamged.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Created by DanielF on 2016-03-03.
 */
public class LogProcessingHandler implements Runnable {
    private final String logStr;

    public LogProcessingHandler(String log) {
        logStr = log;
    }

    @Override
    public void run() {
        try (Socket s = new Socket(Logger.GetLogDestination().getServer(), Logger.GetLogDestination().getPort());
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)
        ) {
            out.println(logStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
