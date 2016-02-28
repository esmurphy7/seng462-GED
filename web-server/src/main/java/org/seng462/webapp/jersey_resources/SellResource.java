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
    public void postSell(@QueryParam("globalSequence") String globalSequence,
                         @QueryParam("userSequence") String userSequence,
                         @QueryParam("userId") String userId,
                         @QueryParam("amount") String amount,
                         @QueryParam("stockSymbol") String stockSymbol)
    {
        UserCommand sellCommand = new UserCommand(CommandCodes.SELL, globalSequence, userSequence, userId, stockSymbol, amount);
        Response response = TransactionService.sendCommand(sellCommand);
    }

    @POST @Path("/commit")
    public void postCommitSell(@QueryParam("globalSequence") String globalSequence,
                               @QueryParam("userSequence") String userSequence,
                               @QueryParam("userId") String userId)
    {
        UserCommand commitSell = new UserCommand(CommandCodes.COMMIT_SELL, globalSequence, userSequence, userId);
        Response response = TransactionService.sendCommand(commitSell);
    }

    @POST @Path("/cancel")
    public void postCancelSell(@QueryParam("globalSequence") String globalSequence,
                               @QueryParam("userSequence") String userSequence,
                               @QueryParam("userId") String userId)
    {
        UserCommand cancelSell = new UserCommand(CommandCodes.CANCEL_SELL, globalSequence, userSequence, userId);
        Response response = TransactionService.sendCommand(cancelSell);
    }

    @POST @Path("/trigger/amount")
    public void postSetSellAmmount(@QueryParam("globalSequence") String globalSequence,
                                   @QueryParam("userSequence") String userSequence,
                                   @QueryParam("userId") String userId,
                                   @QueryParam("amount") String amount,
                                   @QueryParam("stockSymbol") String stockSymbol)
    {
        UserCommand setSellAmount = new UserCommand(CommandCodes.SET_SELL_AMOUNT, globalSequence, userSequence, userId, stockSymbol, amount);
        Response response = TransactionService.sendCommand(setSellAmount);
    }

    @POST @Path("/trigger/stockprice")
    public void postSetSellTrigger(@QueryParam("globalSequence") String globalSequence,
                                   @QueryParam("userSequence") String userSequence,
                                   @QueryParam("userId") String userId,
                                   @QueryParam("amount") String amount,
                                   @QueryParam("stockSymbol") String stockSymbol)
    {
        UserCommand setSellTrigger = new UserCommand(CommandCodes.SET_SELL_TRIGGER, globalSequence, userSequence, userId, stockSymbol, amount);
        Response response = TransactionService.sendCommand(setSellTrigger);
    }

    @POST @Path("/trigger/cancel")
    public void postCancelSetSell(@QueryParam("globalSequence") String globalSequence,
                                  @QueryParam("userSequence") String userSequence,
                                  @QueryParam("userId") String userId,
                                  @QueryParam("stockSymbol") String stockSymbol)
    {
        UserCommand cancelSetSell = new UserCommand(CommandCodes.CANCEL_SET_SELL, globalSequence, userSequence, userId, stockSymbol);
        Response response = TransactionService.sendCommand(cancelSetSell);
    }
}
