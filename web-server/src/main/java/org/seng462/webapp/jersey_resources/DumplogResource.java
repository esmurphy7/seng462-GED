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
import java.util.List;

/**
 * Created by Evan on 2/7/2016.
 */
@Path("/dumplog")
public class DumplogResource
{
    @GET
    public void getDumplog(@Suspended final AsyncResponse asyncResponse,
                               @Context UriInfo uriInfo)
    {
        CommandCodes cmdCode;
        List<String> userIdParam = uriInfo.getQueryParameters().get("userId");
        String userId = (userIdParam != null) ? userIdParam.get(0).toString() : null;

        // type of dumplog command differs based on existence of userId
        if(userId != null && !userId.isEmpty())
        {
            cmdCode = CommandCodes.DUMPLOG;
        }
        else
        {
            cmdCode = CommandCodes.DUMPLOG_ROOT;
        }

        // build the command and relay it to transaction server
        UserCommand dumplogCommand = UserCommandBuilder.Build(cmdCode, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, dumplogCommand)).start();
    }
}
