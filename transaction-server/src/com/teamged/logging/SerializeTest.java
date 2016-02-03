package com.teamged.logging;

import com.teamged.logging.xmlelements.generated.CommandType;
import com.teamged.logging.xmlelements.generated.LogType;
import com.teamged.logging.xmlelements.generated.QuoteServerType;
import com.teamged.logging.xmlelements.generated.UserCommandType;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by Evan on 2/2/2016.
 */
public class SerializeTest
{
    public static void main(String[] args)
    {
        // create instances of log types
        // usercommandtype test object
        UserCommandType userCommand = new UserCommandType();
        userCommand.setTimestamp(1167631200);
        userCommand.setServer("CLT1");
        userCommand.setTransactionNum(BigInteger.valueOf(1));
        userCommand.setCommand(CommandType.ADD);
        userCommand.setUsername("testUser");
        userCommand.setStockSymbol("STK");
        userCommand.setFilename("testFilename");
        userCommand.setFunds(BigDecimal.valueOf(100.00));

        // quoteservertype test object
        QuoteServerType quoteServer = new QuoteServerType();
        quoteServer.setTimestamp(1421242342);
        quoteServer.setServer("testServer");
        quoteServer.setTransactionNum(BigInteger.valueOf(2));
        quoteServer.setPrice(BigDecimal.valueOf(50.00));
        quoteServer.setStockSymbol("STK");
        quoteServer.setUsername("testUser");
        quoteServer.setQuoteServerTime(BigInteger.valueOf(1234));
        quoteServer.setCryptokey("testKey");

        // include log type instances in base log element
        LogType logType = new LogType();
        logType.getUserCommandOrQuoteServerOrAccountTransaction().add(userCommand);
        logType.getUserCommandOrQuoteServerOrAccountTransaction().add(quoteServer);

        // log and save the test logs
        for(Object log : logType.getUserCommandOrQuoteServerOrAccountTransaction())
        {
            Logger.getInstance().Log(log);
        }
        try {
            Logger.getInstance().SaveLog();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
