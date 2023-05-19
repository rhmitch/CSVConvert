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
 * Convert an unknown csv format into a known csv format
 */
public class Convert {

    public static void main(String[] args) {
        boolean delfile = false;
        if (args.length > 1) {
            String sdelfile = args[1];
            if (sdelfile.equalsIgnoreCase("true")) {
                delfile = true;
            }
        }
        if (args.length < 1) {
            System.err.println("No parameters present. Must pass a filename.");
            System.exit(1);
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
            CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.RFC4180);
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