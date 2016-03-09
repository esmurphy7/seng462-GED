package teamgid.deploy462.deployments;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import teamgid.deploy462.DeploymentConfig;
import teamgid.deploy462.base.MultipleDeployment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class QuoteFetchServerDeployment extends MultipleDeployment {

    @Override
    public void deployHandler(SSHClient client, DeploymentConfig deploymentConfig) {

        try {

            removeFile(client, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "QuoteFetchDeploy"));

            System.out.println("Transferring files...");
            Path txPath = Paths.get(System.getProperty("user.dir")).getParent()
                    .resolve("quote-fetch-server")
                    .resolve("src")
                    .resolve("main")
                    .resolve("java");
            client.newSCPFileTransfer().upload(txPath.toString(), "/seng/scratch/group4/QuoteFetchDeploy/");
            System.out.println("Finished transferring");

            System.out.println("Compiling quote fetch server");
            final Session javac_session = client.startSession();
            final Session.Command javac_cmd = javac_session.exec("javac -cp .:" + deploymentConfig.getRemoteDirectory() + "/gson-2.6.2.jar " +
                    "/seng/scratch/group4/QuoteFetchDeploy/com/teamged/fetchserver/*.java " +
                    "/seng/scratch/group4/QuoteFetchDeploy/com/teamged/fetchserver/serverthreads/*.java " +
                    "/seng/scratch/group4/QuoteFetchDeploy/com/teamged/deployment/*.java " +
                    "/seng/scratch/group4/QuoteFetchDeploy/com/teamged/deployment/base/*.java " +
                    "/seng/scratch/group4/QuoteFetchDeploy/com/teamged/deployment/deployments/*.java " +
                    "/seng/scratch/group4/QuoteFetchDeploy/com/teamged/deployment/internals/*.java " +
                    "/seng/scratch/group4/QuoteFetchDeploy/com/teamged/logging/*.java " +
                    "/seng/scratch/group4/QuoteFetchDeploy/com/teamged/logging/xmlelements/generated/*.java"
            );
            String result = IOUtils.readFully(javac_cmd.getInputStream()).toString();
            if (!result.equals("")) {
                System.out.println(result);
            }
            javac_cmd.join(60, TimeUnit.SECONDS);
            javac_session.close();
            System.out.println("Finished compiling");

            setPermissions(client, 770, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "QuoteFetchDeploy"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
