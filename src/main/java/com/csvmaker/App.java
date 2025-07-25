package com.csvmaker;

import com.csvmaker.core.CsvStandaloneTransaction;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        if (args.length < 2) {
            System.out.println("Usage: java -jar <metadata_file> <data_file>");
            System.exit(1);
        }

        CsvStandaloneTransaction csvStandaloneTransaction = new CsvStandaloneTransaction(args[0], args[1]);

        try {
            csvStandaloneTransaction.process();
        } catch (Exception e) {
            System.err.println("Error while processing CSV data: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
