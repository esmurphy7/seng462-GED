package commands;

import com.sun.research.ws.wadl.HTTPMethods;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;


/**
 * Created by Evan on 1/18/2016.
 */
public class CommandTypes {
    // Command type keys must:
        // match the "cmdType" in incoming POST bodies
        // be mutually exclusive
    public static final String ADD              = "add";
    public static final String QUOTE            = "quote";
    public static final String BUY              = "buy";
    public static final String COMMIT_BUY       = "commit_buy";
    public static final String CANCEL_BUY       = "cancel_buy";
    public static final String SET_BUY_AMOUNT   = "set_buy_amount";
    public static final String SET_BUY_TRIGGER  = "set_buy_trigger";
    public static final String CANCEL_SET_BUY   = "cancel_set_buy";

    // Map commands to http methods (readonly)
    public static Map<String, HTTPMethods> HTTP_METHOD_MAP = Collections.unmodifiableMap(new Hashtable<String, HTTPMethods>()
    {
        {
            put(ADD, HTTPMethods.POST);
            put(QUOTE, HTTPMethods.GET);
            put(BUY, HTTPMethods.POST);
            put(COMMIT_BUY, HTTPMethods.POST);
            put(CANCEL_BUY, HTTPMethods.POST);
            put(SET_BUY_AMOUNT, HTTPMethods.POST);
            put(SET_BUY_TRIGGER, HTTPMethods.POST);
            put(CANCEL_SET_BUY, HTTPMethods.POST);
        }
    });
}
