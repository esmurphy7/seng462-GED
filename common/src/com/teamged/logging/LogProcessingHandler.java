package com.teamged.logging;

import com.teamged.logging.xmlelements.LogType;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by DanielF on 2016-03-03.
 */
public class LogProcessingHandler implements Runnable {
    private final LogType logObj;

    public LogProcessingHandler(LogType log) {
        logObj = log;
    }

    @Override
    public void run() {
        String logString = logObj.simpleSerialize();

        try (Socket s = new Socket(Logger.GetLogDestination().getServer(), Logger.GetLogDestination().getPort());
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)
        ) {
            out.println(logString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
