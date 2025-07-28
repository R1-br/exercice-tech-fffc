package com.fffc.csvmaker.common.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class StringUtils {
    private static final String DOUBLE_QUOTES = "\"";

    private StringUtils() {}

    /**
     * Returns the index of the first non-numerical character in the input line
     * or last character of the input line if no non-numerical character is found.
     * @param inputLine the input line
     * @return the index of the first non-numerical character or last character
     */
    public static int getFirstNonNumericalIndex(String inputLine, int startIndex) {
        int index = startIndex;

        while (index < inputLine.length()
                && (Character.isDigit(inputLine.charAt(index)) || inputLine.charAt(index) == '.' || inputLine.charAt(index) == '-')) {
            index++;
        }

        return index;
    }
    public static int getFirstNonNumericalIndex(String inputLine) {
        return getFirstNonNumericalIndex(inputLine, 0);
    }

    public static int getFirstNumericalIndex(String inputLine, int startIndex) {
        int index = startIndex;

        while (index < inputLine.length()
                && !Character.isDigit(inputLine.charAt(index)) && inputLine.charAt(index) != '.' && inputLine.charAt(index)!= '-') {
            index++;
        }

        return index;
    }
    public static int getFirstNumericalIndex(String inputLine) {
        return getFirstNumericalIndex(inputLine, 0);
    }

    /**
     * Returns the input string quoted with double quotes.
     * @param input the input string
     * @return the quoted string
     */
    public static String quoteString(String input) {
        return DOUBLE_QUOTES + input + DOUBLE_QUOTES;
    }

    /**
     * Encodes input string to the given charset.
     * @param input the input string
     * @param charset the charset to convert input to
     * @return the input string encoded in charset
     */
    public static String encodeString(String input, Charset charset) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return new String(input.getBytes(charset), charset);
    }
}
