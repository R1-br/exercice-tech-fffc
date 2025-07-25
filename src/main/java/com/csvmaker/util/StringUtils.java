package com.csvmaker.util;

public class StringUtils {
    private static final String DOUBLE_QUOTES = "\"";

    private StringUtils() {}

    /**
     * Returns the index of the first non-numerical character in the input line
     * or last character of the input line if no non-numerical character is found.
     * @param inputLine the input line
     * @return the index of the first non-numerical character or last character
     */
    public static int getFirstNonNumericalIndex(String inputLine) {
        int index = 0;

        while (index < inputLine.length()
                && (Character.isDigit(inputLine.charAt(index)) || inputLine.charAt(index) == '.' || inputLine.charAt(index) == '-')) {
            index++;
        }

        return index;
    }

    public static String quoteString(String input) {
        return DOUBLE_QUOTES + input + DOUBLE_QUOTES;
    }
}
