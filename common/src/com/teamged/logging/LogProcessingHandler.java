package com.teamged.logging;

import com.teamged.comms.ClientMessage;
import com.teamged.comms.CommsInterface;

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
        ClientMessage clientMessage = ClientMessage.buildMessage(Logger.GetLogDestination().getServer(), logStr, false);
        CommsInterface.addClientRequest(clientMessage);
    }
}
