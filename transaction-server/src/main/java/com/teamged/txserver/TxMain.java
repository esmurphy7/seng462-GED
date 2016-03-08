package com.teamged.txserver;

import com.teamged.deployment.DeployParser;
import com.teamged.deployment.DeploymentSettings;

public class TxMain {
    public static DeploymentSettings Deployment;

    private static boolean cache_debug = false;
    private static boolean verbose = false;
    private static boolean l2 = true;
    private static boolean rt = true;
    private static String hostname;

    public static void main(String... args) {
        parseArgs(args);
        Deployment = DeployParser.parseConfig();
        if (Deployment != null) {
            TransactionMonitor.runServer();
        }
        System.out.println("Exiting server");
    }

    public static boolean isVerbose() {
        return verbose;
    }

    public static boolean cacheDebugMode() {
        return cache_debug;
    }

    public static boolean l2Enabled() {
        return l2;
    }

    public static boolean rtEnabled() {
        return rt;
    }

    public static String getServerName() {
        return hostname;
    }

    private static void parseArgs(String... args) {
        if (args != null && args.length != 0) {
            hostname = args[0];

            for (String arg : args) {
                if (arg.equals("-L1")) {
                    l2 = false;
                    rt = false;
                    cache_debug = true;
                    InternalLog.CacheDebug("L2 and real time caches disabled");
                    break;
                } else if (arg.equals("-L2")) {
                    rt = false;
                    cache_debug = true;
                    InternalLog.CacheDebug("Real time cache disabled");
                    break;
                } else if (arg.equals("-LL")) {
                    cache_debug = true;
                    InternalLog.CacheDebug("Cache debug enabled");
                } else if (arg.equals("-V")) {
                    verbose = true;
                    InternalLog.Log("Verbose mode");
                }
            }
        } else {
            System.out.println("Could not determine server index number. Defaulting to 0");
        }

        if (l2 && rt) {
            System.out.println("Vanilla server config running");
        }
    }
}

