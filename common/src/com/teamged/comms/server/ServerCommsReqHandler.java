package com.teamged.comms.server;

import com.teamged.comms.ServerMessage;
import com.teamged.comms.internal.CommsManager;
import com.teamged.comms.internal.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Created by DanielF on 2016-03-18.
 */
public class ServerCommsReqHandler implements Runnable {
    private final Socket socket;
    private ServerCommsRespHandler respHandler = null;

    public ServerCommsReqHandler(Socket socket) {
        this.socket = socket;
        CommsManager.CommsLogInfo("Communication listener has connected a receiver to client on port " + socket.getLocalPort());
    }

    @Override
    public void run() {
        String request;
        int nullRetries = 10;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            if (establishConnection(in)) {
                while (!respHandler.isShutdown()) {
                    request = in.readLine();
                    CommsManager.CommsLogVerbose("Server communication request handler got message: " + request);
                    Message msg = Message.fromCommunication(request);
                    if (msg != null) {
                        CommsManager.putNextServerRequest(new ServerMessage(msg));
                    } else {
                        if (nullRetries-- <= 0) {
                            CommsManager.CommsLogInfo("Server communication request handler has found that the socket appears to be closed");
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (respHandler != null) {
                respHandler.shutdown();
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean establishConnection(BufferedReader in) throws IOException {
        boolean establishedConn = false;
        String firstComm = "";

        try {
            firstComm = in.readLine();
            long clientAddress = Long.parseLong(firstComm);
            if (clientAddress > 0) {
                CommsManager.putServerResponseMapping(clientAddress);
                BlockingQueue<Message> msgQueue = CommsManager.getServerResponseQueue(clientAddress);
                if (msgQueue != null) {
                    respHandler = new ServerCommsRespHandler(msgQueue, socket);
                    establishedConn = true;
                } else {
                    CommsManager.CommsLogVerbose("Server communication request handler failed to find server response queue with key: " + clientAddress);
                }
            } else {
                CommsManager.CommsLogVerbose("Server communication request handler got invalid client address: " + clientAddress);
            }
        } catch (NumberFormatException ignored) {
            /* Catch the exception if the client fails to initiate correctly */
            CommsManager.CommsLogVerbose("Server communication request handler experienced error parsing client address: " + firstComm);
        }

        return establishedConn;
    }
}
