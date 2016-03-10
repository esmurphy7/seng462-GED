package teamgid.deploy462.deployments;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import teamgid.deploy462.DeploymentConfig;
import teamgid.deploy462.base.MultipleDeployment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class TransactionServerDeployment extends MultipleDeployment {

    @Override
    public void deployHandler(SSHClient client, DeploymentConfig deploymentConfig) {

        try {
            String remoteDir = deploymentConfig.getRemoteDirectory();
            String remoteDeploy = String.format("%s/%s", remoteDir, "TransactionDeploy");
            removeFile(client, remoteDeploy);

            System.out.println("Transferring files...");
            Path txPath = Paths.get(System.getProperty("user.dir")).getParent()
                    .resolve("transaction-server")
                    .resolve("src")
                    .resolve("main")
                    .resolve("java");
            client.newSCPFileTransfer().upload(txPath.toString(), remoteDeploy + "/");
            Path commonPath = Paths.get(System.getProperty("user.dir")).getParent()
                    .resolve("common")
                    .resolve("src")
                    .resolve("com");
            client.newSCPFileTransfer().upload(commonPath.toString(), remoteDeploy + "/");
            System.out.println("Finished transferring");

            System.out.println("Compiling transaction server");
            final Session javac_session = client.startSession();
            final Session.Command javac_cmd = javac_session.exec("javac -cp .:" + remoteDir + "/gson-2.6.2.jar " +
                    remoteDeploy + "/com/teamged/txserver/*.java " +
                    remoteDeploy + "/com/teamged/txserver/transactions/*.java " +
                    remoteDeploy + "/com/teamged/txserver/database/*.java " +
                    remoteDeploy + "/com/teamged/deployment/*.java " +
                    remoteDeploy + "/com/teamged/deployment/base/*.java " +
                    remoteDeploy + "/com/teamged/deployment/deployments/*.java " +
                    remoteDeploy + "/com/teamged/deployment/internals/*.java " +
                    remoteDeploy + "/com/teamged/logging/*.java " +
                    remoteDeploy + "/com/teamged/logging/xmlelements/*.java"
            );
            String result = IOUtils.readFully(javac_cmd.getInputStream()).toString();
            if (!result.equals("")) {
                System.out.println(result);
            }
            javac_cmd.join(60, TimeUnit.SECONDS);
            javac_session.close();
            System.out.println("Finished compiling");

            setPermissions(client, 770, remoteDeploy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
