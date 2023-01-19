package parsers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import catalog.ACatalog;
import catalog.Catalog;
import common.*;

import catalog.ACatalog;
import catalog.Catalog;
import common.Attribute;
import common.AttributeType;
import common.ForeignKey;
import common.Table;
import parsers.where.Node;
import parsers.where.Shunter;
import storagemanager.AStorageManager;
import storagemanager.StorageManager;

public class SelectParser {
    /**
     *
     * @param statement
     * @return
     */
    private static boolean selectStatementErrorCheck(String statement) {
        if(!statement.toLowerCase().contains("select")) {
            System.err.println("Expected select statement to begin with select keyword");
            return false;
        }

        if(!statement.toLowerCase().contains("from")) {
            System.err.println("Expected from keyword");
            return false;
        }
        return true;
    }

    /**
     * Takes a list of Attributes and returns a list of their attribute names
     * @param attributes the list of attributes to obtain the names from
     * @return the ArrayList of the attribute names
     */
    public static ArrayList<String> getAttributesNames(ArrayList<Attribute> attributes) {
        ArrayList<String> attrNames = new ArrayList<>();
        for (Attribute attribute : attributes) {
            attrNames.add(attribute.getAttributeName());
        }
        return attrNames;
    }

    /**
     * Checks if the table is null. If it is, return null. Then takes the select clause, checks if it is a "*'
     * if it is then it checks if there is an orderby clause. If there is, then it returns a table ordered by
     * the clause. Otherwise, it just returns the table. If the string is not a "*" then it parses out the attributes
     * with commas and checks if the attributes are valid within the table. If it is not valid, return null.
     * Otherwise, it will create an ArrayList with only the valid attributes. It then checks if there is an orderby,
     * if there is, then it returns a table ordered by the clause. Otherwise, it just returns the table.
     * @param statement the select clause
     * @param table the table
     * @param orderBy the orderby clause
     * @return ResultSet of the selected clause and with orderby
     *          null if issue was encountered
     */
    public static ResultSet parseSelectClause(String statement, ResultSet table, String orderBy) {
        if (table == null) {
            System.err.println("Table is null.");
            return null;
        }
        if (!statement.equals("*")) {
            //parse out the things that this select statement wants in the result set
            String[] attrNeeded = statement.split("\\s*,\\s*");
            ArrayList<Attribute> tableAttributes = table.attrs();
            ArrayList<String> tableNames = getAttributesNames(tableAttributes);
            ArrayList<Attribute> newAttributes = new ArrayList<>();
            for (String token : attrNeeded) {
                if (tableNames.contains(token)) {
                    int idxOfName = tableNames.indexOf(token);
                    newAttributes.add(tableAttributes.get(idxOfName));
                } else {
                    System.err.println(token + " is a invalid attribute.");
                    return null;
                }
            }
            ArrayList<ArrayList<Object>> currentRecords = table.results();
            ArrayList<ArrayList<Object>> newRecords = new ArrayList<>();
            for (ArrayList<Object> record : currentRecords) {
                ArrayList<Object> newRecord = new ArrayList<>();
                for (Attribute attribute : newAttributes) {
                    int idxOfAttr = tableAttributes.indexOf(attribute);
                    Object attrValue = record.get(idxOfAttr);
                    newRecord.add(attrValue);
                }
                newRecords.add(newRecord);
            }
            ResultSet selectedTable = new ResultSet(newAttributes, newRecords);
            if (!orderBy.equalsIgnoreCase("")) {
                try {
                    OrderByParser.orderBy(selectedTable, orderBy);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return selectedTable;
        } else {
            if (!orderBy.equalsIgnoreCase("")) {
                try {
                    OrderByParser.orderBy(table, orderBy);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return table;
        }
    }

    /***
     * Performs a Cartesian product of tables.
     * @param tables A list of tables who will form the Cartesian product
     * @param whereStatement a condition that all rows of the Cartesian product will satisfy
     * @return
     */
    public static ResultSet cartesianProduct(ArrayList<ITable> tables, String whereStatement){
        // Get current instance of the storage manager
        AStorageManager sm = AStorageManager.getStorageManager();

        // Create an Arraylist to hold the attributes of all the tables
        ArrayList<Attribute> attributes = new ArrayList<>();

        // If there are duplicate tables, immediately stop
        HashSet<ITable> set = new HashSet<>(tables);
        if(set.size() < tables.size()){
            System.err.println("Tables aren't unique in cartesian product.");
            return null;
        }

        // This will store the frequency of each attribute name among all the tables. If an attribute name has a
        // frequency higher than one, it has to be qualified with its table name.
        HashMap<String, Integer> attrFrequency = new HashMap<>();

        for(ITable table : tables){
            ArrayList<Attribute> tableAttrs = table.getAttributes();
            for(Attribute attribute : tableAttrs){
                String attrName = attribute.getAttributeName();
                if(attrFrequency.containsKey(attrName)){
                    attrFrequency.put(attrName, attrFrequency.get(attrName) + 1);
                } else {
                    attrFrequency.put(attrName, 1);
                }
            }
        }

        // Iterate through the tables adding all the attributes of each table to our list of attributes
        for(ITable table : tables){
            String tableName = table.getTableName();
            ArrayList<Attribute> tableAttrs = table.getAttributes();
            for(Attribute attribute : tableAttrs){
                String attrName = attribute.getAttributeName();
                String tempTableAttrName = "";
                if(attrFrequency.get(attrName) > 1){
                    tempTableAttrName += tableName + "." + attrName;
                } else {
                    tempTableAttrName += attrName;
                }
                attributes.add(new Attribute(tempTableAttrName, attribute.getAttributeType()));
            }
        }

        // Make a stack for processing each table for CP and put all the tables onto it in reverse order, since CP is
        // done from left to right
        Stack<ITable> tableStack = new Stack<>();
        for(int i = tables.size()-1; i >= 0; i--){
            tableStack.push(tables.get(i));
        }

        ITable firstTable = tableStack.pop();

        // Initialize the cartesian product records list with the records of the first table
        ArrayList<ArrayList<Object>> cpRecords = sm.getRecords(firstTable);

        // This will hold the records after each cartesian product of two tables. For example, if you have three tables
        // T1, T2, and T3, the cartesian product will be T1 x T2 x T3. firstRecords will have the result of T1 x T2, and
        // will hence be combined with the rows of T3.
        ArrayList<ArrayList<Object>> firstRecords = new ArrayList<>();

        // If there is a where statement, we make a parse tree to see if each record in the cartesian product satisfies
        // the condition in the where clause.
        Node parseTree = null;
        if(!whereStatement.isEmpty()) {
            StringBuilder whereClause = new StringBuilder();
            whereClause.append(whereStatement);

            Shunter shunter = new Shunter(attributes);
            try {
                parseTree = shunter.shunt(whereClause.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // In the case that there was only one table in the table list, we have to make sure to do the where
        // clause since it will never enter the while loop to do the where clause (we already popped the first table off
        // the stack, so the stack would be empty.
        if(tableStack.isEmpty()) {
            Iterator<ArrayList<Object>> iterator = cpRecords.iterator();
            while (iterator.hasNext()) {
                ArrayList<Object> record = iterator.next();
                if (parseTree != null) {
                    try {
                        if (!parseTree.validate(record)) {
                            iterator.remove();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }

        // Here the table stack should have at least one table
        while(!tableStack.isEmpty()){
            // Pop table off stack and get its records
            ITable table = tableStack.pop();
            ArrayList<ArrayList<Object>> tableRecords = sm.getRecords(table);

            for(ArrayList<Object> record1 : cpRecords){
                for(ArrayList<Object> record2 : tableRecords){

                    // Add all the values of record2 to the end of record1
                    ArrayList<Object> record = new ArrayList<>(record1);
                    record.addAll(record2);

                    // We only want to start validating records once there are no more tables in the stack. This is
                    // because we know that each record will have all the attributes by that time.
                    if(tableStack.isEmpty()){
                        // If the parse tree is null, that means the where statement is empty, so just add
                        // each record from the cartesian product into our collection of first records.
                        // If not, use the parseTree to validate the record
                        if(parseTree != null){
                            try {
                                if(parseTree.validate(record)){
                                    firstRecords.add(record);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        } else {
                            firstRecords.add(record);
                        }
                    } else {
                        firstRecords.add(record);
                    }
                }
            }
            // Set total cartesian product records to the intermediate result of the cartesian product of records
            cpRecords = new ArrayList<>(firstRecords);
            firstRecords = new ArrayList<>();
        }

        return new ResultSet(attributes, cpRecords);
    }

    /**
     * Parses the select statement and queries the statement properly with the constraints
     * @param statement the statement to parse and query
     * @return ResultSet of the SQL query
     */
    public static ResultSet selectStatement (String statement) {
        Catalog catalog = (Catalog) ACatalog.getCatalog();
        if (!selectStatementErrorCheck(statement)) {
            return null;
        }
        Pattern pattern = Pattern.compile("select\\s+([*]|[\\w.]+(\\s*,\\s*[\\w.]+)*)\\s+from\\s+([\\w.]+(\\s*,\\s*[\\w.]+)*)(.*)");
        Matcher matcher = pattern.matcher(statement);
        String select = "";
        String from = "";
        String whereOrderby = "";
        if (matcher.find()) {
            select = matcher.group(1);
            from = matcher.group(3);
            whereOrderby = matcher.group(5);
        } else {
            System.err.println("There is an issue with parsing the select and from clause.");
            return null;
        }

        ArrayList<String> splittedQuery = null;
        try {
            splittedQuery = QuerySplitter.splitQuery(whereOrderby);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String where = "";
        String orderby = "";
        if (!whereOrderby.equals("")) {
            if (!splittedQuery.get(0).equalsIgnoreCase("where")
                    && !splittedQuery.get(0).equalsIgnoreCase("orderby")) {
                System.err.println("The optional keyword has been spelt wrong.");
                return null;
            }
            if (splittedQuery.get(splittedQuery.size() - 2).equalsIgnoreCase("orderby")) {
                splittedQuery.remove(splittedQuery.size() - 2);
                orderby += splittedQuery.remove(splittedQuery.size() - 1);
            }
            int containsWhere = 1;
            for (; containsWhere < splittedQuery.size(); where += (splittedQuery.get(containsWhere) + ' '), containsWhere++);
        }

        ArrayList<ITable> fromTables = new ArrayList<>();
        String[] tables = from.split(",\\s*");
        for (String name : tables) {
            if (catalog.containsTable(name)) {
                fromTables.add(catalog.getTable(name));
            } else {
                System.err.println("Table name is invalid.");
                return null;
            }
        }

        ResultSet fromWhere = cartesianProduct(fromTables, where);
        return parseSelectClause(select, fromWhere, orderby);
    }

    public static void main(String[] args) {
        String testing1 = "select something.A, something.B from something, something " +
                "where something = something and somethingelse > 1 orderby something;";
        String testing2 = "select * from table1 orderby attr1";
        ResultSet rs1 = selectStatement(testing2);
    }
}

