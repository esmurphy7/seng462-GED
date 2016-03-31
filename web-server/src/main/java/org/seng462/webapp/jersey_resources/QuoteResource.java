package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionResponseHandler;
import org.seng462.webapp.UserCommand;
import org.seng462.webapp.UserCommandBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Evan on 1/19/2016.
 */
@Path("/quote")
public class QuoteResource {

    @GET
    public void getQuote(@Suspended final AsyncResponse asyncResponse,
                         @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.QUOTE, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }
}
