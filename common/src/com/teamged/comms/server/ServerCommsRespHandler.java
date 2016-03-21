package com.teamged.comms.server;

import com.teamged.comms.internal.CommsManager;
import com.teamged.comms.internal.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Created by DanielF on 2016-03-18.
 */
public class ServerCommsRespHandler {
    private final BlockingQueue<Message> serverResponseQueue;
    private final Socket socket;
    private boolean shutdown = false;

    public ServerCommsRespHandler(BlockingQueue<Message> serverResponseQueue, Socket socket) {
        this.serverResponseQueue = serverResponseQueue;
        this.socket = socket;
        CommsManager.CommsLogInfo("Communication listener has connected a responder to client on port " + socket.getLocalPort());
        new Thread(this::listen).start();
    }

    public void shutdown() {
        shutdown = true;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void listen() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){
            while (!shutdown) {
                Message msg = serverResponseQueue.take();
                if (msg != null) {
                    CommsManager.CommsLogVerbose("Server communication response handler sending message: " + msg.toString());
                    out.println(msg.toString());
                } else {
                    CommsManager.CommsLogVerbose("Server communication response handler found null response message");
                }
            }
        } catch (IOException | InterruptedException e) {
            shutdown();
            e.printStackTrace();
        }
    }
}
