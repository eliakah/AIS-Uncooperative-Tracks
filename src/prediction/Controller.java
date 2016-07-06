package prediction;

import fasade.AisDatabaseFasade;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.IllegalFormatException;

/**
 * //The main will accept  4 arguments, which are the CSV filename
 * containing AIS data set, MMSI#, Time since last known AIS signal
 * , and Date of last AIS signal.
 */
public class Controller {
    static AisDatabaseFasade database = new AisDatabaseFasade();
    static String csv;
    static String mmsi;
    static String time;
    static String date;
    static float maxTurn = 180f;
    static CSVParser parse;

    /**
     * Creates all needed modules (AreaPredictor, KMLGenerator) and gives
     * each module the needed variables.
     *
     * Runs execute() function  at the end which handles methods
     * called within modules.
     *
     * @param args CSV Filename, MMSI, Time, Date, Max Turn Radius
     * @throws SQLException
     * @throws IOException
     * @throws CSVParserException
     */
    public static void main(String[] args) throws SQLException, IOException, CSVParserException {
        if(database.createTable() != true)
        {
            System.err.println("Does not compute");
            System.exit(1);
        }

        parseArgs(args);
        parse = new CSVParser(new File(csv));
        parse.iterateCsv();

        //initiating modules, also pass the database connection to them
        // Area Prediction Algorithm takes db, ship MMSI number, and time after signal loss
        AreaPredictor areaPredict = new AreaPredictor(mmsi, date, time, maxTurn);

        //to grab xml file
        KMLGenerator kmlGen = new KMLGenerator(mmsi);  // KML generator
        //File file = new File(“Insert Path to file here\AIS_DATA.xml”); // sets the file as xml file

        // executes the methods needed
        execute(areaPredict, kmlGen);

    }

    /**
     * Will check if all of the inputs are in proper format and will
     * return an error if any arguements are not in correct format
     *
     * @param args CSV Filename, MMSI, Time, Date
     * @return check
     */
    public static boolean parseArgs(String args[]) {
        if (args.length >= 4) {
            try {
                csv = args[0];
            } catch (IllegalFormatException s) {
                System.err.println("IllegalFormatException: Please enter a Filename");
                System.exit(1);
            }

            try {
                mmsi = args[1];
            } catch (NumberFormatException n) {
                System.err.println("NumberFormatException: Please enter an MMSI  number");
                System.exit(1);
            }

            try {
                date = args[2];
            } catch (IllegalFormatException t) {
                System.err.println("IllegalFormatException: Please enter Date");
                System.exit(1);
            }

            try {
                time = args[3];
            } catch (IllegalFormatException t) {
                System.err.println("IllegalFormatException: Please enter Time");
                System.exit(1);
            }
            if(args.length == 5)
            {
                try{
                    maxTurn = Float.parseFloat(args[4]);
                }
                catch (IllegalFormatException t) {
                    System.err.println("IllegalFormatException: Please enter a maximum turn radius for the vessel. Default is automatically set to 180 degrees.");
                    System.exit(1);
                }
            }
        }
        else {
            System.err.println("Invalid Number of Arguements.");
            System.err.println("Please Enter in the following order:");
            System.err.println("CSV Filename, MMSI Number, Date, Time, Max Turn");
            System.exit(1);
        }
        System.out.println("CSV entered: " + csv);
        System.out.println("MMSI entered: " + mmsi);
        System.out.println("Date entered: " + date);
        System.out.println("Time entered: " + time);
        System.out.println("Current Max Turn: " + maxTurn);
        return true;
    }

    /**
     * This method will execute the needed methods from in their specific
     * order. If any of method returns false an error will be returned specifying
     * which module did not function correctly.
     *
     * @param algo Area predictor algorithm
     * @param kmlGen kml generating algorithm
     */
    private static void execute(AreaPredictor algo, KMLGenerator kmlGen) throws IOException, SQLException {
        algo.execute();
        kmlGen.pull();
        kmlGen.pullPath();
        kmlGen.pullPorts();
        kmlGen.generate();
    }
}