package com.teamged.fetchserver;

import com.teamged.deployment.DeployParser;
import com.teamged.deployment.DeploymentSettings;
import com.teamged.logging.Logger;

/**
 * Created by DanielF on 2016-03-08.
 */
public class FetchMain {

    public static DeploymentSettings Deployment;

    private static boolean verbose = false;
    private static String hostname;

    public static void main(String ... args) {
        parseArgs(args);
        Deployment = DeployParser.parseConfig();
        if (Deployment != null) {
            Logger.SetLogDestination(Deployment.getAuditServer());
            FetchMonitor.runServer();
        }

        System.out.println("Exiting server");
    }

    public static boolean isVerbose() {
        return verbose;
    }

    public static String getServerName() {
        return hostname;
    }

    private static void parseArgs(String ... args) {
        if (args != null && args.length != 0) {
            hostname = args[0];

            for (String arg : args) {
                if (arg.equals("-V")) {
                    verbose = true;
                }
            }
        }
    }
}
