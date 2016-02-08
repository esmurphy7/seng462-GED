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
    public Response postBuy(@QueryParam("userId") String userId,
                            @QueryParam("stockSymbol") String stockSymbol,
                            @QueryParam("amount") String amount)
    {
        // validate the command
            // ex: dollars/cents positive int, stockSymbol all caps chars

        // relay request to transaction server
        UserCommand buyCommand = new UserCommand(CommandCodes.BUY, userId, stockSymbol, amount);
        Response response = TransactionService.sendCommand(buyCommand);

        //handle and return response
            // ex: handle error and render html
        return response;
    }

    @POST @Path("/commit")
    public Response postCommitBuy(@QueryParam("userId") String userId)
    {
        // validate the command

        // relay command to transaction server
        UserCommand commitBuyCommand = new UserCommand(CommandCodes.COMMIT_BUY, userId);
        Response response = TransactionService.sendCommand(commitBuyCommand);

        // handle and return ersponse
        return response;
    }

    @POST @Path("/cancel")
    public Response postCancelBuy(@QueryParam("userId") String userId)
    {
        // validate the command

        // relay command to transaction server
        UserCommand cancelBuyCommand = new UserCommand(CommandCodes.CANCEL_BUY, userId);
        Response response = TransactionService.sendCommand(cancelBuyCommand);

        // handle and return response
        return response;
    }

    @POST @Path("/trigger/amount")
    public Response postSetBuyAmount(@QueryParam("userId") String userId,
                                     @QueryParam("stockSymbol") String stockSymbol,
                                     @QueryParam("amount") String amount)
    {
        // validate the command
            // ex: dollars/cents positive int, stockSymbol all caps chars

        // relay command to transaction server
        UserCommand setBuyAmountCommand = new UserCommand(CommandCodes.SET_BUY_AMOUNT, userId, stockSymbol, amount);
        Response response = TransactionService.sendCommand(setBuyAmountCommand);

        // handle and return ersponse
        return response;
    }

    @POST @Path("/trigger/stockprice")
    public Response postSetBuyTrigger(@QueryParam("userId") String userId,
                                      @QueryParam("stockSymbol") String stockSymbol,
                                      @QueryParam("amount") String amount)
    {
        // validate the command
            // ex: dollars/cents positive int, stockSymbol all caps chars

        // relay response to transaction server
        UserCommand setBuyTriggerCommand = new UserCommand(CommandCodes.SET_BUY_TRIGGER, userId, stockSymbol, amount);
        Response response = TransactionService.sendCommand(setBuyTriggerCommand);

        // handle and return response
        return response;
    }

    @POST @Path("/trigger/cancel")
    public Response postCancelSetBuy(@QueryParam("userId") String userId,
                                     @QueryParam("stockSymbol") String stockSymbol)
    {
        // validate the command
            // ex: stockSymbol all caps chars

        // relay the command to the transaction server
        UserCommand cancelSetBuyCommand = new UserCommand(CommandCodes.CANCEL_SET_BUY, userId, stockSymbol);
        Response response = TransactionService.sendCommand(cancelSetBuyCommand);

        // handle adn return response
        return response;
    }
}
