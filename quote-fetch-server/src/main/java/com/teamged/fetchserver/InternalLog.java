package com.teamged.fetchserver;

/**
 * Created by DanielF on 2016-03-08.
 */
public class InternalLog {
    public static void Log(String log) {
        if (FetchMain.isVerbose()) {
            System.out.println(log);
        }
    }
}
