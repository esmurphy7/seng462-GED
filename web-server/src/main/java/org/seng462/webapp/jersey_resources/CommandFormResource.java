package org.seng462.webapp.jersey_resources;

import org.glassfish.jersey.server.mvc.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by Evan on 3/24/2016.
 */
@Path("/forms")
public class CommandFormResource
{
    /**
     *   Add forms
     */
    @GET
    @Path("/add")
    @Produces("text/html")
    public Viewable getAddForm()
    {
        return new Viewable("/add_form.ftl");
    }

    /**
     *   Quote forms
     */
    @GET
    @Path("/quote")
    @Produces("text/html")
    public Viewable getQuoteForm()
    {
        return new Viewable("/quote_form.ftl");
    }

    /**
     *   Buy forms
     */
    @GET
    @Path("/buy")
    @Produces("text/html")
    public Viewable getBuyForm()
    {
        return new Viewable("/buy_form.ftl");
    }

    @GET
    @Path("/buy/commit")
    @Produces("text/html")
    public Viewable getCommitBuyForm()
    {
        return new Viewable("/commitbuy_form.ftl");
    }

    @GET
    @Path("/buy/cancel")
    @Produces("text/html")
    public Viewable getCancelBuyForm()
    {
        return new Viewable("/cancelbuy_form.ftl");
    }

    @GET
    @Path("/buy/trigger/amount")
    @Produces("text/html")
    public Viewable getSetBuyAmountForm()
    {
        return new Viewable("/setbuyamount_form.ftl");
    }

    @GET
    @Path("/buy/trigger/stockprice")
    @Produces("text/html")
    public Viewable getSetBuyTriggerForm()
    {
        return new Viewable("/setbuytrigger_form.ftl");
    }

    @GET
    @Path("/buy/trigger/cancel")
    @Produces("text/html")
    public Viewable getCancelSetBuyForm()
    {
        return new Viewable("/cancelsetbuy_form.ftl");
    }

    /**
     * Sell Forms
     */
    @GET
    @Path("/sell")
    @Produces("text/html")
    public Viewable getSellForm()
    {
        return new Viewable("/sell_form.ftl");
    }

    @GET
    @Path("/sell/commit")
    @Produces("text/html")
    public Viewable getCommitSellForm()
    {
        return new Viewable("/commitsell_form.ftl");
    }

    @GET
    @Path("/sell/cancel")
    @Produces("text/html")
    public Viewable getCancelSellForm()
    {
        return new Viewable("/cancelsell_form.ftl");
    }

    @GET
    @Path("/sell/trigger/amount")
    @Produces("text/html")
    public Viewable getSetSellAmountForm()
    {
        return new Viewable("/setsellamount_form.ftl");
    }

    @GET
    @Path("/sell/trigger/stockprice")
    @Produces("text/html")
    public Viewable getSetSellTriggerForm()
    {
        return new Viewable("/setselltrigger_form.ftl");
    }

    @GET
    @Path("/sell/trigger/cancel")
    @Produces("text/html")
    public Viewable getCancelSetSellForm()
    {
        return new Viewable("/cancelsetsell_form.ftl");
    }

    /**
     * Display Summary Forms
     */
    @GET
    @Path("/summary")
    @Produces("text/html")
    public Viewable getDisplaySummaryForm()
    {
        return new Viewable("/displaysummary_form.ftl");
    }

    /**
     * Dumplog Forms
     */
    @GET
    @Path("/dumplog")
    @Produces("text/html")
    public Viewable getDumplogForm()
    {
        return new Viewable("/dumplog_form.ftl");
    }
}
