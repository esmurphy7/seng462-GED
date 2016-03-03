package com.teamged;

/**
 * Created by DanielF on 2016-01-30.
 */
public class ServerConstants {

    public static final int TRANSACTION_QUEUE_SIZE = 1000000;

    public static final String QUOTE_SERVER = "quoteserve.seng.uvic.ca";
    public static final int QUOTE_PORT = 4444;

    public static final String[] WEB_SERVERS = new String[] {
            "b130.seng.uvic.ca"
    };

    public static final String[] TX_SERVERS = new String[] {
            "b135.seng.uvic.ca"
    };

    public static final String[] AUDIT_SERVERS = new String[] {
            "b141.seng.uvic.ca"
    };

    public static final int[] TX_PORT_RANGE = new int[] {
            44440
            /*
            ,44441
            ,44442
            ,44443
            ,44444
            ,44445
            ,44446
            ,44447
            ,44448
            ,44449
            */
    };

    public static final int AUDIT_LOG_PORT = 44441;
    public static final int AUDIT_DUMP_PORT = 44441;

    public static final int PROCESSING_THREAD_COUNT = 4;
    public static final int THREAD_POOL_SIZE = 4;
}
