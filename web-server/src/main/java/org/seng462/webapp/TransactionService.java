package org.seng462.webapp;

import com.teamged.deployment.deployments.TransactionServerDeployment;
import com.teamged.deployment.deployments.WebServerDeployment;
import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.CommandType;
import com.teamged.logging.xmlelements.UserCommandType;
import jersey.repackaged.com.google.common.base.Joiner;

import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Evan on 1/18/2016.
 */
public class TransactionService
{
    // format the message to send to the transaction server
    // Format: [workload seq. no], [user seq. no], cmdCode, commandArgs, web server address, web server port
    private static String formatMessage(UserCommand userCommand) throws UnknownHostException {
        // parse command object into message
        ArrayList<String> message = new ArrayList<>();

        // include sequence numbers
        String workloadSeqNo = "["+userCommand.getWorkloadSeqNo()+"]";
        String userSeqNo = "["+userCommand.getUserSeqNo()+"]";
        message.add(workloadSeqNo);
        message.add(userSeqNo);

        // include command code
        message.add(Integer.toString(userCommand.getCmdCode().ordinal()));

        // include command arguments in the correct order: userId, stockSymbol, amount, filename
        Map<String, String> args = userCommand.getArgs();
        String[] argKeys = {
                "userId",
                "stockSymbol",
                "amount",
                "filename"
        };
        for(String argKey : argKeys)
        {
            if(args.get(argKey) != null)
            {
                message.add(args.get(argKey));
            }
        }

        // include timestamp
        message.add(Long.toString(System.currentTimeMillis()));

        // include currently running web server hostname and port
        InetAddress ip = InetAddress.getLocalHost();
        String hostname = ip.getHostName();
        WebServerDeployment webDeployment = ConfigurationManager.DeploymentSettings.getWebServers();

        int hostnameIndex = webDeployment.getServers().indexOf(hostname);
        String hostnameI = (hostnameIndex < 0) ? "0" : Integer.toString(hostnameIndex);
        String port = Integer.toString(webDeployment.getPort());

        message.add(hostnameI);
        message.add(port);

        // format the message with separator
        String separator = ",";
        String messageFmt = Joiner.on(separator).join(message);
        return messageFmt;
    }

    // Send a command to the transaction server in the form of a formatted packet
    public static Response sendCommand(UserCommand userCommand)
    {
        // log a user command to the audit server

        TransactionService.LogUserCommand(userCommand);

        // format the message
        String message = "";
        try {
            message = TransactionService.formatMessage(userCommand);
        } catch (UnknownHostException e) {
            String errorMsg = "Cannot find local hostname to include in message" + "\n" + e.getMessage();
            e.printStackTrace();
            Response response = Response.serverError().entity(errorMsg).build();
            return response;
        }

        // determine the target transaction server (equally distribute the workload to each server)
        TransactionServerDeployment txDeployment = ConfigurationManager.DeploymentSettings.getTransactionServers();
        List<String> servers = txDeployment.getServers();
        int targetPort = txDeployment.getPort();
        int userNameSort = (userCommand.getArgs().get("userId") != null) ? userCommand.getArgs().get("userId").charAt(0) : 1;
        int serverIndex = userNameSort % servers.size();
        String targetServer = servers.get(serverIndex);

        // open transaction socket
        try (Socket socket = new Socket(targetServer, targetPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true))
        {
            // send packet over socket
            out.println(message);

            //TODO:// translate response from tx server into jersey response and return it
            Response response = Response.ok().build();
            return response;

        }catch (Exception e) {
            String errorMsg = "Could not connect to transaction server: "+  targetServer + ":" + targetPort + "\n" + e.getMessage();
            System.out.println(errorMsg);
            Response response = Response.serverError().entity(errorMsg).build();
            return response;
        }
    }

    // Build a user command log and send it to the transaction server
    private static void LogUserCommand(UserCommand userCommand)
    {
        // map JAXB command types to custom command code enum
        CommandType commandType = null;
        switch (userCommand.getCmdCode()) {
            case ADD:
                commandType = CommandType.ADD;
                break;
            case QUOTE:
                commandType = CommandType.QUOTE;
                break;
            case BUY:
                commandType = CommandType.BUY;
                break;
            case COMMIT_BUY:
                commandType = CommandType.COMMIT_BUY;
                break;
            case CANCEL_BUY:
                commandType = CommandType.CANCEL_BUY;
                break;
            case SELL:
                commandType = CommandType.SELL;
                break;
            case COMMIT_SELL:
                commandType = CommandType.COMMIT_SELL;
                break;
            case CANCEL_SELL:
                commandType = CommandType.CANCEL_SELL;
                break;
            case SET_BUY_AMOUNT:
                commandType = CommandType.SET_BUY_AMOUNT;
                break;
            case CANCEL_SET_BUY:
                commandType = CommandType.CANCEL_SET_BUY;
                break;
            case SET_BUY_TRIGGER:
                commandType = CommandType.SET_BUY_TRIGGER;
                break;
            case SET_SELL_AMOUNT:
                commandType = CommandType.SET_SELL_AMOUNT;
                break;
            case SET_SELL_TRIGGER:
                commandType = CommandType.SET_SELL_TRIGGER;
                break;
            case CANCEL_SET_SELL:
                commandType = CommandType.CANCEL_SET_SELL;
                break;
            case DUMPLOG:
                commandType = CommandType.DUMPLOG;
                break;
            case DUMPLOG_ROOT:
                commandType = CommandType.DUMPLOG;
                break;
            case DISPLAY_SUMMARY:
                commandType = CommandType.DISPLAY_SUMMARY;
                break;
        }

        System.out.println("Logging transaction "+userCommand.getWorkloadSeqNo()+": "+userCommand.getCmdCode());

        UserCommandType userCommandLog = new UserCommandType();
        userCommandLog.setCommand(commandType);
        userCommandLog.setUsername(userCommand.getArgs().get("userId"));
        userCommandLog.setStockSymbol(userCommand.getArgs().get("stockSymbol"));

        String amount = userCommand.getArgs().get("amount");
        BigDecimal amountVal = (amount != null) ? new BigDecimal(amount) : null;
        userCommandLog.setFunds(amountVal);

        userCommandLog.setTimestamp(System.currentTimeMillis());
        userCommandLog.setFilename(userCommand.getArgs().get("filename"));
        userCommandLog.setTransactionNum(new BigInteger(userCommand.getWorkloadSeqNo()));

        // include the current server name in the log (default to server at index 0)
        WebServerDeployment webDeploy = ConfigurationManager.DeploymentSettings.getWebServers();
        String hostname = webDeploy.getServers().get(0);
        try {
            InetAddress ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        userCommandLog.setServer(hostname);

        Logger.getInstance().Log(userCommandLog);
    }
}
