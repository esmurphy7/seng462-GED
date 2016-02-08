package commands;

/**
 * Created by Evan on 1/19/2016.
 */
public class BuyCommand extends Command {
    private String userId;
    private String stockSymbol;
    private int dollars;
    private int cents;

    @Override
    public String getCmdType() {
        return CommandTypes.BUY;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
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

    public BuyCommand(){}

    public BuyCommand(String userId, String stockSymbol, int dollars, int cents) {
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.dollars = dollars;
        this.cents = cents;
    }
}
