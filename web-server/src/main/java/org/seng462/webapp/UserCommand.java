package org.seng462.webapp;

import java.util.ArrayList;

/**
 * Created by Evan on 2/5/2016.
 */
public class UserCommand
{
    private int cmdCode;
    private ArrayList<String> args = new ArrayList<String>();

    public int getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(int cmdCode) {
        this.cmdCode = cmdCode;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public void setArgs(ArrayList<String> args) {
        this.args = args;
    }

    public UserCommand(int cmdCode, String... args)
    {
        this.cmdCode = cmdCode;
        for(String arg : args)
        {
            this.args.add(arg);
        }
    }
}
