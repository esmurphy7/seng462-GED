package teamgid.deploy462;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by DanielF on 2016-02-15.
 */
public class DeployLaunch {
    private static String username;
    private static String password;

    public static void main(String[] args) {
        boolean connected = false;
        getUserInfo();

        final SSHClient client = new SSHClient();

        connected = connectToHost(connected, client);

        if (connected) {
            connected = authorizeUser(client);
        }

        if (connected) {
            try {
                client.newSCPFileTransfer().upload(new FileSystemFile(System.getProperty("user.home") + "\\IdeaProjects\\seng462-GED\\transaction-server\\src"), "/seng/scratch/group4/");
                System.out.println("Success!");
                System.out.println(System.getProperty("user.home"));
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

    private static boolean authorizeUser(SSHClient client) {
        boolean connected;
        connected = false;
        try {
            client.authPassword(username, password);
            connected = true;
        } catch (UserAuthException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
        }
        return connected;
    }

    private static boolean connectToHost(boolean connected, SSHClient client) {
        try {
            client.addHostKeyVerifier(new PromiscuousVerifier());
            client.connect("b130.seng.uvic.ca");
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connected;
    }

    private static void getUserInfo() {
        Scanner userInput = new Scanner(System.in);
        System.out.print("Enter username: ");
        username = userInput.nextLine();
        System.out.print("Enter password: ");
        password = userInput.nextLine();
    }
}
