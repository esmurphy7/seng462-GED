package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionService;
import org.seng462.webapp.UserCommand;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Created by Evan on 2/7/2016.
 */
@Path("/dumplog")
public class DumplogResource
{
    @GET
    public Response getDumplog(@QueryParam("userId") String userId,
                               @QueryParam("filename") String filename)
    {
        // type of dumplog command differs based on existence of userId
        UserCommand dumplogCommand;
        if(userId != null && !userId.isEmpty())
        {
            dumplogCommand = new UserCommand(CommandCodes.DUMPLOG, userId, filename);
        }
        else
        {
            dumplogCommand = new UserCommand(CommandCodes.DUMPLOG_ROOT, filename);
        }

        Response response = TransactionService.sendCommand(dumplogCommand);
        return response;
    }
}
