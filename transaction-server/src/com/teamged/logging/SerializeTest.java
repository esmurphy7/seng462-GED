package com.teamged.logging;

import com.teamged.logging.xmlelements.generated.*;
import com.teamged.txserver.transactions.UserCommand;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;

/**
 * Created by Evan on 2/2/2016.
 */
public class SerializeTest
{
    public static void main(String[] args) throws JAXBException, SAXException {
        class MyValidationEventHandler implements ValidationEventHandler
        {

            @Override
            public boolean handleEvent(ValidationEvent event) {
                System.out.println("\nEVENT");
                System.out.println("SEVERITY:  " + event.getSeverity());
                System.out.println("MESSAGE:  " + event.getMessage());
                System.out.println("LINKED EXCEPTION:  " + event.getLinkedException());
                System.out.println("LOCATOR");
                System.out.println("    LINE NUMBER:  " + event.getLocator().getLineNumber());
                System.out.println("    COLUMN NUMBER:  " + event.getLocator().getColumnNumber());
                System.out.println("    OFFSET:  " + event.getLocator().getOffset());
                System.out.println("    OBJECT:  " + event.getLocator().getObject());
                System.out.println("    NODE:  " + event.getLocator().getNode());
                System.out.println("    URL:  " + event.getLocator().getURL());
                return true;
            }
        }

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

        // define schema
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL url = SerializeTest.class.getResource("logfile.xsd");
        File schemaFile = new File(url.getPath());
        Schema schema = sf.newSchema(schemaFile);

        // build jaxb context
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<LogType> jaxbLogType = objectFactory.createLog(logType);
        JAXBContext jc = JAXBContext.newInstance(LogType.class);

        // marshall the data
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(schema);
        marshaller.setEventHandler(new MyValidationEventHandler());
        marshaller.marshal(jaxbLogType, System.out);
    }
}
