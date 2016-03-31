package com.teamged.txserver.transactions;

import com.teamged.comms.ServerMessage;
import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.CommandType;
import com.teamged.logging.xmlelements.SystemEventType;
import com.teamged.txserver.InternalLog;
import com.teamged.txserver.TxMain;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by DanielF on 2016-01-31.
 * <p>
 * Class for parsing request arguments and providing convenient access to those values.
 */
public class TransactionObject {
    public static final String ROOT_USER = "$ROOT$";

    /**
     * The minimum number of arguments that could show up in a transaction request. At minimum, there must be:
     * - A user command
     * - A parameter for the user command (many commands will contain more than one parameter)
     * - A timestamp
     * - A server name index
     * - A server port index
     */
    private static final int MinimumArgs = 6;

    private static final int StockMaxLength = 3;

    private final ServerMessage serverMessage;
    private UserCommand userCommand = UserCommand.NO_COMMAND;
    private String userName = "";
    private String stockSymbol = "";
    //private BigDecimal amountMoney = BigDecimal.ZERO;
    private int amountDollars = 0;
    private int amountCents = 0;
    private long timeStamp = 0;
    private String fileName = "";
    private int webServerNameIdx = -1;
    private int webServerPortIdx = -1;
    private int workloadSeqNum = -1;
    private int userSeqNum = -1;
    private String errorString = ""; // TODO: Use an enum instead?

    /**
     * Creates a new TransactionObject for managing requests for the transaction server to process. Call getErrorString
     * after instantiating this to see if the arguments could be parsed. Any value other than the empty string indicates
     * an error.
     *
     * @param serverMessage The server request to parse and process.
     */
    public TransactionObject(ServerMessage serverMessage) {
        this.serverMessage = serverMessage;
        if (serverMessage != null) {
            parseArgs(serverMessage.getData());
        } else {
            errorString = "Null server message";
        }
        logRequest();
    }

