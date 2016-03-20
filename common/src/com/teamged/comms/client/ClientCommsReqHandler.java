package com.teamged.comms.client;

import com.teamged.comms.ClientMessage;
import com.teamged.comms.internal.CommsManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * Created by DanielF on 2016-03-19.
 */
public class ClientCommsReqHandler implements Callable<String> {
    private static final int INITIAL_RETRY = 5000;
    private static final int MIDDLE_RETRY = 10000;
    private static final int SLOW_RETRY = 60000;
    private static final int RETRY_COUNT = 20;
    private final String server;
    private final int port;

    public ClientCommsReqHandler(String server, int port) {
        CommsManager.CommsLogVerbose("Created client communication request handler"); // TODO: Debugging line
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
        CommsManager.CommsLogVerbose("Running client communication request handler"); // TODO: Debugging line
        Socket socket = null;
        ClientCommsRespHandler respHandler = null;
        boolean connected = false;
        int retries = 0;
        int pauseTime = INITIAL_RETRY;

        while (!connected) {
            try {
                CommsManager.CommsLogInfo("Client communication request handler attempting connection with " + server + ":" + port);
                socket = new Socket(server, port);
                respHandler = new ClientCommsRespHandler(socket);
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

        CommsManager.CommsLogVerbose("Client communication request handler is connected to remote server on port " + socket.getLocalPort()); // TODO: Debugging line

        String retVal = "";
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            ClientMessage msg;
            while (true) {
                CommsManager.CommsLogVerbose("Client communication request handler is waiting on the next available client request"); // TODO: Debugging line
                msg = CommsManager.takeNextClientRequest();

                // Exits the request handler if the response handler died.
                if (respHandler.isShutdown()) {
                    CommsManager.putNextClientRequest(msg);
                    retVal = "Client response handler experienced error";
                    break;
                }

                if (msg.isResponseExpected()) {
                    boolean error = CommsManager.storeClientMessage(msg);
                    if (error) {
                        CommsManager.CommsLogInfo("Client communication request handler experienced error adding client request for response. Returning null."); // TODO: Debugging line
                        msg.setResponse(null);
                        continue;
                    }
                }

                CommsManager.CommsLogVerbose("Client communication request handler sending message: " + msg.toString()); // TODO: Debugging line
                out.println(msg.toString());
            }
        } catch (Exception e) {
            retVal = e.getMessage();
            respHandler.shutdown();
        } finally {
            try {
                CommsManager.CommsLogInfo("Client communication request handler closing socket"); // TODO: Debugging line
                socket.close();
            } catch (IOException ignored) {}
        }

        return retVal;
    }
}
