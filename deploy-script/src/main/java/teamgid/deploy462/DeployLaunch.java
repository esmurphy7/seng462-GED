package teamgid.deploy462;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by DanielF on 2016-02-15.
 */
public class DeployLaunch {
    private static final String TX_TYPE = "tx";
    private static final String WEB_TYPE = "web";
    private static final String AUDIT_TYPE = "audit";
    private static final String ALL_TYPE = "all";
    private static final String TEST_TYPE = "test";

    private static String username;
    private static String password;
    private static String deploytype; // TODO: Support different deployments
    private static int txDeployInst = -1;
    private static int webDeployInst = -1;
    private static int auditDeployInst = -1;

    /**
     * Deployment script for transaction server. Could easily be extended to deploy other servers.
     * Requires user input - first for deployment type (currently only works with 'tx') and deployment server,
     * and then username and password for logging on to the seng lab machine. To add more server options, edit
     * the StaticConstants file in the deploy-script project.
     *
     * Assumes that the transaction-server folder is in a directory level with deploy-script and that the program
     * was launched from the deploy-script folder.
     *
     * Copies the contents of the transaction-server project to the specified server. The contents will be placed in
     * the /seng/scratch/group4 directory, under a new (or replaced) src directory. These files will be compiled (note
     * that if new folders are added to the transaction-server project, those will need to be added to the compile
     * definitions in the 'deployTxServer' method below.
     *
     * A bash script will be copied to the target server, given execute permissions, and its line endings will be
     * replaced with Unix LF instead of Windows CRLF. This way, the transaction server can be run by going to the
     * /seng/scratch/group4 directory and typing:
     *      ./txrun.sh
     *
     * TODO:
     * - Automatically build the list of directories to compile (could do a recursive traversal of the
     * transaction-server folders to check for which ones have .java files, and then add those to a compile list)
     * - Support command line args
     * - Allow for ssh key usage to avoid having to pass a plaintext password in
     * - Support test server deployments (maybe ... this will soon be deprecated)
     * - Add other deployments
     * - Generate a different target directory to avoid clobbering other 'src' folders, in case something else uses
     * that name
     * - Integrate with the other 'StaticConstants.java' currently in the transaction-server project
     * @param args
     */
    public static void main(String[] args) {
        // check if user wants to deploy transaction and/or web server
        getUserPref();

        // get user's name and password
        getUserInfo();

        // connect and deploy to transaction and/or web server
        if(txDeployInst != -1)
        {
            boolean connected = false;
            final SSHClient txClient = new SSHClient();
            connected = connectToTxServer(connected, txClient);

            if (connected) {
                try {
                    connected = authorizeUser(txClient);
                } catch (IOException e) {
                    // Have to try catch because disconnecting can throw an exception...
                    e.printStackTrace();
                }
            }
            if (connected) {
                deployTxServer(txClient);
            }
        }

        if(webDeployInst != -1)
        {
            boolean connected = false;
            final SSHClient webClient = new SSHClient();
            connected = connectToWebServer(connected, webClient);

            if (connected) {
                try {
                    connected = authorizeUser(webClient);
                } catch (IOException e) {
                    // Have to try catch because disconnecting can throw an exception...
                    e.printStackTrace();
                }
            }
            if (connected) {
                deployWebServer(webClient);
            }
        }

        if (auditDeployInst != -1) {
            final SSHClient sshClient = new SSHClient();
            if (connectToAuditServer(sshClient)) {

            }
        }
    }

    private static boolean authorizeUser(SSHClient client) throws IOException {
        boolean connected;
        connected = false;
        try {
            client.authPassword(username, password);
            connected = true;
        } catch (UserAuthException e) {
            e.printStackTrace();
            client.disconnect();
        } catch (TransportException e) {
            e.printStackTrace();
            client.disconnect();
        }
        return connected;
    }

