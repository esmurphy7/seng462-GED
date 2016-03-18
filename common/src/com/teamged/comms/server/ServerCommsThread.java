package com.teamged.comms.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DanielF on 2016-03-18.
 */
public class ServerCommsThread implements Runnable {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private boolean shutdown = false;

    public ServerCommsThread(int port, int poolSize) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(poolSize);
        System.out.println("Opened communication listener on port " + port);
    }

    public void shutdown() {
        System.out.println("Shutting down communication listener on port " + serverSocket.getLocalPort());
        shutdown = true;
        pool.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error encountered when closing socket: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Communication listener on port " + serverSocket.getLocalPort() + " is running");
        while (!shutdown && !pool.isShutdown()) {
            try {
                Socket s = serverSocket.accept();
                pool.execute(new ServerCommsRecvHandler(s));
                pool.execute(new ServerCommsRespHandler(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
