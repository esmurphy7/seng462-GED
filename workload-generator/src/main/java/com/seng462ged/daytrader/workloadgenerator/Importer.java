package com.seng462ged.daytrader.workloadgenerator;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Importer {

    public static List<Transaction> ImportTransactions(String filename) throws IOException {

        Map<String, String[]> commands = new HashMap<String, String[]>();
        commands.put("ADD", new String[]{"userId", "amount"});
        commands.put("QUOTE", new String[]{"userId", "stockSymbol"});
        commands.put("BUY", new String[]{"userId", "stockSymbol", "amount"});
        commands.put("COMMIT_BUY", new String[]{"userId"});
        commands.put("CANCEL_BUY", new String[]{"userId"});
        commands.put("SET_BUY_AMOUNT", new String[]{"userId", "stockSymbol", "amount"});
        commands.put("SET_BUY_TRIGGER", new String[]{"userId", "stockSymbol", "amount"});
        commands.put("CANCEL_SET_BUY", new String[]{"userId", "stockSymbol"});
        commands.put("SELL", new String[]{"userId", "stockSymbol", "amount"});
        commands.put("COMMIT_SELL", new String[]{"userId"});
        commands.put("CANCEL_SELL", new String[]{"userId"});
        commands.put("SET_SELL_AMOUNT", new String[]{"userId", "stockSymbol", "amount"});
        commands.put("SET_SELL_TRIGGER", new String[]{"userId", "stockSymbol", "amount"});
        commands.put("CANCEL_SET_SELL", new String[]{"userId", "stockSymbol"});
        commands.put("DUMPLOG", new String[]{"userId", "filename"});
        commands.put("DISPLAY_SUMMARY", new String[]{"userId"});

        List<Transaction> transactions = new ArrayList<Transaction>();

        InputStream inputStream = new FileInputStream(filename);

        // Put into buffered stream so that we can test if the file is gzipped
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        // Check if the file is gzipped
        if (isCompressed(bufferedInputStream)) {

            GZIPInputStream gzipInputStream = new GZIPInputStream(bufferedInputStream);
            bufferedInputStream = new BufferedInputStream(gzipInputStream);
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));

        try {

            String line = bufferedReader.readLine();

            while (line != null) {

                String pattern = "^\\[(\\d+)\\] (.*)$";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(line);

                if (m.find()) {

                    // Split the remaining parameters into an array
                    List<String> parameters = new ArrayList<String>(Arrays.asList(m.group(2).split(",")));

                    // The first parameter is the command
                    String command = parameters.remove(0);

                    if (commands.containsKey(command)) {

                        // Get the parameter mappings associated with the command
                        String[] parameterMappings = commands.get(command);

                        Transaction transaction = new Transaction();
                        transaction.setId(Integer.parseInt(m.group(1)));
                        transaction.setCommand(command);

                        for (int i = 0; i < parameters.size(); i++) {

                            String parameter = parameters.get(i).trim();

                            // Check the parameter mapping at the same index as our parameter
                            // Hack for DUMPLOG:
                            //   DUMPLOG can take either 1 or 2 parameters
                            //   If only 1 parameter passed, take the last parameter mapping
                            String parameterMapping = parameterMappings[parameterMappings.length > parameters.size() ? parameterMappings.length - 1 : i];

                            // Set the appropriate parameter in our transaction object
                            if (parameterMapping.equals("userId")) {
                                transaction.setUserId(parameter);
                            } else if (parameterMapping.equals("stockSymbol")) {
                                transaction.setStockSymbol(parameter);
                            } else if (parameterMapping.equals("amount")) {
                                transaction.setAmount(parameter);
                            } else if (parameterMapping.equals("filename")) {
                                transaction.setFilename(parameter);
                            }
                        }
                        transactions.add(transaction);
                    }
                }

                line = bufferedReader.readLine();
            }

            return transactions;

        } finally {
            bufferedReader.close();
        }
    }

    public static boolean isCompressed(BufferedInputStream bufferedInputStream) throws IOException {

        byte[] header = new byte[2];

        // Read the first two bytes
        bufferedInputStream.mark(2);
        bufferedInputStream.read(header, 0, 2);

        // Reset BufferedInputStream
        bufferedInputStream.reset();

        // Make sure the first two bytes exist
        if ((header == null) || (header.length < 2))
            return false;

        // Check if the first two bytes match the gzip header
        return ((header[0] == (byte)(GZIPInputStream.GZIP_MAGIC)) && (header[1] == (byte)(GZIPInputStream.GZIP_MAGIC >> 8)));
    }
}