    private static void getUserPref()
    {
        List<String> deployments = new ArrayList<String>();
        boolean hasDeploymentType = false;
        Scanner userInput = new Scanner(System.in);
        while (!hasDeploymentType) {
            System.out.println("Deployment options:" +
                    "\n    '" + TX_TYPE + "' for Transaction Server deployment" +
                    "\n    '" + WEB_TYPE + "' for Web Server deployment" +
                    "\n    '" + AUDIT_TYPE + "' for Audit Server deployment" +
                    "\n    '" + ALL_TYPE + "' for all deployments" +
                    "\nEnter deployment type: "
            );

            System.out.println("Which server do you wish to deploy? (tx, web, audit, all):");
            String input = userInput.nextLine();
            if (input.equals(ALL_TYPE)) {
                deployments.add(TX_TYPE);
                deployments.add(WEB_TYPE);
                deployments.add(AUDIT_TYPE);
                hasDeploymentType = true;
            } else if (input.equals(WEB_TYPE)) {
                deployments.add(WEB_TYPE);
                hasDeploymentType = true;
            } else if (input.equals(TX_TYPE)) {
                deployments.add(TX_TYPE);
                hasDeploymentType = true;
            } else if (input.equals(AUDIT_TYPE)) {
                deployments.add(AUDIT_TYPE);
                hasDeploymentType = true;
            } else {
                System.out.println("Not a valid server option");
            }
        }

        int maxOpts = 0;
        String[] options = null;
        for (String deployment : deployments) {
            if (deployment.equals(TX_TYPE)) {
                maxOpts = StaticConstants.TX_SERVERS.length;
                options = StaticConstants.TX_SERVERS;
            } else if (deployment.equals(WEB_TYPE)) {
                maxOpts = StaticConstants.WEB_SERVERS.length;
                options = StaticConstants.WEB_SERVERS;
            } else if (deployment.equals(AUDIT_TYPE)) {
                maxOpts = StaticConstants.AUDIT_SERVERS.length;
                options = StaticConstants.AUDIT_SERVERS;
            }

            StringBuilder sb = new StringBuilder("Servers available for " + deployment + " deployment:");
            for (int i = 0; i < maxOpts; i++) {
                sb.append("\n    (" + (i + 1) + ") ");
                sb.append(options[i]);
            }

            sb.append("\nEnter desired server number: ");
            System.out.println(sb.toString());
            int userOpt = userInput.nextInt();
            if (userOpt < maxOpts && userOpt > 0) {
                txDeployInst = userOpt - 1;
            }
        }
    }

    private static void getTxUserPref() {
        Scanner userInput = new Scanner(System.in);
        boolean hasDeploymentType = false;
        boolean hasServer = false;

        while (!hasDeploymentType) {
            System.out.println("Deployment options:" +
                    "\n    '" + TX_TYPE + "' for Transaction Server deployment" +
                    "\n    '" + TEST_TYPE + "' for Test Server deployment" +
                    "\nEnter deployment type: "
            );

            String input = userInput.nextLine();
            if (input.equals(TX_TYPE)) {
                deploytype = input;
                hasDeploymentType = true;
            } else if (input.equals(TEST_TYPE)) {
                System.out.println("Test deployment support coming soon.");
            } else {
                System.out.println("Not a valid deployment option.");
            }
        }

        // get target transaction server from user
        int maxOpt = StaticConstants.TX_SERVERS.length;
        while (!hasServer) {
            StringBuilder txServersString = new StringBuilder("Servers available for transaction deployment:");
            for (int i = 0; i < maxOpt; i++) {
                txServersString.append("\n    (" + (i + 1) + ") ");
                txServersString.append(StaticConstants.TX_SERVERS[i]);
            }
            txServersString.append("\nEnter desired transaction server number: ");
            System.out.println(txServersString.toString());
            int userOpt = userInput.nextInt();
            if (userOpt < maxOpt && userOpt > 0) {
                txDeployInst = userOpt - 1;
                hasServer = true;
            }
        }
    }

    private static void getWebUserPrefs()
    {
        Scanner userInput = new Scanner(System.in);
        int maxOpt = StaticConstants.WEB_SERVERS.length;
        boolean hasServer = false;
        while (!hasServer) {
            StringBuilder txServersString = new StringBuilder("Servers available for web server deployment:");
            for (int i = 0; i < maxOpt; i++) {
                txServersString.append("\n    (" + (i + 1) + ") ");
                txServersString.append(StaticConstants.WEB_SERVERS[i]);
            }
            txServersString.append("\nEnter desired web server number: ");
            System.out.println(txServersString.toString());
            int userOpt = userInput.nextInt();
            if (userOpt < maxOpt && userOpt > 0) {
                webDeployInst = userOpt - 1;
                hasServer = true;
            }
        }
    }

