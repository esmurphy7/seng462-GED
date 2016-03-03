package com.teamged.txserver;

/**
 * Created by DanielF on 2016-02-19.
 */
public class InternalLog {
    public static void Log(String log) {
        //System.out.println(log); // :(
    }

    public static void CacheDebug(String log) {
        if (TxMain.cacheDebugMode()) {
            System.out.println(log);
        }
    }
}
