package org.seng462.webapp;

import com.sun.research.ws.wadl.HTTPMethods;
import jersey.repackaged.com.google.common.base.Joiner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 1/18/2016.
 */
public class TransactionService {

    private static final String TX_SERVER_HOST = "http://httpbin.org";
    private static final int TX_SERVER_PORT = 44440;

    public static Response sendCommand(UserCommand userCommand)
    {
        /*
        // get the url of the transaction server
        URI baseUri = TransactionService.getTargetURL();

        // determine the corresponding http method to use
        HTTPMethods httpMethod = CommandTypes.HTTP_METHOD_MAP.get(command.getCmdType());

        // build the request
        Invocation.Builder requestBuilder = ClientBuilder.newClient().target(baseUri)
                                    //.path(command.getCmdType()) // append command type to target url ex: "http://<transaction-server-home>/add"
                                    .path(httpMethod.toString().toLowerCase()) // DEBUG: append http method to url ex: "http://httpbin.org/get"
                                    .request(MediaType.APPLICATION_JSON_TYPE);
        Invocation request = TransactionService.buildRequest(requestBuilder, httpMethod, command);

        // execute the command request
        Response response = request.invoke();

        return response;
        */

        // parse command object into packet message
        List<String> message = new ArrayList<String>();
        message.add(Integer.toString(userCommand.getCmdCode()));
        message.addAll(userCommand.getArgs());
        message.add(Long.toString(System.currentTimeMillis()));
        //TODO: send currently active web server host and port in the message

        // format the message
        String separator = ",";
        String messageFmt = Joiner.on(separator).join(message);

        Response response = null;

        try
        {
            // get the address of the transaction server
            InetAddress address = InetAddress.getByName(TX_SERVER_HOST);

            // open transaction socket
            Socket socket = new Socket(TX_SERVER_HOST, TX_SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // send packet over socket
            out.print(messageFmt);

            //TODO:// translate response from tx server into jersey response and return it

        }catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
