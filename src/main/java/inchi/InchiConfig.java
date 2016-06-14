package inchi;

import org.apache.commons.cli.*;

public class InchiConfig {

    public static final String URL_DB = "url-db";
    public static final String URL_DICT = "url-dict";

    public static final String FILE_DB = "file-db";
    public static final String FILE_DICT = "file-dict";

    public static final String DEF_URL_DB = "http://ftp.ebi.ac.uk/pub/databases/chembl/ChEMBLdb/latest/chembl_21_chemreps.txt.gz";
    public static final String DEF_URL_DICT = "https://raw.githubusercontent.com/jonbcard/scrabble-bot/master/src/dictionary.txt";

    public int numberOfLines = 10;
    public String dbUrl;
    public String dictUrl;

    public boolean isHttpDbUrl;
    public boolean isHttpDictUrl;
    public boolean isReloadDb = false;

    public InchiConfig() {
    }

    public void printCurrentSettings() {
        System.out.println("Type of source for db: " + (isHttpDbUrl ? "web": "file"));
        System.out.println("Db address: " + dbUrl);
        System.out.println("Reload db: " + isReloadDb);

        System.out.println("Type of source for dictionary: " + (isHttpDictUrl ? "web": "file"));
        System.out.println("Dictionary address: " + dictUrl);
        System.out.println("Number of lines: " + numberOfLines);
    }
    public boolean init(String args[]) {
        Options options = new Options();

        // add t option
        options.addOption("h", "help", true, "print this help");

        options.addOption("udb", URL_DB, true, "url for Inchi database file (has higher priority than 'file-db' option)");
        options.addOption("ud", URL_DICT, true, "url for Inchi dictionary file (has higher priority than 'file-dict' option)");
        options.addOption("rl", "reload-db-from-url", true, "true/false, reload file from given url (local copy will be overwritten).");

        options.addOption("fdb", FILE_DB, true, "file for Inchi database file");
        options.addOption("fd", FILE_DICT, true, "file for Inchi dictionary file");
        options.addOption("n", true, "number of lines to find. The value has to be greater than 0. Default value 10");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
            // parse the command line arguments
            commandLine = parser.parse( options, args );
        }
        catch( ParseException exp ) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            printHelpOnCli(options);
            return false;
        }

        if (commandLine.hasOption( "h" )) {
            printHelpOnCli(options);
            return false;
        }

        if (!initNumberOfLines(commandLine)) {
            printHelpOnCli(options);
            return false;
        }

        if (!initDb(commandLine)) {
            printHelpOnCli(options);
            return false;
        }

        if (!initDictionary(commandLine)) {
            printHelpOnCli(options);
            return false;
        }


        return true;
    }

    private boolean initDictionary(CommandLine commandLine) {
        this.dictUrl = commandLine.getOptionValue("ud" );

        if (this.dictUrl==null) {
            this.dictUrl = commandLine.getOptionValue("fd" );
            if (this.dictUrl!=null)  {
                this.isHttpDictUrl = false;
            }
            else {
                this.isHttpDictUrl = true;
                this.dictUrl = DEF_URL_DICT;
            }
        }
        return true;
    }

    private boolean initDb(CommandLine commandLine) {
        this.dbUrl = commandLine.getOptionValue("udb" );

        if (this.dbUrl==null) {
            this.dbUrl = commandLine.getOptionValue("fdb" );
            if (this.dbUrl!=null)  {
                this.isHttpDbUrl = false;
            }
            else {
                this.isHttpDbUrl = true;
                this.dbUrl = DEF_URL_DB;
            }
        }
        else {
            this.isHttpDbUrl = true;
        }

        String s = commandLine.getOptionValue("rl", "false");
        if (s!=null) {
            isReloadDb = Boolean.parseBoolean(s);
        }
        return true;
    }

    private boolean initNumberOfLines(CommandLine commandLine) {
        Integer nols = null;
        if (commandLine.hasOption( "n" )) {
            String n = commandLine.getOptionValue("n" );
            try {
                nols = Integer.parseInt(n);
            } catch (NumberFormatException e) {
                System.out.println("wrong value for param 'n' - "+n+". Must be digit. Default value will be used.");
                return true;
            }
        }

        if (nols==null) {
            String[] params = commandLine.getArgs();
            if (params==null || params.length==0) {
                return true;
            }
            try {
                nols = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                System.out.println("wrong value for param 'n' - "+params[0]+". Must be digit. Default value will be used.");
                return true;
            }
        }
        if (nols < 1) {
            System.out.println("number of lines must be greater that 0. Default value will be used.");
            return true;
        }
        this.numberOfLines = nols;
        return true;
    }

    private static void printHelpOnCli(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "Inchi [OPTIONS] [NUMBER_OF_LINES]", options );
    }
}
