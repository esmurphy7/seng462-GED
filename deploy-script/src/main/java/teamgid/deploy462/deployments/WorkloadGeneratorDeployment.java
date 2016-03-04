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

            System.out.println("Cleaning old files...");

            // Remove workload generator jar
            Session rm_session = client.startSession();
            Session.Command rm_cmd = rm_session.exec("rm -r /seng/scratch/group4/WorkloadGeneratorDeploy/");
            rm_cmd.join(10, TimeUnit.SECONDS);
            rm_session.close();

            // Remove workload files
            Session rm_workload_files_session = client.startSession();
            Session.Command rm_workload_files_cmd = rm_workload_files_session.exec("rm -r /seng/scratch/group4/workload-files/");
            rm_workload_files_cmd.join(10, TimeUnit.SECONDS);
            rm_workload_files_session.close();

            System.out.println("Finished cleaning old files");

            System.out.println("Transferring files...");

            // Copy over pre-compiled JAR file
            Path wgPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("workload-generator").resolve("out").resolve("artifacts").resolve("workload_generator_jar");
            client.newSCPFileTransfer().upload(wgPath.toString(), "/seng/scratch/group4/WorkloadGeneratorDeploy/");

            // Copy over workload generator files
            Path workloadFilesPath = Paths.get(System.getProperty("user.dir")).getParent().resolve("workload-generator").resolve("workload-files");
            client.newSCPFileTransfer().upload(workloadFilesPath.toString(), "/seng/scratch/group4/workload-files/");

            System.out.println("Finished transferring");

            System.out.println("Setting file and directory permissions...");

            this.setPermissions(client, 770, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "WorkloadGeneratorDeploy"));
            this.setPermissions(client, 770, String.format("%s/%s", deploymentConfig.getRemoteDirectory(), "workload-files"));

            System.out.println("File and directory permissions applied");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}