    private static void getUserInfo() {
        Scanner userInput = new Scanner(System.in);
        System.out.println("Enter username: ");
        username = userInput.nextLine();
        System.out.println("Enter password: ");
        password = userInput.nextLine();
    }

    private static boolean connectToTxServer(boolean connected, SSHClient client) {
        System.out.println("Connecting to transaction server " + StaticConstants.TX_SERVERS[txDeployInst]);
        try {
            client.addHostKeyVerifier(new PromiscuousVerifier());
            client.connect(StaticConstants.TX_SERVERS[txDeployInst]);
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connected;
    }

    private static void deployTxServer(SSHClient client) {
        System.out.println("Deploying to transaction server " + StaticConstants.TX_SERVERS[txDeployInst]);
        try {
            System.out.println("Transferring files...");
            Path txPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("transaction-server").resolve("src");
            client.newSCPFileTransfer().upload(txPath.toString(), "/seng/scratch/group4/");

            Path bashPath = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("main").resolve("resources").resolve("txrun.sh");
            client.newSCPFileTransfer().upload(bashPath.toString(), "/seng/scratch/group4/");
            System.out.println("Finished transferring");

            System.out.println("Compiling transaction server");
            final Session javac_session = client.startSession();
            final Session.Command javac_cmd = javac_session.exec("javac " +
                    "/seng/scratch/group4/src/com/teamged/txserver/*.java " +
                    "/seng/scratch/group4/src/com/teamged/*.java " + "" +
                    "/seng/scratch/group4/src/com/teamged/txserver/transactions/*.java " +
                    "/seng/scratch/group4/src/com/teamged/txserver/database/*.java " +
                    "/seng/scratch/group4/src/com/teamged/logging/*.java " +
                    "/seng/scratch/group4/src/com/teamged/logging/xmlelements/generated/*.java"
            );
            String result = IOUtils.readFully(javac_cmd.getInputStream()).toString();
            if (!result.equals("")) {
                System.out.println(result);
            }
            javac_cmd.join(30, TimeUnit.SECONDS);
            javac_session.close();
            System.out.println("Finished compiling");

            // Assigns execute permissions to bash script and replaces any Windows line endings
            System.out.println("Preparing bash script for easy running of transaction server...");
            final Session chmod_session = client.startSession();
            final Session.Command chmod_cmd = chmod_session.exec("chmod 770 /seng/scratch/group4/txrun.sh; " +
                    "sed -i -e 's/\\r$//' /seng/scratch/group4/txrun.sh"
            );
            chmod_cmd.join(5, TimeUnit.SECONDS);
            chmod_session.close();
            System.out.println("Bash script prepared");

            System.out.println("Deployment successful!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean connectToWebServer(boolean connected, SSHClient client) {
        System.out.println("Connecting to web server " + StaticConstants.WEB_SERVERS[webDeployInst]);
        try {
            client.addHostKeyVerifier(new PromiscuousVerifier());
            client.connect(StaticConstants.WEB_SERVERS[webDeployInst]);
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connected;
    }

    private static void deployWebServer(SSHClient webClient)
    {
        System.out.println("Deploying to web server " + StaticConstants.TX_SERVERS[webDeployInst]);
        System.out.println("Transferring WAR file...");
        Path warPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("web-server").resolve("target").resolve("daytrading.war");
        try {
            webClient.newSCPFileTransfer().upload(warPath.toString(), "/seng/seng462/group4/local/apache-tomcat-9.0.0.M3/webapps");
            Path bashPath = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("main").resolve("resources").resolve("webrun.sh");
            webClient.newSCPFileTransfer().upload(bashPath.toString(), "/seng/scratch/group4/");
            System.out.println("Finished transferring");

            // Assigns execute permissions to bash script and replaces any Windows line endings
            System.out.println("Preparing bash script for easy running of web server...");
            final Session chmod_session = webClient.startSession();
            final Session.Command chmod_cmd = chmod_session.exec("chmod 770 /seng/scratch/group4/webrun.sh; " +
                    "sed -i -e 's/\\r$//' /seng/scratch/group4/webrun.sh"
            );
            chmod_cmd.join(5, TimeUnit.SECONDS);
            chmod_session.close();
            System.out.println("Bash script prepared");
            System.out.println("Deployment successful!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                webClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean connectToAuditServer(SSHClient sshClient) {
        return false; //TODO
    }
}
