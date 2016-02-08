package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionService;
import org.seng462.webapp.UserCommand;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Created by Evan on 2/7/2016.
 */
@Path("/sell")
public class SellResource
{
    @POST
    public Response postSell(@QueryParam("userId") String userId,
                             @QueryParam("amount") String amount,
                             @QueryParam("stockSymbol") String stockSymbol)
    {
        UserCommand sellCommand = new UserCommand(CommandCodes.SELL, userId, amount, stockSymbol);
        Response response = TransactionService.sendCommand(sellCommand);
        return response;
    }

    @POST @Path("/commit")
    public Response postCommitSell(@QueryParam("userId") String userId)
    {
        UserCommand commitSell = new UserCommand(CommandCodes.COMMIT_SELL, userId);
        Response response = TransactionService.sendCommand(commitSell);
        return response;
    }

    @POST @Path("/cancel")
    public Response postCancelSell(@QueryParam("userId") String userId)
    {
        UserCommand cancelSell = new UserCommand(CommandCodes.CANCEL_SELL, userId);
        Response response = TransactionService.sendCommand(cancelSell);
        return response;
    }

    @POST @Path("/trigger/amount")
    public Response postSetSellAmmount(@QueryParam("userId") String userId,
                                       @QueryParam("amount") String amount,
                                       @QueryParam("stockSymbol") String stockSymbol)
    {
        UserCommand setSellAmount = new UserCommand(CommandCodes.SET_SELL_AMOUNT, userId, amount, stockSymbol);
        Response response = TransactionService.sendCommand(setSellAmount);
        return response;
    }

    @POST @Path("/trigger/stockprice")
    public Response postSetSellTrigger(@QueryParam("userId") String userId,
                                       @QueryParam("amount") String amount,
                                       @QueryParam("stockSymbol") String stockSymbol)
    {
        UserCommand setSellTrigger = new UserCommand(CommandCodes.SET_SELL_TRIGGER, userId, amount, stockSymbol);
        Response response = TransactionService.sendCommand(setSellTrigger);
        return response;
    }

    @POST @Path("/trigger/cancel")
    public Response postCancelSetSell(@QueryParam("userId") String userId,
                                      @QueryParam("stockSymbol") String stockSymbol)
    {
        UserCommand cancelSetSell = new UserCommand(CommandCodes.CANCEL_SET_SELL, userId, stockSymbol);
        Response response = TransactionService.sendCommand(cancelSetSell);
        return response;
    }
}
