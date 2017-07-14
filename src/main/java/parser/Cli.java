package parser;

import org.apache.commons.cli.*;
import processor.Extactor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by chiranz on 7/14/17.
 */
public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class.getName());

    private String[] args = null;
    private Options options = new Options();

    public Cli(String[] args) {

        this.args = args;

        options.addOption("h", "help", false, "show help.");
        options.addOption("p", "directory", true, "Directory Path");
        options.addOption("q", "query", true, "css query");

    }

    public void parse() {
        CommandLineParser parser = new BasicParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();

            if (cmd.hasOption("q") && cmd.hasOption("p")) {

                try {
                    String path = cmd.getOptionValue("p");
                    String query = cmd.getOptionValue("q");
                    new Extactor().getElements(path, query);
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.getMessage());
                    help();

                }
            } else {
                help();
                log.log(Level.SEVERE, "Missing p and q option");
            }

            if (cmd.hasOption("p")) {
                log.log(Level.INFO, "Using cli argument -p=" + cmd.getOptionValue("p"));
                // Whatever you want to do with the setting goes here
            } else {
                log.log(Level.SEVERE, "Missing p option");
                help();
            }


            if (cmd.hasOption("q")) {
                log.log(Level.INFO, "Using cli argument -q=" + cmd.getOptionValue("q"));
                // Whatever you want to do with the setting goes here
            } else {
                log.log(Level.SEVERE, "Missing q option");
                help();
            }


        } catch (ParseException e) {
            log.log(Level.SEVERE, "Failed to parse comand line properties", e.getMessage());
            help();
        }
    }

    private void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);
        System.exit(0);
    }
}
