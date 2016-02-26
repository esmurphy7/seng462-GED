package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionService;
import org.seng462.webapp.UserCommand;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/add")
public class AddResource
{
    @POST
    public void postAdd(@QueryParam("globalSequence") String globalSequence,
                        @QueryParam("userSequence") String userSequence,
                        @QueryParam("userId") String userId,
                        @QueryParam("amount") String amount)
    {
        // relay the request to transaction server's api
        UserCommand addCommand = new UserCommand(CommandCodes.ADD, globalSequence, userSequence, userId, amount);
        Response response = TransactionService.sendCommand(addCommand);
    }
}
