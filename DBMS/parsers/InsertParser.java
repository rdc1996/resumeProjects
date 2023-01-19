package parsers;

import catalog.ACatalog;
import catalog.Catalog;
import common.Attribute;
import common.AttributeType;
import common.Table;
import storagemanager.AStorageManager;
import storagemanager.StorageManager;

import java.util.ArrayList;

public class InsertParser {
    /**
     * Checks to see if the SQL query contains the correct keywords and format
     * @param tokens the SQL query tokenized
     * @return true if all the keywords are correct
     *         false otherwise
     */
    private static boolean insertStatementErrorCheck(String[] tokens) {
        if(!tokens[0].toLowerCase().equals("insert")) {
            System.err.println("Expected insert statement to begin with insert keyword");
            return false;
        }

        if(!tokens[1].toLowerCase().equals("into")) {
            System.err.println("Expected into keyword");
            return false;
        }

        if(!tokens[3].toLowerCase().equals("values")) {
            System.err.println("Expected values keyword");
            return false;
        }

        return true;
    }

    /**
     * Obtains a list of primary keys that are used in this table
     * @param storageManager the storage manager used in this session
     * @param table the table to obtain a list of primary keys from
     * @return a list of primary keys for a specified table
     */
    private static ArrayList<Object> listOfPrimaryKeys (StorageManager storageManager, Table table) {
        int pkIdx = table.getPrimaryKeyIndex();
        ArrayList<Object> primaryKeys = new ArrayList<>();
        ArrayList<ArrayList<Object>> records = storageManager.getRecords(table);
        for (ArrayList<Object> record : records) {
            primaryKeys.add(record.get(pkIdx));
        }
        return primaryKeys;
    }

