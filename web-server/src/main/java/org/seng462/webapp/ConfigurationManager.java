package org.seng462.webapp;

import com.teamged.deployment.DeployParser;
import com.teamged.deployment.DeploymentSettings;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * Class used to load and handle the system config file.
 * Wrapper around common deployment parser.
 */
public class ConfigurationManager
{
    public static DeploymentSettings DeploymentSettings;

    private static final String DEFAULT_CONFIG_PATH = "/seng/scratch/group4/tomcat/bin/config.json";
    private static final String CONFIG_RESOURCE_PATH = "/config.json";
    /**
     * Loads and deserializes the default configuration file.
     */
    public static void LoadConfig() {
        LoadConfig(null);
    }

    /**
     * Loads and deserializes the configuration file at the provided file path.
     * Searches default directory for the config file first, then searches the application's resources if failed.
     * @param configPath The path of the config file. If null, the default name and location will be used.
     */
    public static void LoadConfig(String configPath)
    {
        DeploymentSettings settings;

        // search for the config file in the default directory first
        if(configPath == null)
        {
            configPath = DEFAULT_CONFIG_PATH;
        }
        settings = DeployParser.parseConfig(configPath);

        // search the webapp's resources for config.json if first method failed
        if(settings == null)
        {
            try {
                URL configResource = ConfigurationManager.class.getResource(CONFIG_RESOURCE_PATH);
                configPath = (configResource!= null) ? Paths.get(configResource.toURI()).toString() : null;
                settings = DeployParser.parseConfig(configPath);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        if (settings != null)
        {
            System.out.println("Configuration file loaded");
        }

        DeploymentSettings = settings;
    }
}
