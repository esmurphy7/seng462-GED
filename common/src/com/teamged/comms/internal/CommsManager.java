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
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by DanielF on 2016-03-18.
 */
public class CommsManager {
    public static final long SERVER_ID = ThreadLocalRandom.current().nextLong(Integer.MAX_VALUE, Long.MAX_VALUE);

    private static final int SERVER_THREAD_POOL_MAX = 128;
    private static final BlockingQueue<ServerMessage> serverReceiveRequest = new LinkedBlockingQueue<>();
    private static final ConcurrentHashMap<Long, BlockingQueue<Message>> serverSendResponse = new ConcurrentHashMap<>();
    private static final List<ServerCommsThread> serverThreads = new ArrayList<>();

    private static final ConcurrentHashMap<String, BlockingQueue<ClientMessage>> clientSendRequest = new ConcurrentHashMap<>();
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

    public static void closeServerComms() {
        serverThreads.forEach(ServerCommsThread::shutdown);
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
                serverSendResponse.get(m.getServerIdentifier()).put(m);
            } catch (InterruptedException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static void putServerResponseMapping(long serverId) {
        serverSendResponse.putIfAbsent(serverId, new LinkedBlockingQueue<>());
    }

    public static BlockingQueue<Message> getServerResponseQueue(long serverId) {
        return serverSendResponse.get(serverId);
    }

    /**********************************************************************************************
     ** Client communications
     *********************************************************************************************/

    public static boolean addClientComms(String server, int commsPort, int connections) {
        ClientCommsThread cct = new ClientCommsThread(server, commsPort, connections);
        clientThreads.add(cct);
        new Thread(cct).start();
        return true;
    }

    public static void closeClientComms() {
        clientThreads.forEach(ClientCommsThread::shutdown);
    }

    public static void putNextClientRequest(ClientMessage cm) {
        if (cm != null) {
            try {
                clientSendRequest.get(cm.getServerAddress()).put(cm);
            } catch (InterruptedException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean storeClientMessage(ClientMessage cm) {
        boolean messageStored = false;
        if (cm != null && cm.isResponseExpected()) {
            messageStored = (clientMessageMap.putIfAbsent(cm.getIdentifier(), cm) == cm);
        }

        return messageStored;
    }

    public static void putClientRequestMapping(String serverAddress) {
        clientSendRequest.putIfAbsent(serverAddress, new LinkedBlockingQueue<>());
    }

    public static BlockingQueue<ClientMessage> getClientRequestQueue(String serverAddress) {
        return clientSendRequest.get(serverAddress);
    }

    public static ClientMessage releaseClientMessage(long identifier) {
        return clientMessageMap.remove(identifier);
    }
}
