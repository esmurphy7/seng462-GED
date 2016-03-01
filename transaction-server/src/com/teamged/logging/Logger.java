package com.teamged.logging;

import com.teamged.ServerConstants;
import com.teamged.logging.xmlelements.generated.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.namespace.QName;
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
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setEventHandler(new DefaultValidationEventHandler());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return marshaller;
    }

    // Marshall the log object and send it over the socket to the audit server
    public void Log(Object logInstance) {
        //System.out.println("Connecting: " + ServerConstants.AUDIT_SERVERS[0]);
        try (Socket s = new Socket(ServerConstants.AUDIT_SERVERS[0], ServerConstants.AUDIT_LOG_PORT))
        {
            // create a logtype to marshall
            LogType logType = new LogType();
            logType.getUserCommandOrQuoteServerOrAccountTransaction().add(logInstance);

            // get the marshaller
            Marshaller marshaller = Logger.getInstance().buildMarshaller();

            // create jaxb element from xml element name, class, and instance
            JAXBElement<LogType> jaxbElement = new ObjectFactory().createLog(logType);

            // marshall the element over the socket
            marshaller.marshal(jaxbElement, s.getOutputStream());
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    // signal the audit server to save the log file
    public void SaveLog() throws JAXBException {
        System.out.println("Connecting: " + ServerConstants.AUDIT_SERVERS[0]);
        try (Socket s = new Socket(ServerConstants.AUDIT_SERVERS[0], ServerConstants.AUDIT_DUMP_PORT))
        {
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.println("DUMPLOG");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
