package org.seng462.webapp;

import org.glassfish.jersey.server.mvc.Viewable;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

/**
 * Created by Evan on 3/25/2016.
 */
public class TransactionResponseHandler implements Runnable
{
    private AsyncResponse asyncResponse;
    private UserCommand userCommand;
    private String template;

    public TransactionResponseHandler(AsyncResponse asyncResponse, UserCommand userCommand)
    {
        this.asyncResponse = asyncResponse;
        this.userCommand = userCommand;
    }

    /**
     * Handler can be specified to build and return a template for the response it's processing
     */
    public TransactionResponseHandler(AsyncResponse asyncResponse, UserCommand userCommand, String template)
    {
        this(asyncResponse, userCommand);
        this.template = template;
    }

    @Override
    public void run()
    {
        Response response = TransactionService.sendCommand(userCommand);

        // return template for response if specified
        if(template != null)
        {
            Viewable viewable = TemplateBuilder.buildViewable(response, template);
            asyncResponse.resume(viewable);
        }
        else
        {
            asyncResponse.resume(response);
        }
    }
}
