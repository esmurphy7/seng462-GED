package com.teamged.txserver;

public class TxMain {

    private static boolean cache_debug = false;
    private static boolean l2 = true;
    private static boolean rt = true;

    public static void main(String[] args) {
        if (args != null) {
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
                }
            }
        }

        if (l2 && rt) {
            System.out.println("Vanilla server config running");
        }

        TransactionMonitor.runServer();
        System.out.println("Exiting server");
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
}

