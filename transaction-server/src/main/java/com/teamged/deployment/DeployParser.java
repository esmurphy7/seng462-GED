package com.teamged.deployment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by DanielF on 2016-03-06.
 */
public class DeployParser {

    private static final String CONFIGFILE = "config.json";

    /**
     * Parses the default configuration file and assigns server constants.
     * @return The deployment settings as loaded from the config file.
     */
    public static DeploymentSettings parseConfig() {
        return parseConfig(null);
    }

    /**
     * Parses the configuration file at the provided file path and assigns server constants.
     * @param configPath The path of the config file. If null, the default name and location will be used.
     * @return The deployment settings as loaded from the config file.
     */
    public static DeploymentSettings parseConfig(String configPath) {
        DeploymentSettings settings = null;
        Path path;
        if (configPath == null) {
            path = Paths.get(System.getProperty("user.dir")).getParent().resolve(CONFIGFILE);
        } else {
            path = Paths.get(configPath);
        }

        System.out.println("Loading configuration file.");
        try {
            InputStream in = new FileInputStream(path.toString());
            Reader reader = new InputStreamReader(in);

            Gson gson = new GsonBuilder().create();
            DeploymentConfig config = gson.fromJson(reader, DeploymentConfig.class);
            if (config != null) {
                settings = config.getDeployments();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return settings;
    }
}
