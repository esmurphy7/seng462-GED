package org.seng462.webapp.jersey_resources;

import org.glassfish.jersey.server.mvc.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by Evan on 3/24/2016.
 */
@Path("/")
public class LandingResource
{
    @GET
    @Produces("text/html")
    public Viewable getLandingPage()
    {
        return new Viewable("/index.ftl");
    }
}
