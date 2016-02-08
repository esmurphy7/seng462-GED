package commands;

/**
 * Created by Evan on 1/19/2016.
 */
public class CancelBuyCommand extends Command {
    private String userId;

    @Override
    public String getCmdType() {
        return CommandTypes.CANCEL_BUY;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CancelBuyCommand(String userId) {
        this.userId = userId;
    }
}
