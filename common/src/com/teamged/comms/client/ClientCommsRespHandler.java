package com.teamged.comms.client;

import com.teamged.comms.ClientMessage;
import com.teamged.comms.internal.CommsManager;
import com.teamged.comms.internal.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by DanielF on 2016-03-19.
 */
public class ClientCommsRespHandler {
    private final Socket socket;
    private boolean shutdown = false;

    public ClientCommsRespHandler(Socket socket) {
        CommsManager.CommsLogVerbose("Created client communication response handler"); // TODO: Debugging line
        this.socket = socket;
        new Thread(() -> listen()).start();
    }

    public void shutdown() {
        shutdown = true;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    private void listen() {
        CommsManager.CommsLogVerbose("Running client communication response handler"); // TODO: Debugging line
        String response;
        int nullRetries = 10;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            while (!shutdown) {
                CommsManager.CommsLogVerbose("Client communication response handler waiting for message"); // TODO: Debugging line
                response = in.readLine();
                CommsManager.CommsLogVerbose("Client communication response handler received message: " + response); // TODO: Debugging line

                if (response == null) {
                    if (nullRetries-- <= 0) {
                        CommsManager.CommsLogInfo("Client communication response handler has found that the socket appears to be closed"); // TODO: Debugging line
                        shutdown = true;
                        continue;
                    }
                }

                Message msg = Message.fromCommunication(response);
                if (msg == null) {
                    continue;
                }

                ClientMessage cmsg = CommsManager.releaseClientMessage(msg.getIdentifier());
                if (cmsg == null) {
                    continue;
                }

                CommsManager.CommsLogVerbose("Client communication response handler setting response"); // TODO: Debugging line
                cmsg.setResponse(msg.getData());
            }
        } catch (IOException e) {
            e.printStackTrace();
            shutdown = true;
        }
    }
}
