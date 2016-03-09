package com.teamged.proxyserver;

/**
 * Created by DanielF on 2016-03-08.
 */
public class InternalLog {
    public static void Log(String log) {
        if (ProxyMain.isVerbose()) {
            System.out.println(log);
        }
    }
}
