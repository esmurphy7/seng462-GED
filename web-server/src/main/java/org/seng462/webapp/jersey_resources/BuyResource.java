package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionService;
import org.seng462.webapp.UserCommand;
import org.seng462.webapp.UserCommandBuilder;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Evan on 1/19/2016.
 */
@Path("/buy")
public class BuyResource
{
    @POST
    public Response postBuy(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.BUY, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }

    @POST @Path("/commit")
    public Response postCommitBuy(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.COMMIT_BUY, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }

    @POST @Path("/cancel")
    public Response postCancelBuy(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.CANCEL_BUY, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }

    @POST @Path("/trigger/amount")
    public Response postSetBuyAmount(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.SET_BUY_AMOUNT, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }

    @POST @Path("/trigger/stockprice")
    public Response postSetBuyTrigger(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.SET_BUY_TRIGGER, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }

    @POST @Path("/trigger/cancel")
    public Response postCancelSetBuy(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.CANCEL_SET_BUY, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }
}
