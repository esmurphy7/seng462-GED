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
    private static final List<Deployment> deployments = new ArrayList<Deployment>();

    private static final String TX_TYPE = "tx";
    private static final String WEB_TYPE = "web";
    private static final String AUDIT_TYPE = "audit";
    private static final String ALL_TYPE = "all";

    private static String username;
    private static String password;

    /**
     * Deployment script for transaction server. Could easily be extended to deploy other servers.
     * Requires user input - first for deployment type (currently only works with 'tx') and deployment server,
     * and then username and password for logging on to the seng lab machine. To add more server options, edit
     * the StaticConstants file in the deploy-script project.
     * <p>
     * Assumes that the transaction-server folder is in a directory level with deploy-script and that the program
     * was launched from the deploy-script folder.
     * <p>
     * Copies the contents of the transaction-server project to the specified server. The contents will be placed in
     * the /seng/scratch/group4 directory, under a new (or replaced) src directory. These files will be compiled (note
     * that if new folders are added to the transaction-server project, those will need to be added to the compile
     * definitions in the 'deployTxServer' method below.
     * <p>
     * A bash script will be copied to the target server, given execute permissions, and its line endings will be
     * replaced with Unix LF instead of Windows CRLF. This way, the transaction server can be run by going to the
     * /seng/scratch/group4 directory and typing:
     * ./txrun.sh
     * <p>
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
     *
     * @param args
     */
    public static void main(String[] args) {
        // check if user wants to deploy transaction and/or web server
        getUserPref();

        // get user's name and password
        getUserInfo();

        for (Deployment deployment : deployments) {
            boolean connected = false;
            final SSHClient client = new SSHClient();
            if (connectToServer(client, deployment.getServerLocation())) {

                switch (deployment.getServerType()) {
                    case WEB_SERVER:
                        try {
                            connected = authorizeUser(client);
                        } catch (IOException e) {
                            // Have to try catch because disconnecting can throw an exception...
                            e.printStackTrace();
                        }
                        if (connected) {
                            deployWebServer(client, deployment.getServerLocation());
                        }
                        break;

                    case TRANSACTION_SERVER:
                        try {
                            connected = authorizeUser(client);
                        } catch (IOException e) {
                            // Have to try catch because disconnecting can throw an exception...
                            e.printStackTrace();
                        }
                        if (connected) {
                            deployTxServer(client, deployment.getServerLocation());
                        }

                        break;

                    case AUDIT_SERVER:
                        try {
                            connected = authorizeUser(client);
                        } catch (IOException e) {
                            // Have to try catch because disconnecting can throw an exception...
                            e.printStackTrace();
                        }
                        if (connected) {
                            deployAuditServer(client, deployment.getServerLocation());
                        }

                        break;
                }
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

    private static void getUserPref() {
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
                deployments.add(new Deployment(ServerType.WEB_SERVER));
                deployments.add(new Deployment(ServerType.TRANSACTION_SERVER));
                deployments.add(new Deployment(ServerType.AUDIT_SERVER));
                hasDeploymentType = true;
            } else if (input.equals(WEB_TYPE)) {
                deployments.add(new Deployment(ServerType.WEB_SERVER));
                hasDeploymentType = true;
            } else if (input.equals(TX_TYPE)) {
                deployments.add(new Deployment(ServerType.TRANSACTION_SERVER));
                hasDeploymentType = true;
            } else if (input.equals(AUDIT_TYPE)) {
                deployments.add(new Deployment(ServerType.AUDIT_SERVER));
                hasDeploymentType = true;
            } else {
                System.out.println("Not a valid server option");
            }
        }

        int maxOpts = 0;
        String[] options = null;
        for (Deployment deployment : deployments) {
            switch (deployment.getServerType()) {
                case WEB_SERVER:
                    maxOpts = StaticConstants.WEB_SERVERS.length;
                    options = StaticConstants.WEB_SERVERS;
                    break;
                case TRANSACTION_SERVER:
                    maxOpts = StaticConstants.TX_SERVERS.length;
                    options = StaticConstants.TX_SERVERS;
                    break;
                case AUDIT_SERVER:
                    maxOpts = StaticConstants.AUDIT_SERVERS.length;
                    options = StaticConstants.AUDIT_SERVERS;
                    break;
            }

            boolean hasServer = false;
            while (!hasServer) {
                StringBuilder sb = new StringBuilder("Servers available for " + deployment.getServerType() + " deployment:");
                for (int i = 0; i < maxOpts; i++) {
                    sb.append("\n    (" + (i + 1) + ") ");
                    sb.append(options[i]);
                }

                sb.append("\nEnter desired server number: ");
                System.out.println(sb.toString());
                int userOpt = userInput.nextInt();
                if (userOpt < maxOpts && userOpt > 0) {
                    deployment.addServerLocation(userOpt - 1);
                    hasServer = true;
                }
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

    private static boolean connectToServer(SSHClient client, String server) {
        boolean connected = false;
        System.out.println("Connecting to server " + server);
        try {
            client.addHostKeyVerifier(new PromiscuousVerifier());
            client.connect(server);
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connected;
    }

    private static void deployTxServer(SSHClient client, String server) {
        System.out.println("\nDEPLOYING TO TRANSACTION SERVER: " + server);
        try {
            System.out.println("Cleaning old files...");
            final Session rm_session = client.startSession();
            final Session.Command rm_cmd = rm_session.exec("rm -r /seng/scratch/group4/TransactionDeploy/");
            rm_cmd.join(10, TimeUnit.SECONDS);
            rm_session.close();
            System.out.println("Finished cleaning old files");

            System.out.println("Transferring files...");
            Path txPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("transaction-server").resolve("src");
            client.newSCPFileTransfer().upload(txPath.toString(), "/seng/scratch/group4/TransactionDeploy/");

            Path bashPath = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("main").resolve("resources").resolve("txrun.sh");
            client.newSCPFileTransfer().upload(bashPath.toString(), "/seng/scratch/group4/");
            System.out.println("Finished transferring");

            System.out.println("Compiling transaction server");
            final Session javac_session = client.startSession();
            final Session.Command javac_cmd = javac_session.exec("javac " +
                    "/seng/scratch/group4/TransactionDeploy/com/teamged/txserver/*.java " +
                    "/seng/scratch/group4/TransactionDeploy/com/teamged/*.java " + "" +
                    "/seng/scratch/group4/TransactionDeploy/com/teamged/txserver/transactions/*.java " +
                    "/seng/scratch/group4/TransactionDeploy/com/teamged/txserver/database/*.java " +
                    "/seng/scratch/group4/TransactionDeploy/com/teamged/logging/*.java " +
                    "/seng/scratch/group4/TransactionDeploy/com/teamged/logging/xmlelements/generated/*.java"
            );
            String result = IOUtils.readFully(javac_cmd.getInputStream()).toString();
            if (!result.equals("")) {
                System.out.println(result);
            }
            javac_cmd.join(60, TimeUnit.SECONDS);
            javac_session.close();
            System.out.println("Finished compiling");

            System.out.println("Preparing bash script for easy running of transaction server...");
            System.out.println("Setting file and directory permissions...");
            final Session chmod_session = client.startSession();
            final Session.Command chmod_cmd = chmod_session.exec("chmod 770 /seng/scratch/group4/txrun.sh; " +
                    "chmod -R 770 /seng/scratch/group4/TransactionDeploy; " +
                    "sed -i -e 's/\\r$//' /seng/scratch/group4/txrun.sh"
            );
            chmod_cmd.join(5, TimeUnit.SECONDS);
            chmod_session.close();
            System.out.println("Bash script prepared");
            System.out.println("File and directory permissions applied");

            System.out.println("Transaction server deployment successful!");
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

    private static void deployWebServer(SSHClient client, String server) {
        System.out.println("\nDEPLOYING TO WEB SERVER: " + server);
        System.out.println("Transferring WAR file...");
        Path warPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("web-server").resolve("target").resolve("daytrading.war");
        try {
            client.newSCPFileTransfer().upload(warPath.toString(), "/seng/seng462/group4/local/apache-tomcat-9.0.0.M3/webapps");
            Path bashPath = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("main").resolve("resources").resolve("webrun.sh");
            client.newSCPFileTransfer().upload(bashPath.toString(), "/seng/scratch/group4/");
            System.out.println("Finished transferring");

            System.out.println("Preparing bash script for easy running of web server...");
            final Session chmod_session = client.startSession();
            final Session.Command chmod_cmd = chmod_session.exec("chmod 770 /seng/scratch/group4/webrun.sh; " +
                    "sed -i -e 's/\\r$//' /seng/scratch/group4/webrun.sh"
            );
            chmod_cmd.join(5, TimeUnit.SECONDS);
            chmod_session.close();
            System.out.println("Bash script prepared");
            System.out.println("Web server deployment successful!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void deployAuditServer(SSHClient client, String server) {
        System.out.println("\nDEPLOYING TO AUDIT SERVER: " + server);
        try {
            System.out.println("Cleaning old files...");
            final Session rm_session = client.startSession();
            final Session.Command rm_cmd = rm_session.exec("rm -r /seng/scratch/group4/AuditDeploy/");
            rm_cmd.join(10, TimeUnit.SECONDS);
            rm_session.close();
            System.out.println("Finished cleaning old files");

            System.out.println("Transferring files...");
            Path txPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("audit-server").resolve("src");
            client.newSCPFileTransfer().upload(txPath.toString(), "/seng/scratch/group4/AuditDeploy/");

            Path bashPath = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("main").resolve("resources").resolve("audrun.sh");
            client.newSCPFileTransfer().upload(bashPath.toString(), "/seng/scratch/group4/");
            System.out.println("Finished transferring");

            System.out.println("Compiling audit server");
            final Session javac_session = client.startSession();
            final Session.Command javac_cmd = javac_session.exec("javac " +
                    "/seng/scratch/group4/AuditDeploy/com/teamged/auditserver/*.java " +
                    "/seng/scratch/group4/AuditDeploy/com/teamged/*.java " + "" +
                    "/seng/scratch/group4/AuditDeploy/com/teamged/auditserver/threads/*.java " +
                    "/seng/scratch/group4/AuditDeploy/com/teamged/logging/*.java " +
                    "/seng/scratch/group4/AuditDeploy/com/teamged/logging/xmlelements/generated/*.java"
            );
            String result = IOUtils.readFully(javac_cmd.getInputStream()).toString();
            if (!result.equals("")) {
                System.out.println(result);
            }
            javac_cmd.join(60, TimeUnit.SECONDS);
            javac_session.close();
            System.out.println("Finished compiling");

            System.out.println("Preparing bash script for easy running of transaction server...");
            System.out.println("Setting file and directory permissions...");
            final Session chmod_session = client.startSession();
            final Session.Command chmod_cmd = chmod_session.exec("chmod 770 /seng/scratch/group4/audrun.sh; " +
                    "chmod -R 770 /seng/scratch/group4/AuditDeploy; " +
                    "sed -i -e 's/\\r$//' /seng/scratch/group4/audrun.sh"
            );
            chmod_cmd.join(5, TimeUnit.SECONDS);
            chmod_session.close();
            System.out.println("Bash script prepared");
            System.out.println("File and directory permissions applied");
            System.out.println("Audit server deployment successful!");
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
}
