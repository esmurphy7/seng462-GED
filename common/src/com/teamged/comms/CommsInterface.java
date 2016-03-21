package com.teamged.comms;

import com.teamged.comms.internal.CommsManager;
import com.teamged.deployment.DeploymentServer;

/**
 * Created by DanielF on 2016-03-18.
 */
public class CommsInterface {

    /**
     * KNOWN ISSUES
     * ------------
     *
     * TODO: Server side does not re-enable response handling properly after a connection is dropped and re-established
     * TODO: Client side does not return to polling the server after it disconnects
     * TODO: Server and client do not check for (sufficient) errors when writing to each other
     */

    /**
     *
     * @param port
     * @return
     */
    public static boolean startServerCommunications(int port) {
        return CommsManager.addServerComms(port);
    }

    /**
     *
     */
    public static void endServerCommunications() {
        CommsManager.closeServerComms();
    }

    /**
     *
     * @param deploymentServer
     * @param server
     * @param port
     * @param connections
     * @return
     */
    public static boolean startClientCommunications(DeploymentServer deploymentServer, String server, int port, int connections) {
        return CommsManager.addClientComms(deploymentServer, server, port, connections);
    }

    /**
     *
     */
    public static void endClientCommunications() {
        CommsManager.closeClientComms();
    }

    /**
     * Communication module API call for getting the next request sent to this server. If the server message
     * flag value is 0, then a response is expected (call serverMessage.setResponse(response) where serverMessage
     * is the ServerMessage received here and response is the String response in the format the client expects).
     * The meaning of flags of different values are server defined.
     *
     * If there is an internal error, the ServerMessage returned may be null.
     *
     * @return The next request the server has received.
     */
    public static ServerMessage getNextServerRequest() {
        return CommsManager.takeNextServerRequest();
    }

    /**
     *
     * @param clientMessage
     */
    public static void addClientRequest(ClientMessage clientMessage) {
        CommsManager.putNextClientRequest(clientMessage);
    }
}
