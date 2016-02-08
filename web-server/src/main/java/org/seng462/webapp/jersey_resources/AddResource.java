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
    public Response postAdd(@QueryParam("userId") String userId,
                            @QueryParam("amount") String amount)
    {
        // validate the command
            // ex: workloadId, dollars, cents are positive ints

        // relay the request to transaction server's api
        UserCommand addCommand = new UserCommand(CommandCodes.ADD, amount);
        Response response = TransactionService.sendCommand(addCommand);

        // return response
            // ex: handling errors and render html page
        return response;
    }
}
