package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionService;
import org.seng462.webapp.UserCommand;
import org.seng462.webapp.UserCommandBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Evan on 2/7/2016.
 */
@Path("/dumplog")
public class DumplogResource
{
    @GET
    public Response getDumplog(@Context UriInfo uriInfo)
    {
        CommandCodes cmdCode;
        String defaultUserSeqNo = "99999";
        String userId = uriInfo.getQueryParameters().get("userId").toString();

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
        Response response = TransactionService.sendCommand(dumplogCommand);
        return response;
    }
}
