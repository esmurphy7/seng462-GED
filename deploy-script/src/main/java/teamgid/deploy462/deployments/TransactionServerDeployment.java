package teamgid.deploy462.deployments;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import teamgid.deploy462.base.MultipleDeployment;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class TransactionServerDeployment extends MultipleDeployment {

    @Override
    public void deployHandler(SSHClient client) {

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

            System.out.println("Setting file and directory permissions...");
            Session chmod_session = client.startSession();
            Session.Command chmod_cmd = chmod_session.exec("chmod -R 770 /seng/scratch/group4/TransactionDeploy");
            chmod_cmd.join(5, TimeUnit.SECONDS);
            chmod_session.close();
            System.out.println("File and directory permissions applied");

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
