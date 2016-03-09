package teamgid.deploy462;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import teamgid.deploy462.base.BaseDeployment;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by DanielF on 2016-02-15.
 */
public class DeployLaunch {

    private static final String WORKLOAD_GENERATOR_TYPE = "wg";
    private static final String WEB_LB_TYPE = "weblb";
    private static final String WEB_TYPE = "web";
    private static final String TX_TYPE = "tx";
    private static final String AUDIT_TYPE = "audit";
    private static final String CACHE_TYPE = "cache";
    private static final String QUOTE_FETCH_TYPE = "fetch";
    private static final String ALL_TYPE = "all";

    private static String username;
    private static String password;

    public static void main(String[] args) {

        Path configPath = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("main").resolve("resources").resolve("config.json");

        try {

            System.out.println("Reading config.json...");
            System.out.println();

            // Load deployment config
            InputStream inputStream = new FileInputStream(configPath.toString());
            Reader reader = new InputStreamReader(inputStream);

            Gson gson = new GsonBuilder().create();
            DeploymentConfig deploymentConfig = gson.fromJson(reader, DeploymentConfig.class);

            // Let user choose deployment
            List<BaseDeployment> deployments = chooseDeployments(deploymentConfig);

            // Get user's name and password
            getUserInfo();

            // Deploy each
            for (BaseDeployment deployment : deployments) {

                deployment.deploy(username, password, deploymentConfig);
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
    }

    private static List<BaseDeployment> chooseDeployments(DeploymentConfig deploymentConfig) {

        List<BaseDeployment> deployments = new ArrayList<BaseDeployment>();

        boolean hasDeploymentType = false;
        Scanner userInput = new Scanner(System.in);

        while (!hasDeploymentType) {

            System.out.println("Deployment options:" +
                    "\n    '" + WORKLOAD_GENERATOR_TYPE + "' for Workload Generator deployment" +
                    "\n    '" + WEB_LB_TYPE + "' for Web Load Balancer deployment" +
                    "\n    '" + WEB_TYPE + "' for Web Server deployment" +
                    "\n    '" + TX_TYPE + "' for Transaction Server deployment" +
                    "\n    '" + AUDIT_TYPE + "' for Audit Server deployment" +
                    "\n    '" + CACHE_TYPE + "' for Cache Server deployment" +
                    "\n    '" + QUOTE_FETCH_TYPE + "' for Quote Fetch Server deployment" +
                    "\n    '" + ALL_TYPE + "' for all deployments"
            );

            System.out.println();
            System.out.println("The configuration for each deployment is in config.json");
            System.out.println();
            System.out.println("Enter deployment type: ");
            System.out.println("Which server do you wish to deploy? (wg, weblb, web, tx, audit, cache, fetch, all):");
            String input = userInput.nextLine();

            if (input.equals(ALL_TYPE)) {

                deployments.add(deploymentConfig.getDeployments().getWorkloadGenerator());
                deployments.add(deploymentConfig.getDeployments().getWebLoadBalancer());
                deployments.add(deploymentConfig.getDeployments().getWebServers());
                deployments.add(deploymentConfig.getDeployments().getTransactionServers());
                deployments.add(deploymentConfig.getDeployments().getAuditServer());
                deployments.add(deploymentConfig.getDeployments().getCacheServer());
                deployments.add(deploymentConfig.getDeployments().getFetchServer());
                hasDeploymentType = true;

            } else if (input.equals(WORKLOAD_GENERATOR_TYPE)) {

                deployments.add(deploymentConfig.getDeployments().getWorkloadGenerator());
                hasDeploymentType = true;

            } else if (input.equals(WEB_LB_TYPE)) {

                deployments.add(deploymentConfig.getDeployments().getWebLoadBalancer());
                hasDeploymentType = true;

            } else if (input.equals(WEB_TYPE)) {

                deployments.add(deploymentConfig.getDeployments().getWebServers());
                hasDeploymentType = true;

            } else if (input.equals(TX_TYPE)) {

                deployments.add(deploymentConfig.getDeployments().getTransactionServers());
                hasDeploymentType = true;

            } else if (input.equals(AUDIT_TYPE)) {

                deployments.add(deploymentConfig.getDeployments().getAuditServer());
                hasDeploymentType = true;

            } else if (input.equals(CACHE_TYPE)) {

                deployments.add(deploymentConfig.getDeployments().getCacheServer());
                hasDeploymentType = true;

            }  else if (input.equals(QUOTE_FETCH_TYPE)) {

                deployments.add(deploymentConfig.getDeployments().getFetchServer());
                hasDeploymentType = true;

            } else {

                System.out.println("Not a valid server option");
            }
        }

        return deployments;
    }

    private static void getUserInfo() {

        Scanner userInput = new Scanner(System.in);

        System.out.println("Enter username: ");
        username = userInput.nextLine();

        System.out.println("Enter password: ");
        password = userInput.nextLine();

        // Clear console to hide password
        for (int i = 0; i < 30; i++) System.out.println();
    }
}
