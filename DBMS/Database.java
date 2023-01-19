import catalog.ACatalog;
import common.Attribute;
import parsers.DDLParser;
import parsers.DMLParser;
import parsers.ResultSet;
import storagemanager.AStorageManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

/*
    This is the main driver class for the database.

    It is responsible for starting and running the database.

    Other than in the provided testers this will be the only class to contain a main.

    You will add functionality to this class during the different phases.

    More details in the writeup.
 */
public class Database {

    // The list of DDL keywords for checking incoming statements
    private final static ArrayList<String> ddlStrings = new ArrayList<>(Arrays.asList("create", "drop", "alter"));

    // The list of DML keywords for checking incoming statements
    private final static ArrayList<String> dmlStrings = new ArrayList<>(Arrays.asList("insert", "update", "delete",
            "select"));

    // The storage manager
    private static AStorageManager sm;

    /**
     * Used when executing database statements that do not return anything
     *
     * @param stmt the statement to be sent to DDL/DML parser
     * @return true if executed, false otherwise
     */
    public static boolean executeStatement(String stmt) throws InterruptedException {
        String[] listStmt = stmt.split("\\s");
        if (listStmt.length < 2) {
            return false;
        }
        String keyword = listStmt[0].toLowerCase();
        if (dmlStrings.contains(keyword)) {
            return DMLParser.parseDMLStatement(stmt);
        }
        if (ddlStrings.contains(keyword)) {
            return DDLParser.parseDDLStatement(stmt);
        }
        System.err.println("Statement is missing keyword");
        Thread.sleep(50);
        return false;
    }


    /**
     * Used when executing database queries that return tables of data.
     *
     * @param query the query to be executed
     * @return a table of the data specified by the query
     */
    public static ResultSet executeQuery(String query) throws InterruptedException {
        String[] listQuery = query.split("\\s");
        if (listQuery.length < 4) {
            return null;
        }
        if (!dmlStrings.contains(listQuery[0].toLowerCase())) {
            System.err.println("Query is missing keyword");
            Thread.sleep(50);
            return null;
        }
        return DMLParser.parseDMLQuery(query);
    }


    /**
     * Check if the given string is a query or not.
     * Returns 2 if the string is empty
     * Returns 1 if it is a query
     * Returns 0 if it is a statement
     *
     * @param query the string being checked
     * @return true if query, false if statement
     */
    public static int isQuery(String query) {
        if (Objects.equals(query, " ") || Objects.equals(query, "")) {
            return 2;
        }
        String[] listQuery = query.split("\\s");
        if (listQuery[0].equalsIgnoreCase("select")) {
            return 1;
        }
        return 0;
    }


    /**
     * Get the length of the attribute name and type;
     *
     * @param attr the attribute given
     * @return the length of the attribute name and type
     */
    public static int lengthOfAttrNameAndType(Object attr) {
        Attribute tempAttr = (Attribute) attr;
        String name = tempAttr.getAttributeName();
        String type = tempAttr.getAttributeType();
        return name.length() + type.length() + 1;
    }


    /**
     * Add spaces to the front of the string provided.
     *
     * @param str the string to add spaces to
     * @param numSpaces the number of spaces to add
     * @return the string with spaces in front
     */
    public static String addSpacesToFront(String str, int numSpaces) {
        return " ".repeat(Math.max(0, numSpaces)) + str;
    }


    /**
     * Add spaces to the back of the string
     *
     * @param str the string to add spaces to
     * @param numSpaces the number of spaces to add
     * @return the new string with spaces
     */
    public static String addSpacesToBack(String str, int numSpaces) {
        return str + " ".repeat(Math.max(0,numSpaces));
    }


