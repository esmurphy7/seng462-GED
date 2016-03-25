package org.seng462.webapp;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

/**
 * Created by Evan on 3/25/2016.
 */
public class TransactionThread implements Runnable
{
    private AsyncResponse asyncResponse;
    private UserCommand userCommand;

    public TransactionThread(AsyncResponse asyncResponse, UserCommand userCommand)
    {
        this.asyncResponse = asyncResponse;
        this.userCommand = userCommand;
    }

    @Override
    public void run()
    {
        Response response = TransactionService.sendCommand(userCommand);
        asyncResponse.resume(response);
    }
}
