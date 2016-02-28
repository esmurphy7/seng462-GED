package teamgid.deploy462.base;

import net.schmizz.sshj.SSHClient;
import teamgid.deploy462.DeploymentConfig;

import java.io.IOException;
import java.util.List;

public abstract class SingleDeployment extends BaseDeployment {

    private String server;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public void deploy(String username, String password, DeploymentConfig deploymentConfig) {

        try {

            SSHClient client = getSSHClient(server, username , password);

            this.singleDeployment(client, deploymentConfig);

            client.disconnect();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
