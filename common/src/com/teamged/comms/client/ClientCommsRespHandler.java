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
        this.socket = socket;
        listen();
    }

    public void shutdown() {
        shutdown = true;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    private void listen() {
        String response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            while (!shutdown) {
                response = in.readLine();

                Message msg = Message.fromCommunication(response);
                if (msg == null) {
                    continue;
                }

                ClientMessage cmsg = CommsManager.releaseClientMessage(msg.getIdentifier());
                if (cmsg == null) {
                    continue;
                }

                cmsg.setResponse(msg.getData());
            }
        } catch (IOException e) {
            e.printStackTrace();
            shutdown = true;
        }
    }
}
