package com.teamged.comms.internal;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Long.MAX_VALUE;

/**
 * Created by DanielF on 2016-03-18.
 *
 * This class provides the underlying workings for a Message passed between client and server.
 * Its functions are exposed through the containing ClientMessage or ServerMessage. There is no
 * reason to directly interact with this class.
 */
public class Message {
    private static final long LRI = (long)Integer.MAX_VALUE + 1;
    private static final Pattern msgPattern = Pattern.compile("^<(\\d+),(\\d+),(\\d+)>,(.+)$");

    private final long serverIdentifier;
    private final long identifier;
    private final int flags;
    private final String data;
    private boolean hasResponse;
    private String response;

    /**
     * Builds a Message from communication text received by a ServerCommsReqHandler.
     * If the message cannot be parsed, null will be returned.
     *
     * @param commText  The communication text received by a ServerCommsReqHandler.
     * @return          The parsed message.
     */
    public static Message fromCommunication(String commText) {
        Message msg = null;
        try {
            Matcher matcher = msgPattern.matcher(commText);
            if (matcher.matches()) {
                long ci;
                long id;
                int fl;
                String dt;

                ci = Long.parseLong(matcher.group(1));
                id = Long.parseLong(matcher.group(2));
                fl = Integer.parseInt(matcher.group(3));
                dt = matcher.group(4);
                msg = new Message(ci, id, fl, dt);
            }
        } catch (NumberFormatException | NullPointerException ignored) {
            String txt = commText == null ? "null" : commText;
            CommsManager.CommsLogVerbose("Message received from communication could not be parsed: " + txt);
            // Couldn't parse the message - null will be returned.
        }

        return msg;
    }

    /**
     * Creates a new instance of a Message from the provided information. The Message
     * will have a randomly generated identifier and an empty flag.
     *
     * @param data The Message data.
     */
    public Message(String data) {
        this(0, 0, 0, data);
    }

    /**
     * Creates a new instance of a Message from the provided information. The Message
     * will have an empty flag. If the identifier's value is 0, a randomly generated
     * identifier will be used. The identifying number is assumed to be unique.
     *
     * @param identifier    The Message's unique identifying number.
     * @param data          The Message's data.
     */
    public Message(long identifier, String data) {
        this(0, identifier, 0, data);
    }

    /**
     * Creates a new instance of a Message from the provided information. If the
     * identifier's value is 0, a randomly generated identifier will be used. The
     * identifying number is assumed to be unique.
     *
     * @param identifier    The Message's unique identifying number.
     * @param flags         The Message's flags.
     * @param data          The Message's data.
     */
    public Message(long identifier, int flags, String data) {
        this(0, identifier, flags, data);
    }

    /**
     * Creates a new instances of a Message from the provided information. This is a private
     * constructor as there is no valid reason to externally assign a server identifier. If the
     * server identifier provided is 0, the identifier found in the communications manager will
     * be used. If the identifier's value is 0, a randomly generated identifier will be used.
     * The identifying number is assumed to be unique. The server identifier is assumed to be
     * unique to the server, but each message from a given server should hold the same server
     * identifier.
     *
     * @param serverIdentifier  The Message's server identification number.
     * @param identifier        The Message's unique identifying number.
     * @param flags             The Message's flags.
     * @param data              The Message's data.
     */
    private Message(long serverIdentifier, long identifier, int flags, String data) {
        this.serverIdentifier = serverIdentifier == 0 ? CommsManager.SERVER_ID : serverIdentifier;
        this.identifier = identifier == 0 ? ThreadLocalRandom.current().nextLong(LRI, MAX_VALUE) : identifier;
        this.flags = flags;
        this.data = data;
    }

    /**
     * Gets the identifying number for the server on which the message originated. This
     * number is assumed to be unique to each server, but each message from a given server
     * is assumed to hold the same server identifier.
     *
     * @return The Message's originating server's identifying number.
     */
    public long getServerIdentifier() {
        return this.serverIdentifier;
    }

    /**
     * Gets the identifying number for this Message. This number is assumed to be unique.
     *
     * @return The Message's identifying number.
     */
    public long getIdentifier() {
        return this.identifier;
    }

    /**
     * Gets the flags for this Message. The meaning of a nonzero flag is caller defined.
     *
     * @return The Message's flags.
     */
    public int getFlags() {
        return this.flags;
    }

    /**
     * Gets the data for this Message.
     *
     * @return The Message's data.
     */
    public String getData() {
        return this.data;
    }

    /**
     * Gets the client response, blocking until one exists. This function is only
     * useful for client response. The Message does not hold a server response.
     *
     * @return The response to this Message.
     */
    public synchronized String getClientResponse() {
        while (!this.hasResponse) {
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }

        return this.response;
    }

    /**
     * Sets the client response, notifying all client response listeners. This will
     * not work for a server side response. A response can only be set once.
     *
     * @param response The response to set for this Message.
     */
    public synchronized void setClientResponse(String response) {
        if (!this.hasResponse) {
            CommsManager.CommsLogVerbose("Setting client response \"" + response +
                    "\" to message \"" + getData() + "\"");
            this.response = response;
            this.hasResponse = true;
            notifyAll();
        } else {
            CommsManager.CommsLogVerbose("Attempted to add extraneous client response \"" +
                    response + "\" to message \"" + getData() + "\"");
        }
    }

    /**
     * Sets the server response, adding the response message to the communications
     * manager.
     *
     * @param data The data of the response for this Message.
     */
    public void setServerResponse(String data) {
        CommsManager.CommsLogVerbose("Setting server response \"" + data +
                "\" to message \"" + getData() + "\"");
        Message resp = new Message(this.serverIdentifier, this.identifier, this.flags, data);
        CommsManager.putNextServerResponse(resp);
    }

    /**
     * Gets the String representation of this Message. Intended for use in serializing a
     * message over a network.
     *
     * @return The String representation of this Message.
     */
    @Override
    public String toString() {
        return "<" + this.serverIdentifier + "," + this.identifier + "," + this.flags + ">," + this.data;
    }
}
