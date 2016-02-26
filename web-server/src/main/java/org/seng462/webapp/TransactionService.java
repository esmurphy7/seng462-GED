package org.seng462.webapp;

import jersey.repackaged.com.google.common.base.Joiner;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by Evan on 1/18/2016.
 */
public class TransactionService
{
    private static final String DEBUG_TX_SERVER_HOST = "localhost";
    private static final String TX_SERVER_HOST = "b136.seng.uvic.ca";
    private static final int DEBUG_TX_SERVER_PORT = 44440;
    private static final int TX_SERVER_PORT = 44440;

    // format the message to send to the transaction server
    // Format: [workload seq. no], [user seq. no], cmdCode, commandArgs, web server address, web server port
    private static String formatMessage(UserCommand userCommand) throws UnknownHostException {
        // parse command object into message
        ArrayList<String> message = new ArrayList<String>();

        //TODO: send workload and user sequence number in message
        //String workloadSeqNo = "["+userCommand.getWorkloadSeqNo()+"]";
        String workloadSeqNo = "["+0+"]";
        //String userSeqNo = "["+userCommand.getUserSeqNo()+"]";
        String userSeqNo = "["+0+"]";
        message.add(workloadSeqNo);
        message.add(userSeqNo);

        // include command code
        message.add(Integer.toString(userCommand.getCmdCode().ordinal()));

        // include command args
        message.addAll(userCommand.getArgs());

        // include timestamp
        message.add(Long.toString(System.currentTimeMillis()));

        //TODO: send currently active web server host and port in the message
        InetAddress ip = InetAddress.getLocalHost();
        String hostname = "0.0.0.0";
        //String hostname = ip.getHostName();
        message.add(hostname);
        message.add("8080");

        // format the message with separator
        String separator = ",";
        String messageFmt = Joiner.on(separator).join(message);
        return messageFmt;
    }

    public static Response sendCommand(UserCommand userCommand)
    {
        // format the message
        String message = "";
        try {
            message = TransactionService.formatMessage(userCommand);
        } catch (UnknownHostException e) {
            String errorMsg = "Cannot find local hostname to include in message" + "\n" + e.getMessage();
            e.printStackTrace();
            Response response = Response.serverError().entity(errorMsg).build();
            return response;
        }

        // send the message as a packet to the transaction server
        try
        {
            // open transaction socket
            Socket socket = new Socket(DEBUG_TX_SERVER_HOST, DEBUG_TX_SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // send packet over socket
            out.println(message);

            //TODO:// translate response from tx server into jersey response and return it
            Response response = Response.ok().build();
            return response;

        }catch (Exception e) {
            String errorMsg = "Could not connect to transaction server: "+  TX_SERVER_HOST + ":" + TX_SERVER_PORT + "\n" + e.getMessage();
            e.printStackTrace();
            Response response = Response.serverError().entity(errorMsg).build();
            return response;
        }
    }
}
