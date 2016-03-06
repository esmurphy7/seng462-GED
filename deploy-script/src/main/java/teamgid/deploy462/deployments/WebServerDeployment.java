package teamgid.deploy462.deployments;

import net.schmizz.sshj.SSHClient;
import teamgid.deploy462.DeploymentConfig;
import teamgid.deploy462.base.BaseDeployment;
import teamgid.deploy462.base.MultipleDeployment;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebServerDeployment extends MultipleDeployment {

    @Override
    protected void deployHandler(SSHClient client, DeploymentConfig deploymentConfig)
    {
        try {

            System.out.println("Transferring WAR file...");

            Path localWarPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("web-server").resolve("target").resolve("ROOT.war");
            String remoteDirectory = String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "ROOT.war");

            client.newSCPFileTransfer().upload(localWarPath.toString(), remoteDirectory);

            System.out.println("Finished transferring");

            this.setPermissions(client, 660, remoteDirectory);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
