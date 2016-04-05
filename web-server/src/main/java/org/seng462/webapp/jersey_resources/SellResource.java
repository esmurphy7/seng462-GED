package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
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
    public void postSell(@Suspended final AsyncResponse asyncResponse,
                             @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.SELL, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }

    @POST @Path("/commit")
    public void postCommitSell(@Suspended final AsyncResponse asyncResponse,
                           @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.COMMIT_SELL, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }

    @POST @Path("/cancel")
    public void postCancelSell(@Suspended final AsyncResponse asyncResponse,
                                   @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.CANCEL_SELL, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }

    @POST @Path("/trigger/amount")
    public void postSetSellAmount(@Suspended final AsyncResponse asyncResponse,
                                       @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.SET_SELL_AMOUNT, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }

    @POST @Path("/trigger/stockprice")
    public void postSetSellTrigger(@Suspended final AsyncResponse asyncResponse,
                                       @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.SET_SELL_TRIGGER, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }

    @POST @Path("/trigger/cancel")
    public void postCancelSetSell(@Suspended final AsyncResponse asyncResponse,
                                      @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.CANCEL_SET_SELL, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }
}
