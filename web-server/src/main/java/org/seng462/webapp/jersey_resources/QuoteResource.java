package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionService;
import org.seng462.webapp.UserCommand;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by Evan on 1/19/2016.
 */
@Path("/quote")
public class QuoteResource {

    @GET
    public Response getQuote(@QueryParam("userId") String userId,
                             @QueryParam("stockSymbol") String stockSymbol)
    {
        // build the command and relay it to transaction server
        UserCommand quoteCommand = new UserCommand(CommandCodes.QUOTE, userId, stockSymbol);
        Response response = TransactionService.sendCommand(quoteCommand);

        return response;
    }
}
