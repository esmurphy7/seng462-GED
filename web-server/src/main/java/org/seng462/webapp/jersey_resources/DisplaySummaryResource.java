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
@Path("/summary")
public class DisplaySummaryResource
{
    @GET
    public void getDisplaySummary(@QueryParam("globalSequence") String globalSequence,
                                  @QueryParam("userSequence") String userSequence,
                                  @QueryParam("userId") String userId)
    {
        UserCommand displaySummary = new UserCommand(CommandCodes.DISPLAY_SUMMARY, globalSequence, userSequence, userId);
        Response response = TransactionService.sendCommand(displaySummary);
    }
}
