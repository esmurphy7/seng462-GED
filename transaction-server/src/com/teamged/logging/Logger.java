package com.teamged.logging;

import com.teamged.ServerConstants;
import com.teamged.logging.xmlelements.generated.LogType;
import com.teamged.logging.xmlelements.generated.ObjectFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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
    private static Marshaller marshaller;
    private static Queue<Object> Logs = new ConcurrentLinkedQueue<>();

    private Logger(){}

    public static synchronized Logger getInstance()
    {
        if (instance == null)
        {
            instance = new Logger();
            Marshaller marshaller = Logger.buildMarshaller();
            instance.marshaller = marshaller;
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
            //marshaller.marshal(jaxbLogType, System.out);
            marshaller.marshal(jaxbLogType, outStream);
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
