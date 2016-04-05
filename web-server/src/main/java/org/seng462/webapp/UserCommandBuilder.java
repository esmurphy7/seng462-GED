package org.seng462.webapp;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Evan on 3/3/2016.
 */
public class UserCommandBuilder
{
    // Build and return a user command based on a set of query parameters
    public static UserCommand Build(CommandCodes cmdCode, UriInfo uriInfo)
    {
        String globalSequence = "0";
        String userSequence = "0";
        HashMap<String,String> args = new HashMap<>();

        // Iterate and process each query parameter
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        for (Map.Entry<String, List<String>> queryParam : queryParams.entrySet())
        {
            String key = queryParam.getKey().toString();
            // assume that there is a single value in the query parameter (not a list)
            String value = queryParam.getValue().get(0).toString();

            // filter out the sequence numbers (they are processed differently)
            if(key.equals("globalSequence"))
            {
                globalSequence = value;
            }
            else if(key.equals("userSequence"))
            {
                userSequence = value;
            }
            else
            {
                args.put(key, value);
            }
        }
        UserCommand userCmd = new UserCommand(cmdCode, globalSequence, userSequence, args);
        return userCmd;
    }
}
