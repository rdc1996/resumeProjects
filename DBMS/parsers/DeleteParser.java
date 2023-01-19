package parsers;

import catalog.ACatalog;
import catalog.Catalog;
import common.Attribute;
import common.ForeignKey;
import common.Page;
import common.Record;
import common.Table;
import parsers.where.Node;
import parsers.where.Shunter;
import storagemanager.AStorageManager;
import storagemanager.StorageManager;

import java.util.ArrayList;
import java.util.Arrays;

public class DeleteParser {
    /**
     * Checks to see if the SQL query contains the correct keywords and format
     * @param tokens the SQL query tokenized
     * @param containsWhere true if there is a where clause
     *                      false otherwise
     * @return true if all the keywords are correct
     *         false otherwise
     */
    private static boolean deleteStatementErrorCheck(String[] tokens, boolean containsWhere) {
        if(!tokens[0].toLowerCase().equals("delete")) {
            System.err.println("Expected delete statement to begin with delete keyword");
            return false;
        }

        if(!tokens[1].toLowerCase().equals("from")) {
            System.err.println("Expected from keyword");
            return false;
        }

        if (containsWhere) {
            if(!tokens[3].toLowerCase().equals("where")) {
                System.err.println("Expected where keyword");
                return false;
            }
        }

        return true;
    }

    /**
     * Checks to see if the record to be deleted is being referenced by another table
     * @param sm the storage manager for this session
     * @param catalog the catalog for this session
     * @param nameOfTableToDeleteRecords name of the table that records are being deleted from
     * @param recordToDelete tuple to be deleted
     * @return true if the record to be deleted is referenced by another table
     *         false otherwise
     */
    private static boolean fkChecker(StorageManager sm, Catalog catalog, Table nameOfTableToDeleteRecords, ArrayList<Object> recordToDelete) {
        ArrayList<Table> listOfTables = catalog.getTables();
        for (Table table : listOfTables) {
            ArrayList<ForeignKey> listOfFK = table.getForeignKeys();
            for (ForeignKey fk : listOfFK) {
                String referencedAttr = nameOfTableToDeleteRecords.getAttrByName(fk.getRefAttribute()).getAttributeName();
                int idxReferencedAttr = nameOfTableToDeleteRecords.findAttrIndex(referencedAttr);
                String attribute = table.getAttrByName(fk.getAttrName()).getAttributeName();
                int idxAttribute = table.findAttrIndex(attribute);
                if (fk.getRefTableName().equals(nameOfTableToDeleteRecords.getTableName())) {
                    ArrayList<ArrayList<Object>> refTableRecords = sm.getRecords(table);
                    for (ArrayList<Object> records : refTableRecords) {
                        if (records.get(idxAttribute).equals(recordToDelete.get(idxReferencedAttr))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Parses the delete statement and queries the statement properly with the constraints
     * @param statement the statement to parse and query
     * @return true if the SQL query parses correctly
     *         false otherwise
     */
    public static boolean deleteStatement (String statement) {
        // Obtains the catalog
        Catalog catalog = (Catalog) ACatalog.getCatalog();
        // Obtains the storage manager
        StorageManager storageManager = (StorageManager) AStorageManager.getStorageManager();

        // Checks to see if the statement contains a where clause
        boolean containsWhere = statement.toLowerCase().contains("where");
        String[] tokens;
        // Tokenizes the statement query according to whether there is a where clause in the statement
        if (containsWhere) {
            tokens = statement.replace("\n", "").replace(";", "").split(" ", 5);
            if(!deleteStatementErrorCheck(tokens, true)) return false;
        } else {
            tokens = statement.replace("\n", "").replace(";", "").split(" ", 3);
            if(!deleteStatementErrorCheck(tokens, false)) return false;
        }

        String tableName = tokens[2];
        // If a table exists
        if (catalog.containsTable(tableName)) {
            // Gets the table, list of attributes of that table, and the primary key index of that table
            Table table = (Table) catalog.getTable(tableName);
            ArrayList<Attribute> tableAttributes = table.getAttributes();
            int pkIdx = table.getPrimaryKeyIndex();
            // If there is a where clause, it parses each record to see if it validates the where clause
            if (containsWhere) {
                String whereStatement = tokens[4];

                StringBuilder whereClause = new StringBuilder();
                whereClause.append(whereStatement);

                Node parseTree = null;
                Shunter shunter = new Shunter(tableAttributes);
                try {
                    parseTree = shunter.shunt(whereClause.toString());

                    ArrayList<ArrayList<Object>> records = storageManager.getRecords(table);
                    for(ArrayList<Object> record : records) {
                        if(parseTree != null)
                            // there was a where clause we need to abide by
                            if(!parseTree.validate(record))
                                continue;

                        // If the record passes the where clause and is not being referenced by another table through
                        // a foreign key, delete the record
                        if (!fkChecker(storageManager, catalog, table, record)) {
                            storageManager.deleteRecord(table, record.get(pkIdx));
                        } else {
                            return false;
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                // Attempts to delete all the records from the table
                ArrayList<ArrayList<Object>> listOfRecords = storageManager.getRecords(table);
                for (int idx = 0; idx < listOfRecords.size(); idx++) {
                    ArrayList<Object> record = listOfRecords.get(idx);
                    // If the record passes the where clause and is not being referenced by another table through
                    // a foreign key, delete the record
                    if (!fkChecker(storageManager, catalog, table, record)) {
                        storageManager.deleteRecord(table, record.get(pkIdx));
                    } else {
                        return false;
                    }
                }
            }

            return true;
        } else {
            // If the table does not exists, throw an error
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
                "(5, \"true\", true, null);";
        ArrayList<Attribute> attr = new ArrayList<>();
        Attribute pk = new Attribute("int", "Integer");
        attr.add(pk);
        attr.add(new Attribute("varchar13", "Varchar(13)"));
        attr.add(new Attribute("boolean", "Boolean"));
        attr.add(new Attribute("double", "Double"));
        catalog.addTable("foo", attr, pk);
        if (InsertParser.insertStatement(testing)) {
            System.out.println("Inserting test records successful");
        }
        Table table = (Table) catalog.getTable("foo");
        System.out.println(storageManager.getRecords(table));
        String testingDelete = "delete from foo where double = 4.14;";
        System.out.println(testingDelete);
        if (deleteStatement(testingDelete)) {
            System.out.println("Deleting record successful");
        }
        System.out.println(storageManager.getRecords(table));
        String testing2 = "insert into foo values (4, \"foo\", true, 2.1),\n" +
                "(6, \"baz\", true, 4.14),\n" +
                "(7, \"bar\", false, 5.2),\n" +
                "(8, \"true\", true, null);";
        if (InsertParser.insertStatement(testing2)) {
            System.out.println("Inserting test records successful");
        }
        System.out.println(storageManager.getRecords(table));
        String testingDelete2 = "delete from foo;";
        if (deleteStatement(testingDelete2)) {
            System.out.println("Deleting all records successful");
        }
        System.out.println(storageManager.getRecords(table));
    }
}
