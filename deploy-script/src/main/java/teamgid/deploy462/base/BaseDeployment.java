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

    protected abstract void deployHandler(SSHClient client, DeploymentConfig deploymentConfig);

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
        this.deployHandler(client, deploymentConfig);

        // Copy resources
        List<String> resources = this.getResources();

        if (resources != null) {

            for (String resource : resources) {

                String remotePath = String.format("%s/%s", deploymentConfig.getRemoteDirectory(), resource);
                removeFile(client, remotePath);
                copyResource(client, resource, remotePath);
                setPermissions(client, 660, remotePath);
            }
        }

        // Copy and run deploy scripts
        List<String> deployScripts = this.getDeployScripts();

        if (deployScripts != null) {

            for (String deployScript : deployScripts) {

                String remoteScriptPath = String.format("%s/%s", deploymentConfig.getRemoteDirectory(), deployScript);
                copyScript(client, deployScript, remoteScriptPath);
                runScript(client, remoteScriptPath);
                removeFile(client, remoteScriptPath);
            }
        }

        // Copy over the run script
        String runScript = this.getRunScript();
        if (runScript != null) {

            String remoteScriptPath = String.format("%s/%s", deploymentConfig.getRemoteDirectory(), runScript);
            removeFile(client, remoteScriptPath);
            copyScript(client, runScript, remoteScriptPath);
        }

        // Copy over config.json
        String configFile = "config.json";
        String remotePath = String.format("%s/%s", deploymentConfig.getRemoteDirectory(), configFile);
        this.removeFile(client, remotePath);
        this.copyResource(client, configFile, remotePath);
        this.setPermissions(client, 660, remotePath);
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
        setPermissions(client, 770, destinationPath);
        stripWindowsLineEndings(client, destinationPath);
    }

    protected static void removeFile(SSHClient client, String destinationPath) throws IOException {

        System.out.println(String.format("Removing file %s", destinationPath));

        Session chmod_session = client.startSession();

        Session.Command chmod_cmd = chmod_session.exec(String.format("rm -r %s", destinationPath));

        chmod_cmd.join(5, TimeUnit.SECONDS);
        chmod_session.close();

        System.out.println("File removed");
    }

    protected static void stripWindowsLineEndings(SSHClient client, String destinationPath) throws IOException {

        System.out.println(String.format("Removing windows line endings on file %s", destinationPath));

        Session session = client.startSession();

        Session.Command command = session.exec(String.format("sed -i -e 's/\\r$//' %s", destinationPath));

        command.join(5, TimeUnit.SECONDS);
        session.close();

        System.out.println(String.format("Windows line endings removed", destinationPath));
    }

    protected static void setPermissions(SSHClient client, int permission, String destinationPath) throws IOException {

        System.out.println(String.format("Setting permission %d on %s", permission, destinationPath));

        Session session = client.startSession();

        Session.Command command = session.exec(String.format("chmod -R %d %s; ", permission, destinationPath));

        command.join(5, TimeUnit.SECONDS);
        session.close();

        System.out.println("Permissions have been set");
    }
}
