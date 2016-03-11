package com.teamged.auditserver.threads;

import com.teamged.auditlogging.LogManager;
import com.teamged.auditlogging.generated.*;
import com.teamged.auditserver.AuditMain;
import com.teamged.auditserver.InternalLog;
import com.teamged.logging.xmlelements.XmlElements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Calendar;

/**
 * Created by DanielF on 2016-02-23.
 */
public class LogConnectionHandler implements Runnable {
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
        Calendar c = Calendar.getInstance();
        long startTime = c.getTimeInMillis();
        String message = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            message = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long tick1 = c.getTimeInMillis();
        long tick2 = 0;
        long tick3 = 0;
        long tick4 = 0;

        if (message != null) {
            // This splits the message into an array, leaving all trailing empty string args intact
            String[] args = message.split(",", -1);
            if (args.length > 0) {
                try {
                    int i = 0;
                    XmlElements elem = XmlElements.values()[Integer.parseInt(args[i++])];

                    switch (elem) {
                        case AccountTransactionType:
                            AccountTransactionType atType = new AccountTransactionType();
                            atType.setTimestamp(Long.parseLong(args[i++]));
                            atType.setServer(args[i++]);
                            atType.setTransactionNum(new BigInteger(args[i++]));
                            atType.setAction(args[i++]);
                            atType.setUsername(args[i++]);
                            atType.setFunds(new BigDecimal(args[i++]));
                            tick2 = c.getTimeInMillis();
                            LogManager.AddLog(atType);
                            tick3 = c.getTimeInMillis();
                            break;
                        case DebugType:
                            DebugType dbType = new DebugType();
                            dbType.setTimestamp(Long.parseLong(args[i++]));
                            dbType.setServer(args[i++]);
                            dbType.setTransactionNum(new BigInteger(args[i++]));
                            dbType.setCommand(CommandType.fromValue(args[i++]));
                            if (!args[i].isEmpty()) {
                                dbType.setUsername(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                dbType.setFilename(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                dbType.setFunds(new BigDecimal(args[i++]));
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                dbType.setDebugMessage(args[i++]);
                            } else {
                                i++;
                            }
                            tick2 = c.getTimeInMillis();
                            LogManager.AddLog(dbType);
                            tick3 = c.getTimeInMillis();
                            break;
                        case ErrorEventType:
                            ErrorEventType eeType = new ErrorEventType();
                            eeType.setTimestamp(Long.parseLong(args[i++]));
                            eeType.setServer(args[i++]);
                            eeType.setTransactionNum(new BigInteger(args[i++]));
                            eeType.setCommand(CommandType.fromValue(args[i++]));
                            if (!args[i].isEmpty()) {
                                eeType.setUsername(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                eeType.setStockSymbol(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                eeType.setFilename(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                eeType.setFunds(new BigDecimal(args[i++]));
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                eeType.setErrorMessage(args[i++]);
                            } else {
                                i++;
                            }
                            tick2 = c.getTimeInMillis();
                            LogManager.AddLog(eeType);
                            tick3 = c.getTimeInMillis();
                            break;
                        case QuoteServerType:
                            QuoteServerType qsType = new QuoteServerType();
                            qsType.setTimestamp(Long.parseLong(args[i++]));
                            qsType.setServer(args[i++]);
                            qsType.setTransactionNum(new BigInteger(args[i++]));
                            qsType.setPrice(new BigDecimal(args[i++]));
                            qsType.setStockSymbol(args[i++]);
                            qsType.setUsername(args[i++]);
                            qsType.setQuoteServerTime(new BigInteger(args[i++]));
                            qsType.setCryptokey(args[i++]);
                            tick2 = c.getTimeInMillis();
                            LogManager.AddLog(qsType);
                            tick3 = c.getTimeInMillis();
                            break;
                        case SystemEventType:
                            SystemEventType seType = new SystemEventType();
                            seType.setTimestamp(Long.parseLong(args[i++]));
                            seType.setServer(args[i++]);
                            seType.setTransactionNum(new BigInteger(args[i++]));
                            seType.setCommand(CommandType.fromValue(args[i++]));
                            if (!args[i].isEmpty()) {
                                seType.setUsername(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                seType.setStockSymbol(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                seType.setFilename(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                seType.setFunds(new BigDecimal(args[i++]));
                            } else {
                                i++;
                            }
                            tick2 = c.getTimeInMillis();
                            LogManager.AddLog(seType);
                            tick3 = c.getTimeInMillis();
                            break;
                        case UserCommandType:
                            UserCommandType ucType = new UserCommandType();
                            ucType.setTimestamp(Long.parseLong(args[i++]));
                            ucType.setServer(args[i++]);
                            ucType.setTransactionNum(new BigInteger(args[i++]));
                            ucType.setCommand(CommandType.fromValue(args[i++]));
                            if (!args[i].isEmpty()) {
                                ucType.setUsername(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                ucType.setStockSymbol(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                ucType.setFilename(args[i++]);
                            } else {
                                i++;
                            }
                            if (!args[i].isEmpty()) {
                                ucType.setFunds(new BigDecimal(args[i++]));
                            } else {
                                i++;
                            }
                            tick2 = c.getTimeInMillis();
                            LogManager.AddLog(ucType);
                            tick3 = c.getTimeInMillis();
                            break;
                        case TransactionCompleteType:
                            AuditMain.updateSequenceId(Integer.parseInt(args[i++]));
                            break;
                    }

                    // This is the sequence with the least lock contention - dumpIsQueued is a simple boolean
                    // read; dumpIfReady calls dumpIsReady internally, and both are potentially blocking calls
                    if (AuditMain.dumpIsQueued()) {
                        AuditMain.dumpIfReady();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    InternalLog.Log("Error parsing log: " + message);
                }
            }
            tick4 = c.getTimeInMillis();
        }
        long endTime = c.getTimeInMillis();

        StringBuilder sb = new StringBuilder();
        sb.append(tick1-startTime); // Socket read time
        sb.append(",");
        if (tick2 != 0 && tick3 != 0) {
            sb.append(tick3-tick2); // Queue storage time
        }
        sb.append(",");
        if (tick4 != 0) {
            sb.append((tick4-tick1)-(tick3-tick2)); // Log parse time
        }
        sb.append(",");
        sb.append(endTime-startTime); // Full run time of log handling
        sb.append("\n");
        LogManager.timestamps.add(sb.toString());
    }
}
