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
        if (args.length < 3) {
            System.out.println("Usage: java -jar <metadata_file> <data_file> <output_path>");
            System.exit(1);
        }

        CsvStandaloneTransaction csvStandaloneTransaction = new CsvStandaloneTransaction(args[0], args[1], args[2]);

        try {
            csvStandaloneTransaction.process();
        } catch (Exception e) {
            System.err.println("Error while processing CSV data: " + e.getMessage());
            System.exit(1);
        }
    }
}
