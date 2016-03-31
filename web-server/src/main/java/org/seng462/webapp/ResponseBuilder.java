package org.seng462.webapp;

import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Created by Evan on 3/27/2016.
 * Static class used to parse responses from the transaction server to build a Jersey response
 */
public class ResponseBuilder
{
    public static Response buildResponse(String txResponse)
    {
        // entity for response
        Map<String, Object> data = new HashMap<>();

        // Response from tx server is list of comma separated elements
        List<String> responseItems = Arrays.asList(txResponse.split(","));

        //debug prints
        System.out.println("Transaction response: "+txResponse);

        // handle error response from tx server
        if (responseItems.get(0).contains("ERROR"))
        {
            data.put("errorMessage", new ArrayList<>(responseItems.subList(1, responseItems.size())));
            Response response = Response.serverError().entity(data).build();
            return response;
        }

        //TODO: handle ADD, QUOTE, DUMPLOG, and DISPLAY_SUMMARY properly
        // build relevant data entity to include in response
        switch(CommandCodes.valueOf(responseItems.get(0)))
        {
            case NO_COMMAND:
                break;
            case ADD:
                break;
            case QUOTE:
                break;
            case BUY:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("stockCost", responseItems.get(3));
                break;
            case COMMIT_BUY:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("stockCost", responseItems.get(3));
                break;
            case CANCEL_BUY:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("account", responseItems.get(3));
                break;
            case SELL:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("stockCost", responseItems.get(3));
                break;
            case COMMIT_SELL:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("stockCost", responseItems.get(3));
                break;
            case CANCEL_SELL:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("account", responseItems.get(3));
                break;
            case SET_BUY_AMOUNT:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("sideAccount", responseItems.get(3));
                break;
            case CANCEL_SET_BUY:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("account", responseItems.get(3));
                break;
            case SET_BUY_TRIGGER:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("stockCost", responseItems.get(3));
                break;
            case SET_SELL_AMOUNT:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("desiredSell", responseItems.get(3));
                break;
            case SET_SELL_TRIGGER:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("desiredSell", responseItems.get(3));
                break;
            case CANCEL_SET_SELL:
                data.put("userId", responseItems.get(1));
                data.put("stockSymbol", responseItems.get(2));
                data.put("account", responseItems.get(3));
                break;
            case DUMPLOG:
                break;
            case DISPLAY_SUMMARY:
                break;
            case DUMPLOG_ROOT:
                break;
            default:
                break;
        }

        Response response = Response.ok().entity(data).build();
        return response;
    }
}