    /**
     * Pretty print the given result set
     *
     * @param rs the result set to be printed
     */
    public static void prettyPrintDisBih(ResultSet rs) {
        System.out.print("Attributes:");
        for (Object value : rs.attrs()) {
            System.out.print("        " + value);
        }
        System.out.println(" ");
        System.out.println(" ");
        ArrayList<ArrayList<Object>> results = rs.results();
        int recordCount = 1;
        for (ArrayList<Object> currRow : results) {
            String recordString = "Record " + recordCount + ":";
            int numSpacesMinus = 0;
            if (recordString.length() > 10) {
                numSpacesMinus = recordString.length() - 10;
            }
            else {
                recordString = addSpacesToBack(recordString, 10 - recordString.length());
            }
            System.out.print(recordString);
            int i = 0;
            for (Object item: currRow) {
                Attribute currAtr = rs.attrs().get(i);
                int lengthOfAttr = lengthOfAttrNameAndType(currAtr);
                String tempString;
                int numSpaces = (int)(lengthOfAttr - Math.ceil(item.toString().length()));
                int numAddSpaces = 8 - numSpacesMinus;
                int numSpacesAfter = 0;
                if (numSpaces > 0) {
                    int tempNum = (int) Math.ceil((double)numSpaces / 2);
                    numAddSpaces += tempNum;
                    numSpacesAfter = numSpaces - tempNum;
                }
                if (currAtr.attributeType().equals("double")) {
                    double scale = Math.pow(10, 3);
                    double newValue = Math.round((double)item * scale) / scale;
                    tempString = String.valueOf(newValue);
                    numSpaces = (int)(lengthOfAttr - Math.ceil(tempString.length()));
                    int tempNum = (int) Math.ceil((double)numSpaces / 2);
                    numAddSpaces += tempNum;
                    numSpacesAfter = numSpaces - tempNum;
                    System.out.print(addSpacesToFront(tempString, numAddSpaces));
                    System.out.print(addSpacesToFront("", numSpacesAfter));
                    i++;
                    continue;
                }
                System.out.print(addSpacesToFront(item.toString(), numAddSpaces));
                System.out.print(addSpacesToFront("", numSpacesAfter));
                i++;
            }
            System.out.println(" ");
            recordCount++;
        }
        System.out.println(" ");
        System.out.println("Total entries: " + results.size());
    }


    /**
     * Safely shutdown the database. Store all data that is needed upon restart to physical hardware.
     *
     * @return true if safely shut down, false otherwise
     */
    public static boolean terminateDatabase(){
        ACatalog catalog = ACatalog.getCatalog();
        sm.purgePageBuffer();
        return catalog.saveToDisk();
    }

    // Main function for database class
    public static void main(String[] args) throws InterruptedException {

        // Make sure that there are exactly 3 arguments passed in to the database class
        if (args.length != 3) {
            System.err.println("java Database <db_loc> <page_size> <buffer_size>");
            return;
        }

        // Grab all the arguments and assign them their necessary functions
        String databaseLocation = args[0];
        int pageSize = Integer.parseInt(args[1]);
        int bufferSize = Integer.parseInt(args[2]);

        // Create a new catalog or grab the old one
        ACatalog.createCatalog(databaseLocation, pageSize, bufferSize);
        sm = AStorageManager.createStorageManager();

        // loop through user input and send it to execute statement
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("input: ");
            StringBuilder statement = new StringBuilder(sc.nextLine());
            if (Objects.equals(statement.toString(), "quit")) {
                break;
            }
            while (true) {
                char lastChar = ' ';
                if (!statement.toString().equals(" ")) {
                    String[] tempString = statement.toString().split("\\s");
                    if (!(tempString.length < 1)) {
                        String lastWord = tempString[tempString.length - 1];
                        lastChar = lastWord.charAt(lastWord.length() - 1);
                    }
                }
                if (lastChar == ';') {
                    break;
                }
                statement.append(sc.nextLine());
            }
            String finalStatement = statement.toString();
            boolean result;
            ResultSet queryResult;
            int possibleQuery = isQuery(finalStatement);
            if (possibleQuery == 1) {
                queryResult = executeQuery(finalStatement);
                if (queryResult == null) {
                    System.err.println("ERROR: ResultSet null, you did something wrong");
                    Thread.sleep(50);
                    continue;
                }
                prettyPrintDisBih(queryResult);
            }
            else if (possibleQuery == 2) {
                System.err.println("ERROR: Statement is empty");
                Thread.sleep(50);
            }
            else {
                result = executeStatement(finalStatement);
                if (result) {
                    System.out.println("SUCCESS");
                }
                else {
                    System.out.println("ERROR");
                }
            }
        }
        if (terminateDatabase()) {
            System.out.println("Database shutdown correctly.");
        }
        else {
            System.err.println("There was a problem shutting down the database.");
        }
    }
}
