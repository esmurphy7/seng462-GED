package com.teamged.comms.internal;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Long.MAX_VALUE;

/**
 * Created by DanielF on 2016-03-18.
 */
public class Message {
    private static final long LOWEST_RANDOM_IDENTIFIER = (long)Integer.MAX_VALUE + 1;
    private static final Pattern msgPattern = Pattern.compile("^<(\\d+),(\\d+)>,(.+)$");
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
                long id;
                int fl;
                String dt;

                id = Long.parseLong(matcher.group(1));
                fl = Integer.parseInt(matcher.group(2));
                dt = matcher.group(3);
                msg = new Message(id, fl, dt);
            }
        } catch (NumberFormatException | NullPointerException ignored) {
            String txt = commText == null ? "NULL" : commText;
            CommsManager.CommsLogVerbose("Message received from communication could not be parsed: " + txt); // TODO: Debugging line
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
        this(0, 0, data);
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
        this(identifier, 0, data);
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
        if (identifier == 0) {
            this.identifier = ThreadLocalRandom.current().nextLong(LOWEST_RANDOM_IDENTIFIER, MAX_VALUE);
        } else {
            this.identifier = identifier;
        }

        this.flags = flags;
        this.data = data;
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
        while (!hasResponse) {
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }

        return response;
    }

    /**
     * Sets the client response, notifying all client response listeners. This will
     * not work for a server side response. A response can only be set once.
     *
     * @param response The response to set for this Message.
     */
    public synchronized void setClientResponse(String response) {
        if (!hasResponse) {
            CommsManager.CommsLogVerbose("Setting client response \"" + response + "\" to message \"" + getData() + "\""); // TODO: Debugging line
            this.response = response;
            hasResponse = true;
            notifyAll();
        } else {
            CommsManager.CommsLogVerbose("Attempted to add extraneous client response \"" + response + "\" to message \"" + getData() + "\""); // TODO: Debugging line
        }
    }

    /**
     * Sets the server response, adding the response message to the communications
     * manager.
     *
     * @param data The data of the response for this Message.
     */
    public void setServerResponse(String data) {
        CommsManager.CommsLogVerbose("Setting server response \"" + data + "\" to message \"" + getData() + "\""); // TODO: Debugging line
        Message resp = new Message(this.identifier, this.flags, data);
        CommsManager.putNextServerResponse(resp);
    }

    @Override
    public String toString() {
        return "<" + identifier + "," + flags + ">," + data;
    }
}
