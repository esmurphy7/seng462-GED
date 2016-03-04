package teamgid.deploy462.deployments;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import teamgid.deploy462.DeploymentConfig;
import teamgid.deploy462.base.MultipleDeployment;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class TransactionServerDeployment extends MultipleDeployment {

    @Override
    public void deployHandler(SSHClient client, DeploymentConfig deploymentConfig) {

        try {

            this.removeFile(client, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "TransactionDeploy"));

            System.out.println("Transferring files...");
            Path txPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("transaction-server").resolve("src");
            client.newSCPFileTransfer().upload(txPath.toString(), "/seng/scratch/group4/TransactionDeploy/");
            System.out.println("Finished transferring");

            System.out.println("Compiling transaction server");
            final Session javac_session = client.startSession();
            final Session.Command javac_cmd = javac_session.exec("javac " +
                    "/seng/scratch/group4/TransactionDeploy/com/teamged/txserver/*.java " +
                    "/seng/scratch/group4/TransactionDeploy/com/teamged/*.java " +
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

            this.setPermissions(client, 770, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "TransactionDeploy"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
