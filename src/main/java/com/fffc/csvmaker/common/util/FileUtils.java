package com.fffc.csvmaker.common.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class FileUtils {
    public static final Charset INPUT_ENCODING = StandardCharsets.UTF_8;
    public static final Charset OUTPUT_ENCODING = StandardCharsets.UTF_8;
    private static final String OUTPUT_EXTENSION = ".csv";

    private static final String OUTPUT_FILE_DATE_FORMAT = "yyyyMMdd-HH";
    private static final String OUTPUT_FILE_TIME_FORMAT = "mm:ss";
    private static final String OUTPUT_FILE_PREFIX = "-fffc.csv";

    private FileUtils() {}
    
    /**
     * Get a reader for the given file.
     * @param filePath the file path
     * @return a BufferedReader for the given file
     */
    public static BufferedReader getReader(String filePath) throws FileNotFoundException {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(filePath), INPUT_ENCODING);
            
            return new BufferedReader(inputStreamReader);
        } catch (FileNotFoundException _) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
    }

    /**
     * Get a writer for the given file. (create the file if it doesn't exist)
     * @param filePath the file path
     * @return a BufferedWriter for the given file
     */
    public static BufferedWriter getWriter(String filePath) throws IOException {
        File file = new File(
                filePath.endsWith(OUTPUT_EXTENSION) ? filePath : filePath + OUTPUT_EXTENSION
        );
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), OUTPUT_ENCODING);
            
            return new BufferedWriter(outputStreamWriter);
        } catch (IOException _) {
            throw new IOException("Failed to create Output operation: " + filePath);
        }
    }

    public static String getOutputFilePath() {
        Date current = new Date();

        return new SimpleDateFormat(OUTPUT_FILE_DATE_FORMAT).format(current) + "h" +
                new SimpleDateFormat(OUTPUT_FILE_TIME_FORMAT).format(current) +
                OUTPUT_FILE_PREFIX;
    }
}
