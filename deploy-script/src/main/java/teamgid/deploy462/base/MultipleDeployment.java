package teamgid.deploy462.base;

import net.schmizz.sshj.SSHClient;
import teamgid.deploy462.DeploymentConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class MultipleDeployment extends BaseDeployment {

    private List<String> servers;

    public MultipleDeployment() {
        this.servers = new ArrayList<String>();
    }

    public List<String> getServers() {
        return servers;
    }

    public void addServer(String server) {
        this.servers.add(server);
    }

    @Override
    public void deploy(String username, String password, DeploymentConfig deploymentConfig) {

        for (String server : servers) {

            try {

                SSHClient client = getSSHClient(server, username, password);

                this.singleDeployment(client, deploymentConfig);

                client.disconnect();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}
