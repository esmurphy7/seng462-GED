package org.seng462.webapp.jersey_resources;

import org.seng462.webapp.CommandCodes;
import org.seng462.webapp.TransactionResponseHandler;
import org.seng462.webapp.UserCommand;
import org.seng462.webapp.UserCommandBuilder;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Evan on 1/19/2016.
 */
@Path("/buy")
public class BuyResource
{
    @POST
    public void postBuy(@Suspended final AsyncResponse asyncResponse,
                            @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.BUY, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }

    @POST @Path("/commit")
    public void postCommitBuy(@Suspended final AsyncResponse asyncResponse,
                                  @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.COMMIT_BUY, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }

    @POST @Path("/cancel")
    public void postCancelBuy(@Suspended final AsyncResponse asyncResponse,
                                  @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.CANCEL_BUY, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }

    @POST @Path("/trigger/amount")
    public void postSetBuyAmount(@Suspended final AsyncResponse asyncResponse,
                                     @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.SET_BUY_AMOUNT, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }

    @POST @Path("/trigger/stockprice")
    public void postSetBuyTrigger(@Suspended final AsyncResponse asyncResponse,
                                      @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.SET_BUY_TRIGGER, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }

    @POST @Path("/trigger/cancel")
    public void postCancelSetBuy(@Suspended final AsyncResponse asyncResponse,
                                     @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.CANCEL_SET_BUY, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }
}
