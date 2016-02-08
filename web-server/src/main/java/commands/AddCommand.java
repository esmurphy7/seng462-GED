package commands;

/**
 * Created by Evan on 1/17/2016.
 */
public class AddCommand extends Command {
    private String userId;
    private int dollars;
    private int cents;

    @Override
    public String getCmdType() {
        return CommandTypes.ADD;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDollars() {
        return dollars;
    }

    public void setDollars(int dollars) {
        this.dollars = dollars;
    }

    public int getCents() {
        return cents;
    }

    public void setCents(int cents) {
        this.cents = cents;
    }

    public AddCommand(){}

    public AddCommand(String userId, int dollars, int cents) {
        this.userId = userId;
        this.dollars = dollars;
        this.cents = cents;
    }


}
