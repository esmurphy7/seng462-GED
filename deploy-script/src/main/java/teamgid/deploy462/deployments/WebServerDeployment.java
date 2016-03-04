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

            // tomcat requires deletion of existing WAR (and exploded WAR)
            System.out.println("Deleting existing remote WAR file...");
            String remoteWarPath = "/seng/seng462/group4/local/apache-tomcat-9.0.0.M3/webapps/ROOT.war";
            String remoteExplodedWarPath = "/seng/seng462/group4/local/apache-tomcat-9.0.0.M3/webapps/ROOT";
            BaseDeployment.removeFile(client, remoteWarPath);
            BaseDeployment.removeFile(client, remoteExplodedWarPath);

            System.out.println("Transferring WAR file...");
            Path localWarPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("web-server").resolve("target").resolve("ROOT.war");
            client.newSCPFileTransfer().upload(localWarPath.toString(), "/seng/seng462/group4/local/apache-tomcat-9.0.0.M3/webapps");
            System.out.println("Finished transferring");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
