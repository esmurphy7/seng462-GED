package com.teamged.auditlogging;

import com.teamged.auditlogging.generated.LogType;
import com.teamged.auditlogging.generated.ObjectFactory;
import com.teamged.auditserver.AuditMain;
import com.teamged.auditserver.InternalLog;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Evan on 2/3/2016.
 * Modified by DanielF on 4/3/2016.
 */
public class LogManager {
    private static final String LOGFILE_SCHEMA = "logfile.xsd";
    private static Queue<Object> logQueue = new LinkedBlockingQueue<>();
    private static final Object queueLock = new Object();

    private static final String OUTPUT_LOGFILE = "outputLog.xml";
    private static final String XML_OPEN = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<log>";
    private static final String XML_CLOSE = "</log>\n";
    private static JAXBContext jc;

    static {
        try {
            jc = JAXBContext.newInstance(LogType.class);
        } catch (JAXBException e) {
            jc = null;
            e.printStackTrace();
        }
    }

    /**
     * Save the current log queue to a file on disk.
     */
    public static void SaveLog() {
        URL url = LogManager.class.getResource("");
        File outfile = new File(url.getPath() + OUTPUT_LOGFILE);
        try {
            outfile.createNewFile();
            PrintWriter writer = new PrintWriter(outfile);

            Pattern re = Pattern.compile("^<log>(.*)</log>$");
            writer.append(XML_OPEN + "\n");
            String nextLog;
            while ((nextLog = AuditMain.takeLogQueue()) != null) {
                Matcher m = re.matcher(nextLog);
                if (m.matches()) {
                    writer.append(m.group(1) + "\n");
                }
            }
            writer.append(XML_CLOSE + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a log to the log queue.
     *
     * @param log The log to add to the queue.
     */
    public static void AddLog(Object log) {
        synchronized (queueLock) {
            try {
                logQueue.add(log);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                InternalLog.Log("Log was not added to log queue - capacity exceeded.");
            }
        }
    }

    /**
     * Dumps the current contents of the log to disk. Empties the current log queue.
     */
    public static void DumpLog() {
        Queue<Object> dumpQueue;

        synchronized (queueLock) {
            dumpQueue = logQueue;
            logQueue = new LinkedList<>();
        }

        LogType logType = new LogType();
        Object nextLog;
        while ((nextLog = dumpQueue.poll()) != null) {
            logType.getUserCommandOrQuoteServerOrAccountTransaction().add(nextLog);
        }

        URL url = LogManager.class.getResource("");
        File outfile = new File(url.getPath() + OUTPUT_LOGFILE);
        try {
            outfile.createNewFile();
            OutputStream outStream = new FileOutputStream(outfile);

            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<LogType> jaxbLogType = objectFactory.createLog(logType);

            Marshaller marshaller = buildMarshaller();
            marshaller.marshal(jaxbLogType, outStream);
            outStream.close();
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds the marshaller for saving logs to file.
     *
     * @return The XML log marshaller.
     */
    private static Marshaller buildMarshaller() {
        Marshaller marshaller = null;
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = LogManager.class.getResource(LOGFILE_SCHEMA);
            File schemaFile = new File(url.getPath());
            Schema schema = sf.newSchema(schemaFile);

            marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setSchema(schema);
            marshaller.setEventHandler(new DebugValidationEventHandler());
        } catch (SAXException | JAXBException e) {
            e.printStackTrace();
        }

        return marshaller;
    }
}
