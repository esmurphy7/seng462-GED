package com.teamged.logging;

import com.teamged.auditserver.AuditMain;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Evan on 2/3/2016.
 * Modified by DanielF on 4/3/2016.
 */
public class Logger
{
    private static final String OUTPUT_LOGFILE = "outputLog.xml";
    private static final String XML_OPEN = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<log>";
    private static final String XML_CLOSE = "</log>\n";

    /**
     * Save the current log queue to a file on disk.
     */
    public static void SaveLog() {
        URL url = Logger.class.getResource("");
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
}
