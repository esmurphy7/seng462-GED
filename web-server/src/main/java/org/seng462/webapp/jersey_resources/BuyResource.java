package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionService;
import org.seng462.webapp.UserCommand;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Created by Evan on 1/19/2016.
 */
@Path("/buy")
public class BuyResource
{
    @POST
    public void postBuy(@QueryParam("globalSequence") String globalSequence,
                        @QueryParam("userSequence") String userSequence,
                        @QueryParam("userId") String userId,
                        @QueryParam("stockSymbol") String stockSymbol,
                        @QueryParam("amount") String amount)
    {

        // relay request to transaction server
        UserCommand buyCommand = new UserCommand(CommandCodes.BUY, globalSequence, userSequence, userId, stockSymbol, amount);
        Response response = TransactionService.sendCommand(buyCommand);
    }

    @POST @Path("/commit")
    public void postCommitBuy(@QueryParam("globalSequence") String globalSequence,
                              @QueryParam("userSequence") String userSequence,
                              @QueryParam("userId") String userId)
    {
        // relay command to transaction server
        UserCommand commitBuyCommand = new UserCommand(CommandCodes.COMMIT_BUY, globalSequence, userSequence, userId);
        Response response = TransactionService.sendCommand(commitBuyCommand);
    }

    @POST @Path("/cancel")
    public void postCancelBuy(@QueryParam("globalSequence") String globalSequence,
                              @QueryParam("userSequence") String userSequence,
                              @QueryParam("userId") String userId)
    {
        // relay command to transaction server
        UserCommand cancelBuyCommand = new UserCommand(CommandCodes.CANCEL_BUY, globalSequence, userSequence, userId);
        Response response = TransactionService.sendCommand(cancelBuyCommand);
    }

    @POST @Path("/trigger/amount")
    public void postSetBuyAmount(@QueryParam("globalSequence") String globalSequence,
                                 @QueryParam("userSequence") String userSequence,
                                 @QueryParam("userId") String userId,
                                 @QueryParam("stockSymbol") String stockSymbol,
                                 @QueryParam("amount") String amount)
    {
        // relay command to transaction server
        UserCommand setBuyAmountCommand = new UserCommand(CommandCodes.SET_BUY_AMOUNT, globalSequence, userSequence, userId, stockSymbol, amount);
        Response response = TransactionService.sendCommand(setBuyAmountCommand);
    }

    @POST @Path("/trigger/stockprice")
    public void postSetBuyTrigger(@QueryParam("globalSequence") String globalSequence,
                                  @QueryParam("userSequence") String userSequence,
                                  @QueryParam("userId") String userId,
                                  @QueryParam("stockSymbol") String stockSymbol,
                                  @QueryParam("amount") String amount)
    {
        // relay response to transaction server
        UserCommand setBuyTriggerCommand = new UserCommand(CommandCodes.SET_BUY_TRIGGER, globalSequence, userSequence, userId, stockSymbol, amount);
        Response response = TransactionService.sendCommand(setBuyTriggerCommand);
    }

    @POST @Path("/trigger/cancel")
    public void postCancelSetBuy(@QueryParam("globalSequence") String globalSequence,
                                 @QueryParam("userSequence") String userSequence,
                                 @QueryParam("userId") String userId,
                                 @QueryParam("stockSymbol") String stockSymbol)
    {
        // relay the command to the transaction server
        UserCommand cancelSetBuyCommand = new UserCommand(CommandCodes.CANCEL_SET_BUY, globalSequence, userSequence, userId, stockSymbol);
        Response response = TransactionService.sendCommand(cancelSetBuyCommand);
    }
}
