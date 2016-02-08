package commands;

/**
 * Created by Evan on 1/19/2016.
 */
public class CancelSetBuyCommand extends Command {
    private String userId;
    private String stockSymbol;

    @Override
    public String getCmdType() {
        return CommandTypes.CANCEL_SET_BUY;
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

    public CancelSetBuyCommand(){}

    public CancelSetBuyCommand(String userId, String stockSymbol) {
        this.userId = userId;
        this.stockSymbol = stockSymbol;
    }
}
