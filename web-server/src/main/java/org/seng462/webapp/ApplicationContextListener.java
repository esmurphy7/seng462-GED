package org.seng462.webapp;

import com.teamged.comms.CommsInterface;
import com.teamged.deployment.deployments.TransactionServerDeployment;
import com.teamged.logging.Logger;

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
        // load and deserialize the config file file into the manager class
        ConfigurationManager.LoadConfig();

        // Initialize the Logger to send its logs to the audit server
        Logger.SetLogDestination(ConfigurationManager.DeploymentSettings.getAuditServer());

        // Initializes communications with the transaction server
        TransactionServerDeployment txServer = ConfigurationManager.DeploymentSettings.getTransactionServers();
        for (String server : txServer.getServers()) {
            CommsInterface.startClientCommunications(server, txServer.getPort(), txServer.getInternals().getCommunicationThreads());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        CommsInterface.endClientCommunications();
    }
}
