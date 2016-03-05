package com.teamged.logging;

import com.teamged.ServerConstants;
import com.teamged.logging.xmlelements.generated.LogType;
import com.teamged.logging.xmlelements.generated.ObjectFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by DanielF on 2016-03-03.
 */
public class LogProcessingHandler implements Runnable {
    private static JAXBContext jc;
    private static final String LOGFILE_SCHEMA = "logfile.xsd";
    private final Object logObj;

    static {
        try {
            jc = JAXBContext.newInstance(LogType.class);
        } catch (JAXBException e) {
            jc = null;
            e.printStackTrace();
        }
    }

    public LogProcessingHandler(Object log) {
        logObj = log;
    }

    @Override
    public void run() {
        if (logObj.getClass() == String.class) {
            try (Socket s = new Socket(ServerConstants.AUDIT_SERVERS[0], ServerConstants.AUDIT_LOG_PORT);
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true)
            ) {
                out.println(logObj);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            //System.out.println("Connecting: " + ServerConstants.AUDIT_SERVERS[0]);
            // create a logtype to marshall
            LogType logType = new LogType();
            logType.getUserCommandOrQuoteServerOrAccountTransaction().add(logObj);

            // get the marshaller
            Marshaller marshaller = BuildMarshaller();

            // create jaxb element from xml element name, class, and instance
            JAXBElement<LogType> jaxbElement = new ObjectFactory().createLog(logType);

            String ser;
            // marshall the element over the socket
            StringWriter sw = new StringWriter();
            try {
                marshaller.marshal(jaxbElement, sw);
                ser = sw.toString();
                try (Socket s = new Socket(ServerConstants.AUDIT_SERVERS[0], ServerConstants.AUDIT_LOG_PORT);
                     PrintWriter pw = new PrintWriter(s.getOutputStream(), true)) {
                    pw.println(ser);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    // build and return the marshaller to use for this singleton
    private static Marshaller BuildMarshaller() {
        Marshaller marshaller = null;
        try {
            // define schema
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = Logger.class.getResource(LOGFILE_SCHEMA);
            File schemaFile = new File(url.getPath());
            Schema schema = sf.newSchema(schemaFile);

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
}
