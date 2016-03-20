package com.teamged.comms;

import com.teamged.comms.internal.Message;

/**
 * Created by DanielF on 2016-03-18.
 */
public class ClientMessage {
    private final Message internalMessage;
    private final boolean expResp;

    /**
     * Private constructor for the ClientMessage. Assigns the underlying Message.
     *
     * @param msg               The underlying Message, containing communication meta-information.
     * @param responseExpected  Whether this client message expects a response or not.
     */
    private ClientMessage(Message msg, boolean responseExpected) {
        internalMessage = msg;
        expResp = responseExpected;
    }

    /**
     * Builds a ClientMessage from the provided information. The message will have a
     * randomly generated identifier and an empty flag.
     *
     * @param data              The Message data.
     * @param responseExpected  Whether this client message expects a response or not.
     * @return                  A ClientMessage containing communication meta-information.
     */
    public static ClientMessage buildMessage(String data, boolean responseExpected) {
        return buildMessage(0, 0, data, responseExpected);
    }

    /**
     * Builds a ClientMessage from the provided information. The message will have an empty
     * flag. If the identifier's value is 0, a randomly generated identifier will be used.
     * The identifying number is assumed to be unique. Non-unique values will cause responses
     * to not be correctly assigned to the requester.
     *
     * @param identifier        The message's unique identifying number.
     * @param data              The message's data.
     * @param responseExpected  Whether this client message expects a response or not.
     * @return                  A ClientMessage containing communication meta-information.
     */
    public static ClientMessage buildMessage(long identifier, String data, boolean responseExpected) {
        return buildMessage(identifier, 0, data, responseExpected);
    }

    /**
     * Builds a ClientMessage from the provided information. If the identifier's value
     * is 0, a randomly generated identifier will be used. The identifying number is
     * assumed to be unique. Non-unique values will cause responses to not be correctly
     * assigned to the requester.
     *
     * @param identifier        The message's unique identifying number.
     * @param flags             The message's flags.
     * @param data              The message's data.
     * @param responseExpected  Whether this client message expects a response or not.
     * @return                  A ClientMessage containing communication meta-information.
     */
    public static ClientMessage buildMessage(long identifier, int flags, String data, boolean responseExpected) {
        Message msg = new Message(identifier, flags, data);
        return new ClientMessage(msg, responseExpected);
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
    public String getResponse() {
        return expResp ? internalMessage.getClientResponse() : null;
    }

    /**
     * Sets the client response. Listeners blocked on the 'getResponse' call will be notified. A
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
