package com.teamged.auditserver.threads;

import com.teamged.auditserver.AuditMain;
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
public class LogConnectionHandler implements Runnable {
    private final Socket socket;

    public LogConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
        {
            StringBuilder s = new StringBuilder();
            String message;
            while ((message = in.readLine()) != null) {
                s.append(message);
            }

            AuditMain.PutLogQueue(s.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
