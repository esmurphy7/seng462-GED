package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;

@Path("/add")
public class AddResource
{
    @POST
    public void postAdd(@Suspended final AsyncResponse asyncResponse,
                        @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.ADD, uriInfo);
        new Thread(new TransactionThread(asyncResponse, userCommand)).start();
    }
}