    /**
     * Parses the insert statement and queries the statement properly with the constraints
     * @param statement the statement to parse and query
     * @return true if the SQL query parses correctly
     *         false otherwise
     */
    public static boolean insertStatement (String statement) {
        // Obtains the catalog
        Catalog catalog = (Catalog) ACatalog.getCatalog();
        // Obtains the storage manager
        StorageManager storageManager = (StorageManager) AStorageManager.getStorageManager();

        // Tokenizes the statement
        String[] tokens = statement.replace("\n", "").replace(";", "").split(" ", 5);

        // Checks to see if the tokens have the correct keywords
        if(!insertStatementErrorCheck(tokens)) return false;

        String tableName = tokens[2];
        // If the table exists
        if (catalog.containsTable(tableName)) {
            //Get the table, primary key index, list of non-null constraints
            Table table = (Table) catalog.getTable(tableName);
            int pkIdx = table.getPrimaryKeyIndex();
            ArrayList<Attribute> nonNulls = table.getNonNulls();
            ArrayList<Attribute> tableAttributes = table.getAttributes();
            String[] recordsToInsert = tokens[4].split(",(?![^()]*\\))");
            // For each record to parse in the query statement
            for (String record : recordsToInsert) {

                String strippedRecord = record.strip();
                String parenthesisRemovedStr = strippedRecord.substring(1, strippedRecord.length() - 1);
                String[] parenthesisRemoved = parenthesisRemovedStr.split(",[ \t\n]*");

                // Gets a list of the primary keys currently in the table
                ArrayList<Object> pkValues = listOfPrimaryKeys(storageManager, table);
                ArrayList<Object> columns = new ArrayList<>();
                // For each attribute in the table
                for (int attr = 0; attr < tableAttributes.size(); attr++) {
                    // Get the attribute
                    AttributeType type = AttributeType.toAttributeType(tableAttributes.get(attr));
                    Object value;
                    switch (type) {
                        // If the attribute is an Integer, it parses an int and add it to columns
                        case Integer:
                            try {
                                value = Integer.parseInt(parenthesisRemoved[attr]);
                                if (pkIdx == attr && !pkValues.contains(value)) {
                                    // If it is a primary key and is not already used, add it to the columns
                                    columns.add(value);
                                } else if (pkIdx == attr && pkValues.contains(value)){
                                    // If it is a primary key and is already used, throw an error
                                    System.err.println(value + " is a primary key and is already in the table.");
                                    return false;
                                } else {
                                    // Add the value to the columns
                                    columns.add(value);
                                }
                            } catch (Exception e){
                                if (pkIdx == attr) {
                                    // If it is a primary key and is null, throw an error
                                    System.err.println(type + " is a primary key and therefore cannot be null.");
                                    return false;
                                } else if (nonNulls.contains(tableAttributes.get(attr))) {
                                    // If the attribute contains a not null constraint, throw an error
                                    System.err.println(tableAttributes.get(attr).getAttributeName() + " cannot be null.");
                                    return false;
                                } else {
                                    // Add a null to the columns
                                    columns.add(null);
                                }
                            }
                            break;
                        // If the attribute is a Double, it parses a double and add it to columns
                        case Double:
                            try {
                                value = Double.parseDouble(parenthesisRemoved[attr]);
                                if (pkIdx == attr && !pkValues.contains(value)) {
                                    // If it is a primary key and is not already used, add it to the columns
                                    columns.add(value);
                                } else if (pkIdx == attr && pkValues.contains(value)){
                                    // If it is a primary key and is already used, throw an error
                                    System.err.println(value + " is a primary key and is already in the table.");
                                    return false;
                                } else {
                                    // Add the value to the columns
                                    columns.add(value);
                                }
                            } catch (Exception e){
                                if (pkIdx == attr) {
                                    // If it is a primary key and is null, throw an error
                                    System.err.println(type + " is a primary key and therefore cannot be null.");
                                    return false;
                                } else if (nonNulls.contains(tableAttributes.get(attr))) {
                                    // If the attribute contains a not null constraint, throw an error
                                    System.err.println(tableAttributes.get(attr).getAttributeName() + " cannot be null.");
                                    return false;
                                } else {
                                    // Add a null to the columns
                                    columns.add(null);
                                }
                            }
                            break;
                        // If the attribute is a Boolean, it parses a boolean and add it to columns
                        case Boolean:
                            if (parenthesisRemoved[attr].equals("null")) {
                                // If the token is a "null", then value is set to null
                                value = null;
                            } else {
                                // Parse a boolean
                                value = Boolean.parseBoolean(parenthesisRemoved[attr]);
                            }
                            if (pkIdx == attr && !pkValues.contains(value)) {
                                // If it is a primary key and is not already used, add it to the columns
                                columns.add(value);
                            } else if (pkIdx == attr && pkValues.contains(value)){
                                // If it is a primary key and is already used, throw an error
                                System.err.println(value + " is a primary key and is already in the table.");
                                return false;
                            } else if (nonNulls.contains(tableAttributes.get(attr)) && value == null) {
                                // If the attribute contains a not null constraint and the value is null, throw an error
                                System.err.println(tableAttributes.get(attr).getAttributeName() + " cannot be null.");
                                return false;
                            } else if (pkIdx == attr && value == null) {
                                // If it is a primary key and the value is null, throw an error
                                System.err.println(type + " is a primary key and therefore cannot be null.");
                                return false;
                            } else {
                                // Add the value the columns
                                columns.add(value);
                            }
                            break;
                        // If the attribute is a Varchar, it parses chars and add it to columns
                        case Varchar:
                        // If the attribute is a Char, it parses chars and add it to columns
                        case Char:
                            // Parses the token as a string
                            value = parenthesisRemoved[attr];
                            if (value.equals("null")) {
                                // If the token is "null", then set value to null
                                value = null;
                            }
                            if (value != null && type.equals(AttributeType.Varchar)) {
                                String attrName = tableAttributes.get(attr).attributeType();
                                int lengthVarChar = Integer.parseInt(attrName.toString().replaceAll("[^\\d]", ""));
                                if (value.toString().length() -2 > lengthVarChar) {
                                    System.err.println(value + " has a length longer than " + lengthVarChar);
                                    return false;
                                }
                            }

                            if (pkIdx == attr && !pkValues.contains(value)) {
                                // If it is a primary key and is not already used, add it to the columns
                                columns.add(value);
                            } else if (pkIdx == attr && pkValues.contains(value)){
                                // If it is a primary key and is already used, throw an error
                                System.err.println(value + " is a primary key and is already in the table.");
                                return false;
                            } else if (pkIdx == attr && value == null) {
                                // If it is a primary key and the value is null, throw an error
                                System.err.println(type + " is a primary key and therefore cannot be null.");
                                return false;
                            } else if (nonNulls.contains(tableAttributes.get(attr)) && value == null) {
                                // If the attribute contains a not null constraint and the value is null, throw an error
                                System.err.println(tableAttributes.get(attr).getAttributeName() + " cannot be null.");
                                return false;
                            } else {
                                // Add the value the columns
                                columns.add(value);
                            }
                            break;
                        // If the attribute is not an existing, valid attribute, throw an error
                        default:
                            System.err.println(parenthesisRemoved[attr] + " is an invalid type for attr " + type +
                                    " in table" + tableName);
                            return false;
                    }
                }
                // If tokenized tuple parses through the entire list of attributes successfully, add it to the table
                if (tableAttributes.size() == columns.size()) {
                    storageManager.insertRecord(table, columns);
                }
            }
            // If all the tokenized tuples parses successfully, return true
            return true;
        } else {
            // If the table does not exists, throw an error and return false
            System.err.println("Table " + tableName + " does not exist in the Catalog.");
            return false;
        }
    }

