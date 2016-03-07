package com.teamged.auditserver.threads;

import com.teamged.auditserver.AuditMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DanielF on 2016-02-23.
 */
public class LogConnectionHandler implements Runnable {
    //private static Pattern userCommandTidPattern = Pattern.compile(".*<userCommand>.*<transactionNum>(\\d+)<.*", Pattern.DOTALL);
    private static Pattern userCommandTidPattern = Pattern.compile("IDX,(\\d+)");
    private final Socket socket;

    public LogConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Reads all the text available from the client socket and adds it to the log queue. If the log is a
     * user command, the transaction number is stored in preparation for performing a log dump. If a log
     * dump has been requested and the necessary logs are present, then one will be performed.
     */
    @Override
    public void run() {
        String message = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            StringBuilder s = new StringBuilder();
            while ((message = in.readLine()) != null) {
                s.append(message);
            }
            message = s.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (message != null) {
            Matcher m = userCommandTidPattern.matcher(message);
            if (m.matches()) {
                AuditMain.updateSequenceId(Integer.parseInt(m.group(1)));
            } else {
                AuditMain.putLogQueue(message);
            }

            // This is the sequence with the least lock contention - dumpIsQueued is a simple boolean
            // read; dumpIfReady calls dumpIsReady internally, and both are potentially blocking calls
            if (AuditMain.dumpIsQueued()) {
                AuditMain.dumpIfReady();
            }
        }
    }
}
