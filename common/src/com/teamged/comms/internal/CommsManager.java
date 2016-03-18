package com.teamged.comms.internal;

import com.teamged.comms.ServerMessage;
import com.teamged.comms.server.ServerCommsThread;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by DanielF on 2016-03-18.
 */
public class CommsManager {
    private static final int SERVER_THREAD_POOL_MAX = 128;
    private static final BlockingQueue<ServerMessage> serverReceivedMessages = new LinkedBlockingQueue<>();
    private static final BlockingQueue<Message> serverSendResponse = new LinkedBlockingQueue<>();

    private static final ConcurrentHashMap<Integer, String> clientMessageMap = new ConcurrentHashMap<>();

    private static ServerCommsThread serverThread;

    public static boolean runServerComms(int commsPort) {
        boolean started = false;
        try {
            serverThread = new ServerCommsThread(commsPort, SERVER_THREAD_POOL_MAX);
            started = true;
        } catch (IOException e) {
            System.out.println("Failed to start server socket on port " + commsPort + "; error: " + e.getMessage());
        }

        return started;
    }

    public static void putNextServerRequest(ServerMessage sm) {
        if (sm != null) {
            try {
                serverReceivedMessages.put(sm);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static ServerMessage takeNextServerRequest() {
        ServerMessage sMsg = null;
        try {
            sMsg = serverReceivedMessages.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sMsg;
    }

    public static void putNextServerResponse(Message m) {
        if (m != null) {
            try {
                serverSendResponse.put(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static Message takeNextServerResponse() {
        Message msg = null;
        try {
            msg = serverSendResponse.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return msg;
    }

}
