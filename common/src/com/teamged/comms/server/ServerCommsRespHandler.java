package com.teamged.comms.server;

import com.teamged.comms.internal.CommsManager;
import com.teamged.comms.internal.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by DanielF on 2016-03-18.
 */
public class ServerCommsRespHandler implements Runnable {
    private final Socket socket;

    public ServerCommsRespHandler(Socket socket) {
        this.socket = socket;
        CommsManager.CommsLogInfo("Communication listener has connected a responder to client on port " + socket.getLocalPort());
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){
            while (true) {
                Message msg = CommsManager.takeNextServerResponse();
                CommsManager.CommsLogVerbose("Server communication response handler sending message: " + msg.toString()); // TODO: Debugging line
                out.println(msg.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
