package org.seng462.webapp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Evan on 3/8/2016.
 * Listener that can execute code before and after the application starts and ends.
 */
public class ApplicationContextListener implements ServletContextListener
{
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // load and deserialize the config.json file into the manager class
        ConfigurationManager.LoadConfig();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
