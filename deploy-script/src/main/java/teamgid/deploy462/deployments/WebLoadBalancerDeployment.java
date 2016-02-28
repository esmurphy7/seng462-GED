package teamgid.deploy462.deployments;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import net.schmizz.sshj.SSHClient;
import teamgid.deploy462.DeploymentConfig;
import teamgid.deploy462.base.SingleDeployment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class WebLoadBalancerDeployment extends SingleDeployment {

    @Override
    protected void deployHandler(SSHClient client, DeploymentConfig deploymentConfig) {

        WebServerDeployment webServerDeployment = deploymentConfig.getDeployments().getWebServers();

        if (webServerDeployment == null) {
            System.out.println("Cannot create web load balancer. No web server deployments exist");
            return;
        }

        Map<String, Object> scopes = new HashMap<String, Object>();
        scopes.put("webServers", webServerDeployment.getServers());

        String file = "nginx.conf.loadbalancer";

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(String.format("%s.mustache", file));

        try {

            File tempFile = File.createTempFile(file, "");

            mustache.execute(new FileWriter(tempFile), scopes).flush();

            client.newSCPFileTransfer().upload(tempFile.getAbsolutePath(), String.format("%s/%s", deploymentConfig.getRemoteDirectory(), file));

            if (tempFile.delete()) {
                System.out.println("Temp file deleted");
            } else {
                System.out.println("Temp file not deleted");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
