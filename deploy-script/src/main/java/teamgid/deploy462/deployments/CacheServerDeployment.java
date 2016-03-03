package teamgid.deploy462.deployments;

import net.schmizz.sshj.SSHClient;
import teamgid.deploy462.DeploymentConfig;
import teamgid.deploy462.base.SingleDeployment;

public class CacheServerDeployment extends SingleDeployment {

    @Override
    protected void deployHandler(SSHClient client, DeploymentConfig deploymentConfig) { }
}
