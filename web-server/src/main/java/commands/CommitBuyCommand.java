package commands;

/**
 * Created by Evan on 1/19/2016.
 */
public class CommitBuyCommand extends Command {
    private String userId;

    @Override
    public String getCmdType() {
        return CommandTypes.COMMIT_BUY;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CommitBuyCommand(){}

    public CommitBuyCommand(String userId) {
        this.userId = userId;
    }
}
