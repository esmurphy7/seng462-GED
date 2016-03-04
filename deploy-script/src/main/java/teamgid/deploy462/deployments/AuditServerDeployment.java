package teamgid.deploy462.deployments;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import teamgid.deploy462.DeploymentConfig;
import teamgid.deploy462.base.SingleDeployment;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class AuditServerDeployment extends SingleDeployment {

    @Override
    protected void deployHandler(SSHClient client, DeploymentConfig deploymentConfig) {

        try {

            this.removeFile(client, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "AuditDeploy"));

            System.out.println("Transferring files...");

            Path auditPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("audit-server").resolve("src");
            client.newSCPFileTransfer().upload(auditPath.toString(), "/seng/scratch/group4/AuditDeploy/");

            System.out.println("Finished transferring");

            System.out.println("Compiling audit server");

            final Session javac_session = client.startSession();
            final Session.Command javac_cmd = javac_session.exec("javac " +
                    "/seng/scratch/group4/AuditDeploy/com/teamged/auditserver/*.java " +
                    "/seng/scratch/group4/AuditDeploy/com/teamged/*.java " + "" +
                    "/seng/scratch/group4/AuditDeploy/com/teamged/auditserver/threads/*.java " +
                    "/seng/scratch/group4/AuditDeploy/com/teamged/logging/*.java "
            );

            String result = IOUtils.readFully(javac_cmd.getInputStream()).toString();

            if (!result.equals("")) {
                System.out.println(result);
            }

            javac_cmd.join(60, TimeUnit.SECONDS);
            javac_session.close();

            System.out.println("Finished compiling");

            this.setPermissions(client, 770, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "AuditDeploy"));

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
