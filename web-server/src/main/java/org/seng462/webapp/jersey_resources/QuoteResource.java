package org.seng462.webapp.jersey_resources;

import org.glassfish.jersey.server.mvc.Template;
import org.glassfish.jersey.server.mvc.Viewable;
import org.seng462.webapp.*;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Evan on 1/19/2016.
 */
@Path("/quote")
public class QuoteResource {

    @GET
    public void getQuote(@Suspended final AsyncResponse asyncResponse,
                         @Context UriInfo uriInfo)
    {
        // build the command and relay it to transaction server
        UserCommand userCommand = UserCommandBuilder.Build(CommandCodes.QUOTE, uriInfo);
        new Thread(new TransactionResponseHandler(asyncResponse, userCommand)).start();
    }
}
