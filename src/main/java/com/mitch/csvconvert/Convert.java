package com.mitch.csvconvert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.cli.ParseException;

/**
 *
 * Convert an unknown csv format into a known csv format
 */
public class Convert {

    /**
     * Constants used by CLI framework for command line parameter
     */
    public static String INPUT = "input";
    public static String DELETE = "delete";
    public static String FORMAT = "format";

    //todo - add support for output filename
    private static Options generateOptions() {
        final Option inputOption = Option.builder("i")
                .required()
                .longOpt("input")
                .hasArg()
                .desc("Input CSV File")
                .build();
        final Option formatOption = Option.builder("f")
                .longOpt("format")
                .hasArg()
                .desc("Output format")
                .build();
        final Option deleteOption = Option.builder("d")
                .longOpt("delete")
                .desc("Delete original input file after conversion")
                .build();

        final Options options = new Options();
        options.addOption(inputOption);
        options.addOption(formatOption);
        options.addOption(deleteOption);
        return options;
    }

    private static CommandLine generateCommandLine(final Options options, final String[] commandLineArguments) {
        final CommandLineParser cmdLineParser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = cmdLineParser.parse(options, commandLineArguments);
        } catch (ParseException parseException) {
            System.err.println(
                    "ERROR: Unable to parse command-line arguments "
                    + Arrays.toString(commandLineArguments) + " due to:\n"
                    + parseException.getMessage());
            printHelp(options);
            System.exit(45);
        }
        return commandLine;
    }

    private static void printHelp(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        final String syntax = "java -jar CSVConvert.jar";
        final String usageHeader = "CSV Conversion";
        final String usageFooter = "\n\n2023 Mitch Morris";
        formatter.printHelp(syntax, usageHeader, options, usageFooter, true);
    }

    public static void main(String[] args) {
        Options opts = generateOptions(); //create the command line options in Apache CLI
        CommandLine cl = generateCommandLine(opts, args); //parse the command line parameters        
        //set this as a default in case the argument is not supplied
        CSVFormat reqOutputFormat = CSVFormat.DEFAULT;
        boolean delfile = false;
        if(cl.hasOption(DELETE))
        {
            delfile = true;
        }
        String outfmt = null;
        if (cl.hasOption(FORMAT)) {
            outfmt = cl.getOptionValue(FORMAT);
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
        File f = new File(cl.getOptionValue(INPUT));

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
            BufferedWriter csvWriter = new BufferedWriter(new FileWriter(cl.getOptionValue(INPUT)));
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
