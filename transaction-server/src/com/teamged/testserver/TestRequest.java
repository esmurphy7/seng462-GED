package com.teamged.testserver;

import com.teamged.ServerConstants;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DanielF on 2016-01-30.
 */
public class TestRequest {
    private static final char[] CHARSET = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890".toCharArray();
    private static final ArrayList<String> COMMAND_MAP = new ArrayList<>(Arrays.asList(
            "NO_COMMAND",
            "ADD",
            "QUOTE",
            "BUY",
            "COMMIT_BUY",
            "CANCEL_BUY",
            "SELL",
            "COMMIT_SELL",
            "CANCEL_SELL",
            "SET_BUY_AMOUNT",
            "CANCEL_SET_BUY",
            "SET_BUY_TRIGGER",
            "SET_SELL_AMOUNT",
            "SET_SELL_TRIGGER",
            "CANCEL_SET_SELL",
            "__",   // both DUMPLOG have the same format, but only the 'root' version seems to be used, so ignore this one
            "DUMPLOG",
            "DISPLAY_SUMMARY"
    ));

    /**
     * This is a scrappy garbage testing file. Its uses are limited, but it does the job alright ...
     * @param args
     */
    public static void main(String[] args) {
        int count = 3;
        boolean showNum = false;
        boolean altRun = false;
        try {
            count = Integer.parseInt(args[0]);
            if (args.length > 1 && args[1].equals("--number")) {
                showNum = true;
            }
        } catch (Exception e) {
            if (args.length == 1 && args[0].equals("--workload")) {
                altRun = true;
            }
        }

        if (!altRun) {
            System.out.println("Running " + count + " test request(s)");

            for (int i = 0; i < count; i++) {
                try (
                        Socket s = new Socket(ServerConstants.TX_SERVERS[0], ServerConstants.PORT_RANGE[0]);
                        PrintWriter out = new PrintWriter(s.getOutputStream(), true)
                ) {
                    StringBuilder sb = new StringBuilder();
                    if (showNum) {
                        sb.append("[");
                        sb.append(i);
                        sb.append("],");
                    }
                    sb.append(2);
                    sb.append(",");
                    sb.append(randomString(CHARSET, 8));
                    sb.append(",");
                    sb.append(randomString(CHARSET, 3));
                    sb.append(",");
                    sb.append(Calendar.getInstance().getTimeInMillis());
                    sb.append(",0,0");
                    String request = sb.toString();
                    out.println(request);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } else {
            // "Workload generator" functionality (only for 1user)
            try (
                    BufferedReader br = new BufferedReader(new FileReader("1userWorkLoad.sdx"))
            ) {
                String nextLine;
                while ((nextLine = br.readLine().trim()) != null) {
                    String req = nextLine.replace(" ", ",") + "," + Calendar.getInstance().getTimeInMillis() + ",0,0";
                    Pattern re = Pattern.compile("^(\\[\\d+\\],)([A-Z_]+)(,.*)");
                    Matcher m = re.matcher(req);
                    if (m.matches() && m.groupCount() == 3) {
                        int command = COMMAND_MAP.indexOf(m.group(2));
                        if (command > 0) {
                            req = m.group(1) + command + m.group(3);
                            // Should probably handle this part in its own sort of try/catch/retry/break thing
                            Socket s = new Socket(ServerConstants.TX_SERVERS[0], ServerConstants.PORT_RANGE[0]);
                            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                            System.out.println(req);
                            out.println(req);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String randomString(char[] charSet, int length) {
        Random r = new SecureRandom();
        char[] res = new char[length];
        for (int i = 0; i < res.length; i++) {
            int idx = r.nextInt(charSet.length);
            res[i] = charSet[idx];
        }

        return new String(res);
    }

    public static int randomInt(int min, int max) {
        Random r = new SecureRandom();
        return r.nextInt(max+1) + min;
    }
}
