package com.teamged.auditlogging;

import com.teamged.auditlogging.generated.LogType;
import com.teamged.auditlogging.generated.ObjectFactory;
import com.teamged.auditserver.InternalLog;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Evan on 2/3/2016.
 * Modified by DanielF on 4/3/2016.
 */
public class LogManager {
    private static final String LOGFILE_SCHEMA = "logfile.xsd";
    private static final String OUTPUT_LOGFILE = "outputLog.xml";
    private static final Object queueLock = new Object();
    private static final int QUEUE_SIZE = 100000;

    private static Queue<Object> logQueue = new ArrayDeque<>(QUEUE_SIZE);
    private static final Object outfileLock = new Object();
    private static boolean isFirstLog = true;

    private static JAXBContext jc;

    static {
        try {
            jc = JAXBContext.newInstance(LogType.class);
        } catch (JAXBException e) {
            jc = null;
            e.printStackTrace();
        }
    }

    public static Queue<String> timestamps = new ConcurrentLinkedQueue<>();

    /**
     * Adds a log to the log queue.
     *
     * @param log The log to add to the queue.
     */
    public static void AddLog(Object log) {
        boolean writeLogQueue = false;
        Queue<Object> tempLogQueue = new ArrayDeque<>();

        synchronized (queueLock) {
            try {
                logQueue.add(log);
                if (logQueue.size() == QUEUE_SIZE) {
                    tempLogQueue = logQueue;
                    logQueue = new ArrayDeque<>(QUEUE_SIZE);
                    writeLogQueue = true;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
                InternalLog.Log("Log was not added to log queue - capacity exceeded.");
            }
        }

        // write a queue of logs to the logfile if there's one ready
        if(writeLogQueue)
        {
            LogType logType = new LogType();
            for (Object nextLog : tempLogQueue)
            {
                logType.getUserCommandOrQuoteServerOrAccountTransaction().add(nextLog);
            }

            appendLogFile(logType, true);
        }
    }

    /**
     * Dumps the current contents of the log to disk. Empties the current log queue.
     */
    public static void DumpLog()
    {
        Queue<Object> tempLogQueue = new ArrayDeque<>();
        synchronized (queueLock)
        {
            // copy then clear remaining logs
            tempLogQueue = logQueue;
            logQueue = new ArrayDeque<>(QUEUE_SIZE);
        }

        try
        {
            // append remaining logs to file
            LogType logType = new LogType();
            for (Object nextLog : tempLogQueue)
            {
                logType.getUserCommandOrQuoteServerOrAccountTransaction().add(nextLog);
            }
            appendLogFile(logType, false);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Append set of logs to a file on disk.
     * @param logType
     * @param trimTrailingTag Choose to trim the "</log>" tag from the log's resulting xml
     */
    private static void appendLogFile(LogType logType, boolean trimTrailingTag)
    {
        synchronized (outfileLock)
        {
            URL url = LogManager.class.getResource("");
            File outfile = new File(url.getPath() + OUTPUT_LOGFILE);
            try
            {
                StringWriter sw = new StringWriter();
                FileWriter fw = new FileWriter(outfile, true);

                ObjectFactory objectFactory = new ObjectFactory();
                JAXBElement<LogType> jaxbLogType = objectFactory.createLog(logType);

                // marshal log set into stream
                Marshaller marshaller = buildMarshaller();
                // omit xml declaration
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                marshaller.marshal(jaxbLogType, sw);

                String logStr = sw.toString();

                // remove leading "<log>" tag if it's not the first log
                if(!isFirstLog)
                {
                    logStr = sw.toString().replaceAll("^<log>+", "");
                }
                else
                {
                    // add xml declaration
                    XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(fw);
                    writer.writeStartDocument();

                    writer.close();
                    isFirstLog = false;
                }

                // remove trailing "</log>" tag unless specified not to
                if(trimTrailingTag)
                {
                    logStr = logStr.replaceAll("</log>+$", "");
                }

                // if the log set is empty, the marshaller has written a self-closing "<log/>" tag which breaks our xml
                // replace "<log/>" with "</log>"
                if(logType.getUserCommandOrQuoteServerOrAccountTransaction() == null || logType.getUserCommandOrQuoteServerOrAccountTransaction().size() == 0)
                {
                    logStr = logStr.replaceAll("<log/>+$","</log>");
                }

                // write the edited xml to the file
                fw.write(logStr);

                fw.close();
                sw.close();

            } catch (IOException | JAXBException | XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  Deletes the output logfile
     */
    public static void DeleteLogs()
    {
        URL url = LogManager.class.getResource("");

        File logfile = new File(url.getPath() + OUTPUT_LOGFILE);
        boolean success = logfile.delete();
        if(!success)
        {
            InternalLog.Log("Could not delete logfile at: "+url.getPath()+OUTPUT_LOGFILE);
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
