package org.seng462.webapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evan on 2/5/2016.
 */
public class UserCommand
{
    private CommandCodes cmdCode;
    private String workloadSeqNo;
    private String userSeqNo;
    private Map<String, String> args = new HashMap<>();

    public CommandCodes getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(CommandCodes cmdCode) {
        this.cmdCode = cmdCode;
    }

    public String getUserSeqNo() {
        return userSeqNo;
    }

    public void setUserSeqNo(String userSeqNo) {
        this.userSeqNo = userSeqNo;
    }

    public String getWorkloadSeqNo() {
        return workloadSeqNo;
    }

    public void setWorkloadSeqNo(String workloadSeqNo) {
        this.workloadSeqNo = workloadSeqNo;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    public String getArg(String argKey)
    {
        String arg = args.get(argKey);
        return (arg != null) ? arg : "";
    }

    public UserCommand(CommandCodes cmdCode, String workloadSeqNo, String userSeqNo, HashMap<String,String> args)
    {
        this.cmdCode = cmdCode;
        this.workloadSeqNo = workloadSeqNo;
        this.userSeqNo = userSeqNo;
        this.args = args;
    }
}
