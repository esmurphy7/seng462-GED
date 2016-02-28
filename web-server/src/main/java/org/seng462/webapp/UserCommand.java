package org.seng462.webapp;

import java.util.ArrayList;

/**
 * Created by Evan on 2/5/2016.
 */
public class UserCommand
{
    private CommandCodes cmdCode;
    private String workloadSeqNo;
    private String userSeqNo;
    private ArrayList<String> args = new ArrayList<String>();

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

    public ArrayList<String> getArgs() {
        return args;
    }

    public void setArgs(ArrayList<String> args) {
        this.args = args;
    }

    public UserCommand(CommandCodes cmdCode, String workloadSeqNo, String userSeqNo, String... args)
    {
        this.cmdCode = cmdCode;
        this.workloadSeqNo = workloadSeqNo;
        this.userSeqNo = userSeqNo;
        for(String arg : args)
        {
            this.args.add(arg);
        }
    }
}