    private void logRequest() {
        CommandType commandType = null;
        switch (userCommand) {
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

        SystemEventType systemEvent = new SystemEventType();
        systemEvent.setTimestamp(System.currentTimeMillis());
        systemEvent.setServer(TxMain.getServerName());
        systemEvent.setTransactionNum(BigInteger.valueOf(workloadSeqNum));
        if (!userName.equals(ROOT_USER)) {
            systemEvent.setUsername(userName);
        }
        systemEvent.setCommand(commandType);
        systemEvent.setStockSymbol(stockSymbol);
        systemEvent.setFilename(fileName);
        systemEvent.setFunds(new BigDecimal(amountDollars + "." + amountCents));

        Logger.getInstance().Log(systemEvent);
    }

    /**
     * Parses the request string, assigning parameters based on the results. If the parsing fails at any point, a value
     * will be written to the error string property.
     *
     * @param args The request to parse.
     */
    private void parseArgs(String args) {
        if (args == null) {
            errorString = "Null argument";
        } else if (args.isEmpty()) {
            errorString = "Empty argument";
        } else if (!args.contains(",")) {
            errorString = "Argument contains no parameters";
        } else {
            int idx = 0;
            boolean parsed = true;
            String[] argsArray = args.split(",");
            int argsArrayLen = argsArray.length;

            // Must have at least the minimum number of arguments to parse
            if (argsArrayLen < MinimumArgs) {
                errorString = "Too few arguments to parse " + args + " as a transaction request";
                parsed = false;
            }

            // Parses the user command
            if (parsed) {
                if (argsArray[idx].contains("[")) {
                    String sequenceStr = argsArray[idx].substring(1, argsArray[idx].indexOf("]"));
                    try {
                        workloadSeqNum = Integer.parseInt(sequenceStr);
                    } catch (NumberFormatException e) {
                        errorString = "Could not find the sequence number from " + args;
                        parsed = false;
                    }
                    idx++;
                } else {
                    parsed = false;
                }

                if (argsArray[idx].contains("[")) {
                    String sequenceStr = argsArray[idx].substring(1, argsArray[idx].indexOf("]"));
                    try {
                        userSeqNum = Integer.parseInt(sequenceStr);

                        // If one of the parameters was a sequence, then there needs to be one more argument at minimum
                        if (argsArrayLen < MinimumArgs + 1) {
                            errorString = "Too few arguments to parse " + args + " as a transaction request";
                            parsed = false;
                        }
                    } catch (NumberFormatException e) {
                        // Don't do anything
                        // TODO: Should this be a required parameter?
                    }
                    idx++;
                }

                try {
                    userCommand = UserCommand.values()[Integer.parseInt(argsArray[idx])];
                    idx++;
                } catch (NumberFormatException e) {
                    errorString = "Could not parse " + argsArray[0] + " as a user command (need number)";
                    parsed = false;
                } catch (ArrayIndexOutOfBoundsException e) {
                    errorString = "Could not parse " + argsArray[0] + " as a user command (need number from 1-17 inclusive)";
                    parsed = false;
                }
            }

            // First pass of parsing user command arguments
            if (parsed) {
                switch (userCommand) {
                    case ADD:
                    case QUOTE:
                    case BUY:
                    case COMMIT_BUY:
                    case CANCEL_BUY:
                    case SELL:
                    case COMMIT_SELL:
                    case CANCEL_SELL:
                    case SET_BUY_AMOUNT:
                    case CANCEL_SET_BUY:
                    case SET_BUY_TRIGGER:
                    case SET_SELL_AMOUNT:
                    case SET_SELL_TRIGGER:
                    case CANCEL_SET_SELL:
                    case DUMPLOG:
                    case DISPLAY_SUMMARY:
                        userName = argsArray[idx++];
                        break;
                    case DUMPLOG_ROOT:
                        parsed = parseFileName(argsArray[idx++]);
                        if (parsed) {
                            userName = ROOT_USER;
                        }
                        break;
                    default:
                        errorString = "Could not parse arguments for user command: " + userCommand.toString();
                        parsed = false;
                        break;
                }
            }

            // Second pass of parsing user command arguments
            if (parsed) {
                switch (userCommand) {
                    case ADD:
                        parsed = parseMoneyAmount(argsArray[idx++]);
                        break;
                    case QUOTE:
                    case BUY:
                    case SELL:
                    case SET_BUY_AMOUNT:
                    case CANCEL_SET_BUY:
                    case SET_BUY_TRIGGER:
                    case SET_SELL_AMOUNT:
                    case SET_SELL_TRIGGER:
                    case CANCEL_SET_SELL:
                        parsed = parseStockSymbol(argsArray[idx++]);
                        break;
                    case COMMIT_BUY:
                    case CANCEL_BUY:
                    case COMMIT_SELL:
                    case CANCEL_SELL:
                    case DUMPLOG_ROOT:
                    case DISPLAY_SUMMARY:
                        break;
                    case DUMPLOG:
                        parsed = parseFileName(argsArray[idx++]);
                        break;
                    default:
                        errorString = "Could not parse arguments for user command: " + userCommand.toString();
                        parsed = false;
                        break;
                }
            }

            // Third and final pass of parsing user command arguments (no user command has more than three arguments)
            if (parsed) {
                switch (userCommand) {
                    case ADD:
                    case QUOTE:
                    case COMMIT_BUY:
                    case CANCEL_BUY:
                    case COMMIT_SELL:
                    case CANCEL_SELL:
                    case CANCEL_SET_BUY:
                    case CANCEL_SET_SELL:
                    case DUMPLOG:
                    case DUMPLOG_ROOT:
                    case DISPLAY_SUMMARY:
                        break;
                    case BUY:
                    case SELL:
                    case SET_BUY_AMOUNT:
                    case SET_BUY_TRIGGER:
                    case SET_SELL_AMOUNT:
                    case SET_SELL_TRIGGER:
                        parsed = parseMoneyAmount(argsArray[idx++]);
                        break;
                    default:
                        errorString = "Could not parse arguments for user command: " + userCommand.toString();
                        parsed = false;
                        break;
                }
            }

            // Parses time stamp and web server name/port indices
            if (parsed) {
                if (idx + 3 < argsArrayLen) {
                    errorString = "Too many arguments";
                    parsed = false;
                } else if (idx + 3 > argsArrayLen) {
                    errorString = "Not enough arguments";
                } else {
                    try {
                        timeStamp = Long.parseLong(argsArray[idx++]);
                        webServerNameIdx = Integer.parseInt(argsArray[idx++]);
                        webServerPortIdx = Integer.parseInt(argsArray[idx++]);

                        if (webServerNameIdx < 0 || webServerNameIdx >= TxMain.Deployment.getWebServers().getServers().size()) {
                            errorString = "Web server name index is invalid";
                            parsed = false;
                        } /*else if (webServerPortIdx < 0 || webServerPortIdx >= 1) {
                            errorString = "Web server port index is invalid";
                            parsed = false;
                        }*/
                    } catch (NumberFormatException e) {
                        errorString = "Unable to parse a timestamp or web server address";
                        parsed = false;
                    }
                }
            }

            // If this error string gets set, an error assignment is missing in the parsing above; this is a backup to
            // avoid hard-to-find errors
            if (errorString.isEmpty() && !parsed) {
                errorString = "Unknown parsing error";
            }

            if (!parsed) {
                InternalLog.Log("[TX OBJECT ERROR] " + errorString);
            }
        }
    }

    /**
     * Getter for the user command property. Should never be the "NO_COMMAND" value, unless the error string has a
     * value.
     *
     * @return The user command for the request.
     */
    public UserCommand getUserCommand() {
        return userCommand;
    }

    /**
     * Getter for the user name property. Should never be null. If the error string has a value, or if the user command
     * does not have a user name argument, this may be the empty string.
     *
     * @return The user name property for the request.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Getter for the stock symbol property. Should never be null. If the error string has a value, or if the user
     * command does not have a stock symbol argument, this may be the empty string.
     *
     * @return The stock symbol property for the request.
     */
    public String getStockSymbol() {
        return stockSymbol;
    }

    /**
     * Getter for the dollar amount property. Should never be negative. If the error string has a value, the user
     * command does not have an amount argument, or the amount is actually $0.xx, this may be 0.
     *
     * @return The dollar amount for the request.
     */
    public int getAmountDollars() {
        return amountDollars;
    }

    /**
     * Getter for the cent amount property. Should never be negative. If the error string has a value, the user command
     * does not have an amount argument, or the amount is actually $x.00, this may be 0.
     *
     * @return The cent amount for the request.
     */
    public int getAmountCents() {
        return amountCents;
    }

    /**
     * Getter for the timestamp property. Should never be null or empty unless the error string has a value.
     *
     * @return The timestamp for when the user launched this command with the web server.
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Getter for the file name property. Should never be null. Unless this is a DUMPLOG user command and the error
     * string has no value, this will be the empty string.
     *
     * @return The file name for this argument.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Getter for the web server name index. Should only be negative if the error string has a value. Should never be
     * greater than the number of entries in the ServerConstants.WEB_SERVERS.
     *
     * @return The name index of the web server that made this request.
     */
    public int getWebServerNameIdx() {
        return webServerNameIdx;
    }

    /**
     * Getter for the web server port index. Should only be negative if the error string has a value. Should never be
     * greater than the number of entries in the ServerConstants.TX_PORT_RANGE.
     *
     * @return The port index of the web server that made this request.
     */
    public int getWebServerPortIdx() {
        return webServerPortIdx;
    }

    /**
     * @return The workload sequence identifier number of this request. Primarily for test logging purposes.
     */
    public int getWorkloadSeqNum() {
        return workloadSeqNum;
    }

    /**
     * @return The sequence number of this user request for transaction processing ordering.
     */
    public int getUserSeqNum() {
        return userSeqNum;
    }

    /**
     * Getter for the error string for this request object. If any value failed to parse, this will have a value other
     * than the empty string. Check this before calling any other property. If this is not an empty string, then the
     * values contained in the other getters are undefined. Should never be null.
     *
     * @return The error string for the request. An empty string indicates no error.
     */
    public String getErrorString() {
        return errorString;
    }

    /**
     * Adds a response to the server message that this transaction was built from. This will queue and eventually
     * send the response to the original requester.
     *
     * @param response The response to send to the requester.
     */
    public void sendResponse(String response) {
        serverMessage.setResponse(response);
    }

    /**
     * Returns the original request string that this transaction object was built from.
     *
     * @return The string representation of this object.
     */
    @Override
    public String toString() {
        return serverMessage != null ? serverMessage.getData() : "";
    }

    /**
     * Parses a string as a dollar value, splitting the result into two integers - one for dollars and one for cents.
     * Assumes the dollar value is in a decimal format. If no decimal is present, it is assumed that the number is the
     * dollar amount and that cents are zero. Assigns these to transaction object dollars and cents properties if
     * successful. If not, writes an error string.
     *
     * @param amount The string to parse into a dollar value.
     * @return Whether the string was successfully parsed.
     */
    private boolean parseMoneyAmount(String amount) {
        boolean parsed = false;
        if (amount != null && !amount.isEmpty()) {
            try {
                String[] amounts = amount.split("\\.");
                int amountsLength = amounts.length;
                if (amountsLength == 0 || amountsLength > 2) {
                    errorString = "Error splitting " + amount + " as a money amount for parsing";
                } else {
                    /*
                    amountMoney = new BigDecimal(amounts[0]);
                    if (amountMoney.signum() < 0) {
                        errorString = "Error parsing amount - negative dollar values are not allowed";
                    } else {
                        parsed = true;
                    }
                    */
                    amountDollars = Integer.parseInt(amounts[0]);
                    amountCents = amountsLength == 2 ? Integer.parseInt(amounts[1]) : 0;
                    parsed = true;
                }
            } catch (NumberFormatException e) {
                errorString = "Could not parse " + amount + " as a money amount";
            }
        } else {
            errorString = "Money amount was null or empty";
        }

        return parsed;
    }

    /**
     * Parses a string as a stock symbol. Checks that the stock symbol is not longer than allowed. Assigns the value
     * to the transaction object stock symbol property if successful. If not, writes an error string.
     *
     * @param symbol The string to parse into a stock symbol.
     * @return Whether the string was successfully parsed.
     */
    private boolean parseStockSymbol(String symbol) {
        boolean parsed = false;
        String checkVal = symbol;
        if (checkVal.length() > StockMaxLength) {
            errorString = "Could not parse " + checkVal + " as a stock symbol";
        } else {
            stockSymbol = symbol;
            parsed = true;
        }

        return parsed;
    }

    /**
     * Parses a string as a file name.
     * TODO: Actually parse it properly. Currently only checks for null/empty.
     * Assigns the value to the transaction object file name property if successful. If not, writes an error string.
     *
     * @param name The string to parse into a file name.
     * @return Whether the string was successfully parsed.
     */
    private boolean parseFileName(String name) {
        boolean parsed = false;
        if (name != null && !name.isEmpty()) {
            fileName = name;
            parsed = true;
        } else {
            errorString = "Could not parse " + name + " as a file name";
        }

        return parsed;
    }
}
