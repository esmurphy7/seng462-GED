package teamgid.deploy462.base;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import teamgid.deploy462.DeploymentConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class BaseDeployment {

    private List<String> resources;
    private List<String> deployScripts;
    private String runScript;

    public abstract void deploy(String username, String password, DeploymentConfig deploymentConfig);

    protected abstract void deployHandler(SSHClient client);

    public List<String> getResources() {
        return resources;
    }

    public void addResource(String resource) {
        this.resources.add(resource);
    }

    public List<String> getDeployScripts() {
        return deployScripts;
    }

    public void addDeployScript(String deployScript) {
        this.deployScripts.add(deployScript);
    }

    public String getRunScript() {
        return runScript;
    }

    public void setRunScript(String runScript) {
        this.runScript = runScript;
    }

    protected void singleDeployment(SSHClient client, DeploymentConfig deploymentConfig) throws IOException {

        // Run the main deployment
        this.deployHandler(client);

        // Copy resources
        List<String> resources = this.getResources();

        if (resources != null) {

            for (String resource : resources) {

                String remotePath = String.format("%s/%s", deploymentConfig.getRemoteDirectory(), resource);
                this.copyResource(client, resource, remotePath);
            }
        }

        // Copy and run deploy scripts
        List<String> deployScripts = this.getDeployScripts();

        if (deployScripts != null) {

            for (String deployScript : deployScripts) {

                String remoteScriptPath = String.format("%s/%s", deploymentConfig.getRemoteDirectory(), deployScript);
                this.copyScript(client, deployScript, remoteScriptPath);
                this.runScript(client, remoteScriptPath);
                this.removeScript(client, remoteScriptPath);
            }
        }

        // Copy over the run script
        String runScript = this.getRunScript();
        if (runScript != null) {

            String remoteScriptPath = String.format("%s/%s", deploymentConfig.getRemoteDirectory(), runScript);
            this.copyScript(client, runScript, remoteScriptPath);
        }
    }

    protected static SSHClient getSSHClient(String server, String username, String password) throws IOException {

        final SSHClient client = new SSHClient();

        System.out.println("Connecting to server " + server);

        try {

            client.addHostKeyVerifier(new PromiscuousVerifier());
            client.connect(server);
            client.authPassword(username, password);

        } catch (UserAuthException e) {

            e.printStackTrace();
            client.disconnect();

        } catch (TransportException e) {

            e.printStackTrace();
            client.disconnect();
        }

        return client;
    }

    protected static void runScript(SSHClient client, String destinationScript) throws IOException {

        System.out.println(String.format("Executing script %s", destinationScript));

        Session session = client.startSession();

        Session.Command cmd = session.exec(destinationScript);

        cmd.join();
        session.close();

        System.out.println("Script execution complete");
    }

    protected static void copyResource(SSHClient client, String resourceName, String destinationPath) throws IOException {

        System.out.println(String.format("Copying resource %s", resourceName));

        Path resourcePath = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("main").resolve("resources").resolve(resourceName);
        client.newSCPFileTransfer().upload(resourcePath.toString(), destinationPath);

        System.out.println("Resource copied");
    }

    protected static void copyScript(SSHClient client, String scriptName, String destinationPath) throws IOException {

        copyResource(client, scriptName, destinationPath);

        System.out.println(String.format("Setting permissions on script %s", scriptName));

        // Set permissions on script and strip Windows line endings
        Session chmod_session = client.startSession();

        Session.Command chmod_cmd = chmod_session.exec(
                String.format("chmod 770 %s; ", destinationPath) +
                        String.format("sed -i -e 's/\\r$//' %s", destinationPath)
        );

        chmod_cmd.join(5, TimeUnit.SECONDS);
        chmod_session.close();

        System.out.println("Permissions have been set");
    }

    protected static void removeScript(SSHClient client, String destinationPath) throws IOException {

        System.out.println(String.format("Removing script %s", destinationPath));

        Session chmod_session = client.startSession();

        Session.Command chmod_cmd = chmod_session.exec(String.format("rm %s -r", destinationPath));

        chmod_cmd.join(5, TimeUnit.SECONDS);
        chmod_session.close();

        System.out.println("Script removed");
    }
}
