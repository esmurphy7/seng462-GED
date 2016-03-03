package org.seng462.webapp.logging;

import org.seng462.webapp.logging.xmlelements.generated.LogType;
import org.seng462.webapp.logging.xmlelements.generated.ObjectFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Evan on 2/3/2016.
 */
// Singleton class used to manage system logs
public class Logger
{
    private static Logger instance = null;

    private static final String LOGFILE_SCHEMA = "logfile.xsd";
    private static final String OUTPUT_LOGFILE = "outputLog.xml";
    private static Queue<Object> Logs = new ConcurrentLinkedQueue<>();

    private Logger(){}

    public static synchronized Logger getInstance()
    {
        if (instance == null)
        {
            instance = new Logger();
        }
        return instance;
    }

    // build and return the marshaller to use for this singleton
    private static Marshaller buildMarshaller()
    {
        Marshaller marshaller = null;
        try
        {
            // define schema
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = Logger.class.getResource(LOGFILE_SCHEMA);
            File schemaFile = new File(url.getPath());
            Schema schema = sf.newSchema(schemaFile);

            // build jaxb context
            JAXBContext jc = JAXBContext.newInstance(LogType.class);

            // build marshaller
            marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setSchema(schema);
            //marshaller.setEventHandler(new DebugValidationEventHandler());
            marshaller.setEventHandler(new DefaultValidationEventHandler());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return marshaller;
    }

    // Store the log in a list to save later
    public void Log(Object logType)
    {
        Logs.add(logType);
    }

    // write the log to an xml file on disk
    public void SaveLog() throws JAXBException {
        // include log type instances in base log element
        LogType logType = new LogType();
        for (Object log : Logs) {
            logType.getUserCommandOrQuoteServerOrAccountTransaction().add(log);
        }
        
        try {
            // define output location
            URL url = Logger.class.getResource("");
            File outfile = new File(url.getPath() + OUTPUT_LOGFILE);
            outfile.createNewFile();
            OutputStream outStream = new FileOutputStream(outfile);

            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<LogType> jaxbLogType = objectFactory.createLog(logType);

            // marshall the data
            Marshaller marshaller = Logger.getInstance().buildMarshaller();
            marshaller.marshal(jaxbLogType, outStream);
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
