package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionService;
import org.seng462.webapp.UserCommand;
import org.seng462.webapp.UserCommandBuilder;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Evan on 1/19/2016.
 */
@Path("/quote")
public class QuoteResource {

    @GET
    public Response getQuote(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.QUOTE, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }
}
