package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionResponseHandler;
import org.seng462.webapp.UserCommand;
import org.seng462.webapp.UserCommandBuilder;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("/add")
public class AddResource
{
    @POST
    public void postAdd(@Suspended final AsyncResponse asyncResponse,
                        @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.ADD, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }
}
