package teamgid.deploy462.deployments;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import teamgid.deploy462.DeploymentConfig;
import teamgid.deploy462.base.SingleDeployment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class WorkloadGeneratorDeployment extends SingleDeployment {

    @Override
    protected void deployHandler(SSHClient client, DeploymentConfig deploymentConfig) {

        try {

            // Remove workload generator jar
            this.removeFile(client, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "WorkloadGeneratorDeploy"));

            // Remove workload files
            this.removeFile(client, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "workload-files"));

            System.out.println("Transferring files...");

            // Copy over pre-compiled JAR file
            Path wgPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("workload-generator").resolve("out").resolve("artifacts").resolve("workload_generator_jar");
            client.newSCPFileTransfer().upload(wgPath.toString(), "/seng/scratch/group4/WorkloadGeneratorDeploy/");

            // Copy over workload generator files
            Path workloadFilesPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("workload-generator").resolve("workload-files");
            client.newSCPFileTransfer().upload(workloadFilesPath.toString(), "/seng/scratch/group4/workload-files/");

            System.out.println("Finished transferring");

            // Set permissions
            this.setPermissions(client, 770, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "WorkloadGeneratorDeploy"));
            this.setPermissions(client, 770, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "workload-files"));

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
