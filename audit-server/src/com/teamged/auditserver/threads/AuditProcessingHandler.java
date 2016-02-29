package com.teamged.auditserver.threads;

import com.teamged.auditserver.InternalLog;
import com.teamged.logging.DebugValidationEventHandler;
import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.generated.LogType;
import com.teamged.logging.xmlelements.generated.QuoteServerType;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.Socket;
import java.net.URL;

/**
 * Created by DanielF on 2016-02-23.
 */
public class AuditProcessingHandler implements Runnable {
    private static final String LOGFILE_SCHEMA = "logfile.xsd";
    private final Socket socket;

    public AuditProcessingHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try
        {
            InternalLog.Log("Processing log");

            // define schema
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = Logger.class.getResource(LOGFILE_SCHEMA);
            File schemaFile = new File(url.getPath());
            Schema schema = null;
            try {
                schema = sf.newSchema(schemaFile);
            } catch (SAXException e) {
                e.printStackTrace();
                return;
            }

            // build jaxb context
            JAXBContext jc = JAXBContext.newInstance(LogType.class);

            // build unmarshaller
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(new DebugValidationEventHandler());

            // read xml from socket
            InputStream in = socket.getInputStream();
            XMLStreamReader xmlSr = XMLInputFactory.newInstance().createXMLStreamReader(in);
            JAXBElement<LogType> outerLog = unmarshaller.unmarshal(xmlSr, LogType.class);

            // since the current logging system wraps each log with the root log, it must be unwrapped
            LogType innerLog = outerLog.getValue();
            Object innerValue = innerLog.getUserCommandOrQuoteServerOrAccountTransaction().get(0);
            if (innerValue == null)
            {
                return;
            }

            Logger.getInstance().Log(innerValue);
        } catch (IOException | JAXBException | XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
