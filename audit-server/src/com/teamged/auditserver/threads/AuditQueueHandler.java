package com.teamged.auditserver.threads;

import com.teamged.logging.DebugValidationEventHandler;
import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.generated.LogType;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

/**
 * Created by DanielF on 2016-03-03.
 */
public class AuditQueueHandler implements Runnable {
    private static final String LOGFILE_SCHEMA = "logfile.xsd";
    private final String log;

    public AuditQueueHandler(String log) {
        this.log = log;
    }

    @Override
    public void run() {
        XMLStreamReader xmlSr = null;
        try {
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
            xmlSr = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(log.getBytes()));
            JAXBElement<LogType> outerLog = unmarshaller.unmarshal(xmlSr, LogType.class);

            // since the current logging system wraps each log with the root log, it must be unwrapped
            LogType innerLog = outerLog.getValue();
            Object innerValue = innerLog.getUserCommandOrQuoteServerOrAccountTransaction().get(0);
            if (innerValue == null)
            {
                return;
            }

            Logger.getInstance().Log(innerValue);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            if (xmlSr != null) {
                try {
                    xmlSr.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
