package com.teamged.comms;

import com.teamged.comms.internal.Message;

/**
 * Created by DanielF on 2016-03-18.
 */
public class ServerMessage {
    private final Message internalMessage;

    public ServerMessage(Message msg) {
        internalMessage = msg;
    }

    /**
     * Gets the identifying number for this message. This number is assumed to be unique.
     *
     * @return The message's identifying number.
     */
    public long getIdentifier() {
        return internalMessage.getIdentifier();
    }

    /**
     * Gets the flags for this message. The meaning of a nonzero flag is caller defined.
     *
     * @return The message's flags.
     */
    public int getFlags() {
        return internalMessage.getFlags();
    }

    /**
     * Gets the data for this message.
     *
     * @return The message's data.
     */
    public String getData() {
        return internalMessage.getData();
    }

    /**
     * Sets the response to this message. This will be sent to the client making
     * the original request of the server.
     *
     * @param data The data of the response.
     */
    public void setResponse(String data) {
        internalMessage.setServerResponse(data);
    }
}
