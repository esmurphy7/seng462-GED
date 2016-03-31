package org.seng462.webapp.jersey_resources;

import org.glassfish.jersey.server.mvc.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by Evan on 3/24/2016.
 */
@Path("/forms")
public class CommandFormResource
{
    @GET
    @Path("/add")
    @Produces("text/html")
    public Viewable getAddForm()
    {
        return new Viewable("/add_form.ftl");
    }

    @GET
    @Path("/quote")
    @Produces("text/html")
    public Viewable getQuoteForm()
    {
        return new Viewable("/quote_form.ftl");
    }
}
