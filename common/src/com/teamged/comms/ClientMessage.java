package com.teamged.comms;

import com.teamged.comms.internal.Message;
import com.teamged.deployment.DeploymentServer;

/**
 * Created by DanielF on 2016-03-18.
 */
public class ClientMessage {
    private final String serverAddress;
    private final Message internalMessage;
    private final boolean expResp;

    /**
     * Private constructor for the ClientMessage. Assigns the underlying Message.
     *
     * @param serverAddress     The server this ClientMessage is bound for.
     * @param msg               The underlying Message, containing communication meta-information.
     * @param responseExpected  Whether this client message expects a response or not.
     */
    private ClientMessage(String serverAddress, Message msg, boolean responseExpected) {
        this.serverAddress = serverAddress;
        this.internalMessage = msg;
        this.expResp = responseExpected;
    }

    /**
     * Builds a ClientMessage from the provided information. The message will have a
     * randomly generated identifier and an empty flag.
     *
     * @param serverAddress     The server this ClientMessage is bound for.
     * @param data              The Message data.
     * @param responseExpected  Whether this client message expects a response or not.
     * @return                  A ClientMessage containing communication meta-information.
     */
    public static ClientMessage buildMessage(String serverAddress, String data, boolean responseExpected) {
        return buildMessage(serverAddress, 0, data, responseExpected);
    }

    /**
     * Builds a ClientMessage from the provided information. The message will have a
     * randomly generated identifier.
     *
     * @param serverAddress     The server this ClientMessage is bound for.
     * @param flags             The message's flags.
     * @param data              The message's data.
     * @param responseExpected  Whether this client message expects a response or not.
     * @return                  A ClientMessage containing communication meta-information.
     */
    public static ClientMessage buildMessage(String serverAddress, int flags, String data, boolean responseExpected) {
        Message msg = new Message(0, flags, data);
        return new ClientMessage(serverAddress, msg, responseExpected);
    }

    /**
     * Gets the server that this ClientMessage is bound for.
     *
     * @return The server this ClientMessage is bound for.
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Gets the identifying number for this Message. This number is assumed to be unique.
     *
     * @return The Message's identifying number.
     */
    public long getIdentifier() {
        return internalMessage.getIdentifier();
    }

    /**
     * Gets the flags for this Message. The meaning of a nonzero flag is caller defined.
     *
     * @return The Message's flags.
     */
    public int getFlags() {
        return internalMessage.getFlags();
    }

    /**
     * Gets the data for this Message.
     *
     * @return The Message's data.
     */
    public String getData() {
        return internalMessage.getData();
    }

    /**
     * Gets whether a response is expected for this ClientMessage or not.
     *
     * @return  Whether this client message expects a response or not.
     */
    public boolean isResponseExpected() {
        return expResp;
    }

    /**
     * Gets the client response, blocking until one exists. If the ClientMessage is flagged as not
     * expecting a response, this will return null. If a response already exists, this will return
     * it immediately.
     *
     * @return The response to this message, or null if no response is expected or an error occurred.
     */
    public String waitForResponse() {
        return expResp ? internalMessage.getClientResponse() : null;
    }

    /**
     * Sets the client response. Listeners blocked on the 'waitForResponse' call will be notified. A
     * response can only be set once - future attempts will be ignored.
     *
     * @param response  The response to add to this message.
     */
    public void setResponse(String response) {
        internalMessage.setClientResponse(response);
    }

    @Override
    public String toString() {
        return internalMessage.toString();
    }
}
