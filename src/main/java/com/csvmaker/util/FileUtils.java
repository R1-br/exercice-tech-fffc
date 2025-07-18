package com.csvmaker.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static final Charset INPUT_ENCODING = StandardCharsets.UTF_8;
    private static final Charset OUTPUT_ENCODING = StandardCharsets.UTF_8;
    private static final String OUTPUT_EXTENSION = ".csv";

    /**
     * Get a reader for the given file.
     * @param filePath the file path
     * @return a BufferedReader for the given file
     */
    public static BufferedReader getReader(String filePath) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(filePath), INPUT_ENCODING);
            return new BufferedReader(inputStreamReader);
        } catch (FileNotFoundException _) {
            System.err.println("MetaData File not found: " + filePath);
            System.exit(1);
        }

        return null;
    }

    /**
     * Get a writer for the given file. (create the file if it doesn't exist)
     * @param filePath the file path
     * @return a BufferedWriter for the given file
     */
    public static BufferedWriter getWriter(String filePath) {
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
            System.err.println("Failed to create Output operation: " + filePath);
            System.exit(1);
        }
        return null;
    }
}
