package teamgid.deploy462.deployments;

import net.schmizz.sshj.SSHClient;
import teamgid.deploy462.base.SingleDeployment;

public class WebLoadBalancerDeployment extends SingleDeployment {

    @Override
    protected void deployHandler(SSHClient client) {
        System.out.println("WebLoadBalancerDeployment not implemented yet");
    }
}
