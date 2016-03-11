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
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
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

    private static List<Queue<Object>> logQueueStorage = new ArrayList<>();
    private static Queue<Object> logQueue = new ArrayDeque<>(QUEUE_SIZE);
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
        synchronized (queueLock) {
            try {
                logQueue.add(log);
                if (logQueue.size() == QUEUE_SIZE) {
                    logQueueStorage.add(logQueue);
                    logQueue = new ArrayDeque<>(QUEUE_SIZE);
                    InternalLog.Log((QUEUE_SIZE * logQueueStorage.size()) + " logs are now stored in the audit server");
                }
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
        List<Queue<Object>> dumpQueueList;

        synchronized (queueLock) {
            dumpQueueList = logQueueStorage;
            logQueueStorage = new ArrayList<>();

            dumpQueueList.add(logQueue);
            logQueue = new ArrayDeque<>(QUEUE_SIZE);
        }

        LogType logType = new LogType();
        Object nextLog;
        for (Queue<Object> q : dumpQueueList) {
            while ((nextLog = q.poll()) != null) {
                logType.getUserCommandOrQuoteServerOrAccountTransaction().add(nextLog);
            }
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

        File statsOut = new File(url.getPath() + "timings");
        try (FileWriter write = new FileWriter(url.getPath() + "timings");
            PrintWriter outWriter = new PrintWriter(write)
        ){
            String s;
            while ((s = timestamps.poll()) != null) {
                outWriter.write(s);
            }
        } catch (IOException e) {
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
