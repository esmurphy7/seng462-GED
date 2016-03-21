package com.teamged.comms.client;

import com.teamged.comms.ClientMessage;
import com.teamged.comms.internal.CommsManager;
import com.teamged.deployment.DeploymentConfig;
import com.teamged.deployment.DeploymentServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 * Created by DanielF on 2016-03-19.
 */
public class ClientCommsReqHandler implements Callable<String> {
    private static final int INITIAL_RETRY = 5000;
    private static final int MIDDLE_RETRY = 10000;
    private static final int SLOW_RETRY = 60000;
    private static final int RETRY_COUNT = 20;
    private final DeploymentServer deploymentServer;
    private final String server;
    private final int port;
    private ClientCommsRespHandler respHandler;
    private BlockingQueue<ClientMessage> msgQueue;

    public ClientCommsReqHandler(DeploymentServer deploymentServer, String server, int port) {
        CommsManager.CommsLogVerbose("Created client communication request handler");
        this.deploymentServer = deploymentServer;
        this.server = server;
        this.port = port;
    }

    /**
     * Attempts to connect to the remote server. If the connection cannot be established, it will attempt to
     * retry, slowly increasing the retry rate. At its slowest, it will retry making a connection every minute.
     * Once a connection has been established, the client request queue will be monitored for new requests.
     * When one arrives, it will be sent to the remote server. The eventual response, if one is expected, will
     * be handled by the ClientCommsRespHandler.
     *
     * @return
     * @throws Exception
     */
    @Override
    public String call() throws Exception {
        CommsManager.CommsLogVerbose("Running client communication request handler");
        Socket socket = null;
        boolean connected = false;
        int retries = 0;
        int pauseTime = INITIAL_RETRY;

        while (!connected) {
            try {
                CommsManager.CommsLogInfo("Client communication request handler attempting connection with " + server + ":" + port);
                socket = new Socket(server, port);
                //respHandler = new ClientCommsRespHandler(socket);
                connected = true;
                CommsManager.CommsLogInfo("Client communication initialized with " + server + ":" + port);
            } catch (IOException ignored) {
                retries++;
                if (retries > RETRY_COUNT) {
                    pauseTime = retries > (RETRY_COUNT * 2) ? SLOW_RETRY : MIDDLE_RETRY;
                }
                CommsManager.CommsLogInfo("Could not connect to server at " + server + ":" + port +
                        "; retrying in " + pauseTime/1000 + " seconds");
                Thread.sleep(pauseTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CommsManager.CommsLogVerbose("Client communication request handler is connected to remote server on port " + socket.getLocalPort());

        String retVal = "";
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            if (establishConnection(out, socket)) {
                ClientMessage msg;
                while (!out.checkError()) {
                    CommsManager.CommsLogVerbose("Client communication request handler is waiting on the next available client request");
                    msg = msgQueue.take();

                    // Exits the request handler if the response handler died.
                    if (respHandler.isShutdown()) {
                        CommsManager.putNextClientRequest(msg);
                        retVal = "Client response handler experienced error";
                        break;
                    }

                    if (msg.isResponseExpected()) {
                        boolean error = CommsManager.storeClientMessage(msg);
                        if (error) {
                            CommsManager.CommsLogInfo("Client communication request handler experienced error adding client request for response. Returning null.");
                            msg.setResponse(null);
                            continue;
                        }
                    }

                    CommsManager.CommsLogVerbose("Client communication request handler sending message: " + msg.toString());
                    out.println(msg.toString());
                }
            }
        } catch (Exception e) {
            retVal = e.getMessage();
        } finally {
            if (respHandler != null) {
                respHandler.shutdown();
            }

            try {
                CommsManager.CommsLogInfo("Client communication request handler closing socket");
                socket.close();
            } catch (IOException ignored) {}
        }

        return retVal;
    }

    private boolean establishConnection(PrintWriter out, Socket socket) {
        boolean establishedConn = false;
        out.println(CommsManager.SERVER_ID);
        if (!out.checkError()) {
            CommsManager.putClientRequestMapping(deploymentServer);
            msgQueue = CommsManager.getClientRequestQueue(deploymentServer);
            if (msgQueue != null) {
                respHandler = new ClientCommsRespHandler(socket);
                establishedConn = true;
            } else {
                CommsManager.CommsLogVerbose("Client communication request handler failed to find client request queue with key: " + deploymentServer);
            }
        } else {
            CommsManager.CommsLogVerbose("Client communication request handler experienced IO error with server");
        }

        return establishedConn;
    }
}
