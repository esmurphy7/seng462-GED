package com.teamged.auditserver.threads;

import com.teamged.auditserver.InternalLog;
import com.teamged.logging.Logger;
import com.teamged.logging.xmlelements.generated.LogType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;

/**
 * Created by DanielF on 2016-02-23.
 */
public class AuditProcessingHandler implements Runnable {
    private final Socket socket;

    public AuditProcessingHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
        //try (InputStream is = new ByteArrayInputStream(serializedLog.getBytes(StandardCharsets.UTF_8))){
            InternalLog.Log("Processing log");
            JAXBContext context = JAXBContext.newInstance(LogType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<LogType> logElem = (JAXBElement<LogType>)unmarshaller.unmarshal(socket.getInputStream());
            Logger.getInstance().Log(logElem.getValue());
            Logger.getInstance().SaveLog();
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }
}