    /**
     * It's a tester
     * @param args
     */
    public static void main(String[] args) {
        ACatalog.createCatalog("/c/Users/MyPc/Desktop/testing", 500, 10);
        AStorageManager.createStorageManager();
        ACatalog catalog = ACatalog.getCatalog();
        AStorageManager storageManager = AStorageManager.getStorageManager();
        String testing = "insert into foo values (1, \"foo\", true, 2.1),\n" +
                "(3, \"baz\", true, 4.14),\n" +
                "(2, \"bar\", false, 5.2),\n" +
                "(5, \"true\", true, 7.18);";
        ArrayList<Attribute> attr = new ArrayList<>();
        Attribute pk = new Attribute("int", "Integer");
        attr.add(pk);
        attr.add(new Attribute("varchar13", "Varchar(13)"));
        Attribute notNull = new Attribute("double", "Double");
        Attribute notNull2 = new Attribute("boolean", "Boolean");
        attr.add(notNull2);
        attr.add(notNull);
        catalog.addTable("foo", attr, pk);
        Table table = (Table) catalog.getTable("foo");
        table.addNonNullAttribute(notNull);
        table.addNonNullAttribute(notNull2);
        System.out.println("Attempting to insert values into table with the query: ");
        System.out.println(testing);
        insertStatement(testing);
        System.out.println(storageManager.getRecords(table));
        String testing2 = "insert into bar values (1, \"foo\", true, 2.1);";
        System.out.println("Attempting to insert values into a invalid table with the query: ");
        System.out.println(testing2);
        insertStatement(testing2);
        System.out.println(storageManager.getRecords(table));
        String testing3 = "insert into foo values (1, \"invalid\", false, 1.22);";
        System.out.println("Attempting to insert existing primary key value into the table with the query: ");
        System.out.println(testing3);
        insertStatement(testing3);
        System.out.println(storageManager.getRecords(table));
        String testing4 = "insert into foo values (6, \"check\", false, null);";
        System.out.println("Attempting to insert record with null in not null attribute with the query: ");
        System.out.println(testing4);
        insertStatement(testing4);
        System.out.println(storageManager.getRecords(table));
        String testing5 = "insert into foo values (7, \"check\", null, 3.33);";
        System.out.println("Attempting to insert record with null in not null attribute with the query: ");
        System.out.println(testing5);
        insertStatement(testing5);
        System.out.println(storageManager.getRecords(table));
        String testing6 = "insert into foo values (7, \"photosynthesis\", null, 3.33);";
        System.out.println("Attempting to insert record with varchar longer than what's allowed: ");
        System.out.println(testing6);
        insertStatement(testing6);
    }
}
