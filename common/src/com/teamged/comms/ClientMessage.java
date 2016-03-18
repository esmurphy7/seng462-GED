package com.teamged.comms;

import com.teamged.comms.internal.Message;

/**
 * Created by DanielF on 2016-03-18.
 */
public class ClientMessage {
    private final Message internalMessage;

    public ClientMessage(Message msg) {
        internalMessage = msg;
    }
}
