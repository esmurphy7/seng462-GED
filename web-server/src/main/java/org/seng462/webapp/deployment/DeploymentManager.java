package org.seng462.webapp.deployment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by DanielF on 2016-03-06.
 */
public class DeploymentManager {

    public static DeploymentSettings DeploymentSettings;

    private static final String CONFIGFILE = "/config.json";

    /**
     * Loads and deserializes the default configuration file.
     */
    public static void LoadConfig() {
        LoadConfig(null);
    }

    /**
     * Loads and deserializes the configuration file at the provided file path.
     * @param configPath The path of the config file. If null, the default name and location will be used.
     */
    public static void LoadConfig(String configPath)
    {
        DeploymentSettings settings = null;
        if (configPath == null) {
            configPath = CONFIGFILE;
        }

        System.out.println("Loading configuration file.");
        try {
            URI uri = DeploymentManager.class.getResource(configPath).toURI();
            InputStream in = new FileInputStream(uri.getPath());
            Reader reader = new InputStreamReader(in);

            Gson gson = new GsonBuilder().create();
            DeploymentConfig config = gson.fromJson(reader, DeploymentConfig.class);
            if (config != null) {
                settings = config.getDeployments();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        DeploymentSettings = settings;
    }
}
