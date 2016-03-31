package org.seng462.webapp;

import org.glassfish.jersey.server.mvc.Viewable;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evan on 1/21/2016.
 */
public class TemplateBuilder
{
    public static Viewable buildViewable(Response transactionRes, String templatePath)
    {
        // choose correct template based on the response from transaction server
        if(transactionRes.getStatusInfo() == Response.serverError())
        {
            // templatePath = ERROR_TEMPLATE_PATH;
        }

        // the data model to be passed to the template exists in the response entity
        //HashMap dataModel = (HashMap) transactionRes.getEntity();
        System.out.println("Building template with response entity: "+transactionRes.getEntity());
        HashMap dataModel = new HashMap();

        Viewable viewable = new Viewable(templatePath, dataModel);

        return viewable;
    }
}
