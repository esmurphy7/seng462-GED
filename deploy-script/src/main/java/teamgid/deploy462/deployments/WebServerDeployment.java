package teamgid.deploy462.deployments;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import teamgid.deploy462.base.MultipleDeployment;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class WebServerDeployment extends MultipleDeployment {

    @Override
    protected void deployHandler(SSHClient client) {

        System.out.println("Transferring WAR file...");
        Path warPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("web-server").resolve("target").resolve("daytrading.war");

        try {

            client.newSCPFileTransfer().upload(warPath.toString(), "/seng/seng462/group4/local/apache-tomcat-9.0.0.M3/webapps");
            Path bashPath = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("main").resolve("resources").resolve("run-web-server.sh");
            client.newSCPFileTransfer().upload(bashPath.toString(), "/seng/scratch/group4/");
            System.out.println("Finished transferring");

            System.out.println("Preparing bash script for easy running of web server...");
            final Session chmod_session = client.startSession();
            final Session.Command chmod_cmd = chmod_session.exec("chmod 770 /seng/scratch/group4/run-web-server.sh; " +
                    "sed -i -e 's/\\r$//' /seng/scratch/group4/run-web-server.sh"
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
}
