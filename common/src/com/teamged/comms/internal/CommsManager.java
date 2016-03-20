package com.teamged.comms.internal;

import com.teamged.comms.ClientMessage;
import com.teamged.comms.ServerMessage;
import com.teamged.comms.client.ClientCommsThread;
import com.teamged.comms.server.ServerCommsThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by DanielF on 2016-03-18.
 */
public class CommsManager {
    private static final int SERVER_THREAD_POOL_MAX = 128;
    private static final BlockingQueue<ServerMessage> serverReceiveRequest = new LinkedBlockingQueue<>();
    private static final BlockingQueue<Message> serverSendResponse = new LinkedBlockingQueue<>();
    private static final List<ServerCommsThread> serverThreads = new ArrayList<>();

    private static final BlockingQueue<ClientMessage> clientSendRequest = new LinkedBlockingQueue<>();
    private static final ConcurrentHashMap<Long, ClientMessage> clientMessageMap = new ConcurrentHashMap<>();
    private static final List<ClientCommsThread> clientThreads = new ArrayList<>();

    public static void CommsLogInfo(String log) {
        System.out.println(log);
    }

    public static void CommsLogVerbose(String log) {
        //System.out.println(log);
    }

    /**********************************************************************************************
     ** Server communications
     *********************************************************************************************/

    /**
     *
     * @param commsPort
     * @return
     */
    public static boolean addServerComms(int commsPort) {
        boolean started = false;
        try {
            ServerCommsThread sct = new ServerCommsThread(commsPort, SERVER_THREAD_POOL_MAX);
            serverThreads.add(sct);
            new Thread(sct).start();
            started = true;
        } catch (IOException e) {
            CommsManager.CommsLogInfo("Failed to start server socket on port " + commsPort + "; error: " + e.getMessage());
        }

        return started;
    }

    public static void putNextServerRequest(ServerMessage sm) {
        if (sm != null) {
            try {
                serverReceiveRequest.put(sm);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static ServerMessage takeNextServerRequest() {
        ServerMessage sMsg = null;
        try {
            sMsg = serverReceiveRequest.take();
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

    /**********************************************************************************************
     ** Client communications
     **
     ** TODO: Support multiple client connections.
     *********************************************************************************************/

    public static boolean addClientComms(String server, int commsPort, int connections) {
        ClientCommsThread cct = new ClientCommsThread(server, commsPort, connections);
        clientThreads.add(cct);
        new Thread(cct).start();
        return true;
    }

    public static void putNextClientRequest(ClientMessage cm) {
        if (cm != null) {
            try {
                clientSendRequest.put(cm);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static ClientMessage takeNextClientRequest() {
        ClientMessage cMsg = null;
        try {
            cMsg = clientSendRequest.take();
            CommsManager.CommsLogInfo(cMsg.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return cMsg;
    }

    public static boolean storeClientMessage(ClientMessage cm) {
        boolean messageStored = false;
        if (cm != null && cm.isResponseExpected()) {
            messageStored = (clientMessageMap.putIfAbsent(cm.getIdentifier(), cm) == cm);
        }

        return messageStored;
    }

    public static ClientMessage releaseClientMessage(long identifier) {
        return clientMessageMap.remove(identifier);
    }
}
