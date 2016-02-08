package commands;

/**
 * Created by Evan on 1/19/2016.
 */
public class QuoteCommand extends Command {
    private String userId;
    private String stockSymbol;

    @Override
    public String getCmdType() {
        return CommandTypes.QUOTE;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public QuoteCommand(){}

    public QuoteCommand(String userId, String stockSymbol) {
        this.userId = userId;
        this.stockSymbol = stockSymbol;
    }
}
