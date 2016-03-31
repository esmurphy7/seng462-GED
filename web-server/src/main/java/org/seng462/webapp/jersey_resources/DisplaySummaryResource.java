package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Evan on 2/7/2016.
 */
@Path("/summary")
public class DisplaySummaryResource
{
    @GET
    public void getDisplaySummary(@Suspended final AsyncResponse asyncResponse,
                                      @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.DISPLAY_SUMMARY, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }
}
