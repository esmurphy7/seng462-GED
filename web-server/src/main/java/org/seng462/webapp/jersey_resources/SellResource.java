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
 * Created by Evan on 2/7/2016.
 */
@Path("/sell")
public class SellResource
{
    @POST
    public Response postSell(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.SELL, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }

    @POST @Path("/commit")
    public Response postCommitSell(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.COMMIT_SELL, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }

    @POST @Path("/cancel")
    public Response postCancelSell(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.CANCEL_SELL, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }

    @POST @Path("/trigger/amount")
    public Response postSetSellAmmount(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.SET_SELL_AMOUNT, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }

    @POST @Path("/trigger/stockprice")
    public Response postSetSellTrigger(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.SET_SELL_TRIGGER, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }

    @POST @Path("/trigger/cancel")
    public Response postCancelSetSell(@Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.CANCEL_SET_SELL, uriInfo);
        Response response = TransactionService.sendCommand(userCommand);
        return response;
    }
}
