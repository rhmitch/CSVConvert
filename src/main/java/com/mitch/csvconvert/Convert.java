package com.mitch.csvconvert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * Convert an unknown csv format into a known csv format todo - command line
 * parameters
 */
public class Convert {

    public static void main(String[] args) {
        CSVFormat reqOutputFormat = CSVFormat.DEFAULT;
        boolean delfile = false;
        if (args.length < 1) {
            System.err.println("No parameters present. Must pass a filename.");
            System.exit(1);
        }
        if (args.length > 1) {
            String sdelfile = args[1];
            if (sdelfile.equalsIgnoreCase("true")) {
                delfile = true;
            }
        }
        if (args.length > 2) {
            String outfmt = args[2].toUpperCase();
            switch (outfmt) {
                case "DEFAULT":
                    reqOutputFormat = CSVFormat.DEFAULT;
                    break;
                case "EXCEL":
                    reqOutputFormat = CSVFormat.EXCEL;
                    break;
                case "INFORMIX_UNLOAD":
                    reqOutputFormat = CSVFormat.INFORMIX_UNLOAD;
                    break;
                case "INFORMIX_UNLOAD_CSV":
                    reqOutputFormat = CSVFormat.INFORMIX_UNLOAD_CSV;
                    break;
                case "MYSQL":
                    reqOutputFormat = CSVFormat.MYSQL;
                    break;
                case "RFC4180":
                    reqOutputFormat = CSVFormat.RFC4180;
                    break;
                case "ORACLE":
                    reqOutputFormat = CSVFormat.ORACLE;
                    break;
                case "POSTGRESQL_CSV":
                    reqOutputFormat = CSVFormat.POSTGRESQL_CSV;
                    break;
                case "POSTGRESQL_TEXT":
                    reqOutputFormat = CSVFormat.POSTGRESQL_TEXT;
                    break;
                case "POSTGRESQL_TDF":
                    reqOutputFormat = CSVFormat.TDF;
                    break;
                default:
                    System.err.println("Requested format not valid: " + outfmt);
                    System.exit(1);
            }
        }
        File f = new File(args[0]);
        if (!f.canRead()) {
            System.err.println("Unable to read file: " + args[0]);
            System.exit(2);
        }
        boolean renameTo = f.renameTo(new File(f.getAbsolutePath() + ".original"));
        if (!renameTo) {
            System.err.println("Unable to rename original file.");
            System.exit(3);
        } else {
            f = new File(f.getAbsolutePath() + ".original");
        }
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(f.getAbsolutePath()));
            BufferedWriter csvWriter = new BufferedWriter(new FileWriter(args[0]));
            CSVPrinter csvPrinter = new CSVPrinter(csvWriter, reqOutputFormat);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            for (CSVRecord record : csvParser) {
                for (String field : record) {
                    csvPrinter.print(field);
                }
                csvPrinter.println();
            }
            reader.close();
            csvPrinter.close();
            boolean deleted = false;
            if (delfile) {
                deleted = f.delete();
                if (!deleted) {
                    System.err.println("Unable to delete input file.");
                    System.exit(4);
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}