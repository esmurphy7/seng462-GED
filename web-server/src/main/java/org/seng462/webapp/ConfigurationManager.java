package org.seng462.webapp;

import com.teamged.deployment.DeployParser;
import com.teamged.deployment.DeploymentSettings;

/**
 * Class used to load and handle the system config file.
 * Wrapper around common deployment parser.
 */
public class ConfigurationManager
{
    public static DeploymentSettings DeploymentSettings;

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
        DeploymentSettings settings = DeployParser.parseConfig(configPath);

        DeploymentSettings = settings;
    }
}
