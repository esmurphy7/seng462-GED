package org.seng462.webapp;

import org.glassfish.jersey.server.mvc.Viewable;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evan on 1/21/2016.
 */
public class TemplateService
{
    public static Viewable getViewable(UserCommand userCommand, String commandKey, Response transactionRes, String templatePath)
    {
        // build the data model to pass to the template
        Map dataModel = new HashMap<String, Object>();
        dataModel.put(commandKey, userCommand);

        // choose correct template based on the response from transaction server
        if(transactionRes.getStatusInfo() == Response.serverError())
        {
            // templatePath = ERROR_TEMPLATE_PATH;
        }

        Viewable viewable = new Viewable(templatePath, dataModel);

        return viewable;
    }
}
