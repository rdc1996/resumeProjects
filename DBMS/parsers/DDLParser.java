package parsers;

import catalog.ACatalog;
import catalog.Catalog;
import common.*;
import storagemanager.AStorageManager;
import storagemanager.StorageManager;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;

/*
  Class for DDL parser

  This class is responsible for parsing DDL statements

  You will implement the parseDDLStatement function.
  You can add helper functions as needed, but the must be private and static.

  @author Scott C Johnson (sxjcs@rit.edu)

 */
public class DDLParser {

    private final static ArrayList<String> keyWords = new ArrayList<>(Arrays.asList("create", "table", "drop",
            "alter", "primarykey", "foreignkey", "references", "add", "default", "notnull",  "insert", "into",
            "values", "delete", "from", "where", "update", "set"));


    /**
     * This function will parse and execute DDL statements (create table, create index, etc)
     * @param stmt the statement to parse
     * @return true if successfully parsed/executed; false otherwise
     */
    public static boolean parseDDLStatement(String stmt){
        String[] words = stmt.replaceAll("\\n", " ").trim().split("\\s+");
        if(words[0].equalsIgnoreCase("create") && words[1].equalsIgnoreCase("table")){
            if(!createTable(stmt)){
                System.err.println("Error creating table.");
                return false;
            }
        }
        else if (words[0].equalsIgnoreCase("drop") && words[1].equalsIgnoreCase("table")) {
            if (!dropTable(stmt.substring(0, stmt.length()-1))) { // remove semicolon
                System.err.println("Error dropping table");
                return false;
            }
        }
        else if (words[0].equalsIgnoreCase("alter") && words[1].equalsIgnoreCase("table")) {
            if (!alterTable(stmt.substring(0, stmt.length()-1))) { // remove semicolon
                System.err.println("Error altering table");
                return false;
            }
        }
        else {
            System.err.println("Error parsing statement.");
            return false;
        }
        return true;
    }


    /**
     * Drops the table given in the statement
     *
     * @param stmt the statement to be parsed
     * @return true if the table was successfully dropped, false otherwise
     */
    public static boolean dropTable(String stmt) {
        ACatalog tempCatalog = ACatalog.getCatalog();
        String[] listStmt = stmt.split("\\s+");
        if (listStmt.length != 3) {
            return false;
        }
        String tableName = listStmt[2];
        if(isKeyWord(tableName)){
            System.err.println("Tried to drop a table with a name that is a keyword.");
        }
        return tempCatalog.dropTable(tableName);
    }

    /***
     * This function checks to see if the foreign key that a user inputs is valid.
     * @param catalog The current instance of the catalog
     * @param refTableName The name of the table being referenced
     * @param refAttribute The name of the attribute in the table being referenced
     * @param attrType The attribute type of the foreign key
     * @return true if it's a valid foreign key, false otherwise
     */
    public static boolean validateForeignKey(ACatalog catalog, String refTableName, String refAttribute, String attrType){
        if(catalog.containsTable(refTableName)){
            Attribute ref = catalog.getTable(refTableName).getAttrByName(refAttribute);
            if(ref != null) {
                if (ref.getAttributeType().equals(attrType)) {
                    return true;
                } else {
                    System.err.println("Attribute types don't match.");
                }
            }
            else {
                System.err.println("Attribute of referenced table doesn't exist.");
            }
        } else {
            System.err.println("Referenced table " + refTableName + " doesn't exist.");
        }
        return false;

    }

    /***
     * Checks to see if a user defined name is a key word.
     * @param userDefinedName a String input by the user
     * @return true if it's a key word, false otherwise
     */
    public static boolean isKeyWord(String userDefinedName) {
        return keyWords.contains(userDefinedName);
    }

    /***
     * Checks to see if a user defined name is a valid attribute type
     * @param userDefinedName a String input by the user
     * @return true if it's an attribute type word, false otherwise
     */
    public static boolean isAttributeType(String userDefinedName) {
        int userDefinedNameLength = userDefinedName.length();
        String proper = userDefinedName.substring(0, 1).toUpperCase() + userDefinedName.substring(1, userDefinedNameLength);

        if(proper.equals("Integer") || proper.equals("Double") || proper.equals("Boolean")){
            return true;
        }
        else {
            if(proper.startsWith("Char")){
                String[] clauses = proper.split("Char");
                if(clauses.length != 2){
                    return false;
                }
                else {
                    String[] arr = clauses[1].split("[()]");
                    if(arr.length != 2){
                        return false;
                    }
                    else {
                        try {
                            int intValue = Integer.parseInt(arr[1]);
                            return true;
                        } catch (NumberFormatException e) {
                            System.err.println("Char size is supposed to be an integer.");
                            return false;
                        }
                    }
                }
            }
            else if(proper.startsWith("Varchar")){
                String[] clauses = proper.split("Varchar");
                if(clauses.length != 2){
                    return false;
                } else {
                    String[] arr = clauses[1].split("[()]");
                    if(arr.length != 2){
                        return false;
                    } else {
                        try {
                            int intValue = Integer.parseInt(arr[1]);
                            return true;
                        } catch (NumberFormatException e) {
                            System.err.println("Varchar size is supposed to be an integer.");
                            return false;
                        }
                    }
                }

            }
            else {
                return false;
            }
        }

    }

    /***
     * This function parses a create table statement and then creates the table.
     * @param stmt a String denoting a create table statement
     * @return true if the table was successfully created, false otherwise
     */
    public static boolean createTable(String stmt) {

        // convert the statement all to lowercase. We can do this because there are no String values in a create table stmt
        // Then replace all the newlines (if any) with spaces. Then we remove the leading and trailing spaces of the stmt
        // Finally, for our purposes, we split the string by commas.
        String[] words = stmt.toLowerCase().replaceAll("\\n", " ").trim().split(",");

        // this will hold the following string structure: "create table t1( attr1 attrtype constraint, "
        // Therefore, IF THE FORMAT GIVEN TO US IS CORRECT, we can extract the name of the table and the first attribute
        // of the table from first
        String first = words[0];

        // this will remove all leading and trailing spaces and split on spaces.
        String[] firstWords = first.trim().split("\\s+");

        // As said before, this will hold the tablename, but with an opening parenthesis. So we remove the last character
        String tableNameWithParen = firstWords[2];
        String tableName = tableNameWithParen.substring(0, tableNameWithParen.length() - 1);

        // The tablename can't be a keyword.
        if(isKeyWord(tableName)){
            System.err.println("Tried to create a table with a name that is a key word.");
            return false;
        }

        // Get the first attribute's name and check if it's a keyword or attribute type. If it is, error
        String attr1Name = firstWords[3];
        if(isKeyWord(attr1Name) || isAttributeType(attr1Name)){
            System.err.println("Tried to create an attribute with a name that isn't valid.");
            return false;
        }

        // Get the first attribute's type and check if it's a keyword or not an attribute type. If it is, error
        String attr1Type = firstWords[4];
        if(isKeyWord(attr1Type) || !isAttributeType(attr1Type)){
            System.err.println("Tried to create an attribute with a type that isn't valid.");
            return false;
        }

        ArrayList<ForeignKey> foreignKeys = new ArrayList<>();
        ArrayList<Attribute> nonNullAttributes = new ArrayList<>();
        Attribute primaryKey = null;
        boolean primaryKeyFound = false;
        ACatalog catalog = ACatalog.getCatalog();
        String attr1Constraint = "";
        ArrayList<Attribute> attributes = new ArrayList<>();

        // add first attribute to list
        Attribute firstAttr = new Attribute(attr1Name, attr1Type);
        attributes.add(firstAttr);

        // Now we check to see if the attribute had a constraint, since it's possible it could.
        if (firstWords.length == 6 || (firstWords.length == 7 && firstWords[6].equals(");")) ) {
            attr1Constraint += firstWords[5];
            if (attr1Constraint.equals("primarykey")) {
                primaryKeyFound = true;
                primaryKey = firstAttr;
            } else if (attr1Constraint.equals("notnull")) {
                nonNullAttributes.add(firstAttr);
            } else {
                System.err.println("Attempt to add an invalid constraint to an attribute of table " + tableName);
                return false;
            }
        }

        // Now we iterate through the comma separated list of clauses, except we start at index 1 since we did the first
        // one already.
        for (int i = 1; i < words.length; i++) {
            // Again, remove trialing and leading spaces and split on space
            String[] clause = words[i].trim().split("\\s+");

            // This checks to see if a primary key is being defined.
            if (clause[0].equals("primarykey(")) {
                if(!primaryKeyFound){
                    primaryKeyFound = true;
                    String primaryKeyName = clause[1];
                    if(isKeyWord(primaryKeyName) || isAttributeType(primaryKeyName)){
                        System.err.println("Tried to create a primary key with a name that isn't valid.");
                        return false;
                    }
                    for (Attribute attribute : attributes) {
                        if (attribute.getAttributeName().equals(primaryKeyName)) {
                            primaryKey = attribute;
                            break;
                        }
                    }
                }
                else {
                    System.err.println("Tried to create a table " + tableName + " with two primary keys.");
                    return false;
                }
            }

            // This checks to see if a foreign key is being defined.
            else if (clause[0].equals("foreignkey(")) {
                String attrName = clause[1];
                String refTableName = clause[4].substring(0, clause[4].length() - 1);
                String refAttribute = clause[5];
                String attrType = "";
                for (Attribute attribute : attributes) {
                    if (attribute.getAttributeName().equals(attrName)) {
                        attrType += attribute.getAttributeType();
                        break;
                    }
                }
                // If attrType is still empty at this point then it means that the attribute doesn't exist, and that is a big error
                if (attrType.equals("")) {
                    System.err.println("Foreign key attribute of " + tableName + " hasn't been created yet.");
                    return false;
                }

                // We'll want to see if the name of the attribute is a key word or an attribute type. If so, error
                if(isKeyWord(attrName) || isAttributeType(attrName)){
                    System.err.println("Tried to create an attribute with a name that isn't valid.");
                    return false;
                }

                // Now we have to validate the foreign key before adding it to the list
                if (validateForeignKey(catalog, refTableName, refAttribute, attrType)) {
                    foreignKeys.add(new ForeignKey(refTableName, refAttribute, attrName));
                } else {
                    System.err.println("Error creating foreign key of table " + tableName);
                    return false;
                }

            }
            // If we reach this else, the it means that we'e trying to define an ordinary attribute of the table
            else {
                String attrName = clause[0];
                String attrType = clause[1];
                if(isKeyWord(attrName) || isAttributeType(attrName)){
                    System.err.println("Tried to create an attribute with a name that isn't valid.");
                    return false;
                }
                if(isKeyWord(attrType) || !isAttributeType(attrType)){
                    System.err.println("Tried to create an attribute with a type that isn't valid.");
                    return false;
                }
                Attribute attr = new Attribute(attrName, attrType);
                if(!attributes.contains(attr)){
                    attributes.add(attr);
                } else {
                    System.err.println("Attempt to add a duplicate attribute.");
                    return false;
                }
                String attrConstraint = "";
                if (clause.length == 3) {
                    if(clause[2].equals(");")){
                        break;
                    }
                    attrConstraint += clause[2];
                    if (attrConstraint.equals("primarykey")) {
                        if (!primaryKeyFound) {
                            primaryKeyFound = true;
                            primaryKey = attr;
                        } else {
                            System.err.println("Tried to create table " + tableName + " with two primary keys.");
                            return false;
                        }
                    } else if (attrConstraint.equals("notnull")) {
                        nonNullAttributes.add(new Attribute(attrName, attrType));
                    } else {
                        System.err.println("Attempt to add an invalid constraint to an attribute of table " + tableName);
                        return false;
                    }
                }
            }
        }

        // If all goes well, the catalog will create this table and add it to the database. We must remember to add the
        // foreign keys and attributes that can't be null to the table as well.
        ITable table = catalog.addTable(tableName, attributes, primaryKey);
        for (ForeignKey foreignKey : foreignKeys) {
            table.addForeignKey(foreignKey);
        }
        for (Attribute nonNullAttr : nonNullAttributes) {
            ((Table) table).addNonNullAttribute(nonNullAttr);
        }
        return true;
    }

    /***
     * This function gets the type of the value in a String.
     * @param defaultValue - a default value that will be added to all tuples upon the addition of an attribute
     * @return a String denoting the type of the default value
     */
    public static String extractAttributeType(String defaultValue) {
            try {
                int intValue = Integer.parseInt(defaultValue);
                return "Integer";
            } catch (NumberFormatException ie) {
                try {
                    double doubleValue = Double.parseDouble(defaultValue);
                    return "Double";
                }  catch (NumberFormatException de) {
                    if(defaultValue.equals("true") || defaultValue.equals("false")){
                        return "Boolean";
                    } else {
                        return "";
                    }
                }
            }
    }

    /***
     * This function extracts the actual default value from a String
     * @param defaultValue - a default value that will be added to all tuples upon the addition of an attribute
     * @return an Object that can either be an Integer, Double, Boolean, or String
     */
    public static Object extractDefaultValue(String defaultValue) {
        if(!defaultValue.startsWith("\"") && !defaultValue.endsWith("\"")) {
            try {
                return Integer.parseInt(defaultValue);
            } catch (NumberFormatException nfe1){
                try {
                    return Double.parseDouble(defaultValue);
                } catch (NumberFormatException nfe2) {
                    return defaultValue.equals("true");

                }
            }
        }
        return defaultValue.substring(1, defaultValue.length() - 1); //remove the double quotes
    }


    /***
     * This function validates a default value.
     * @param defaultValue - a default value that will be added to all tuples upon the addition of an attribute
     * @param attrType - the type of the attribute being added. It must match the type of the default value once we extract it
     * @return true if the default value is valid, and false otherwise
     */
    public static boolean validateDefaultValue(String defaultValue, String attrType) {
        if(defaultValue.startsWith("\"") && defaultValue.endsWith("\"")){
            int length = defaultValue.length()-2;
            int attrTypeSize = java.lang.Integer.parseInt(attrType.split("[()]")[1]);
            if(length == attrTypeSize && attrType.toLowerCase().startsWith("char")){
                return true;
            }
            else if(length <= attrTypeSize && attrType.toLowerCase().startsWith("varchar")){
                return true;
            }
            else {
                return false;
            }
        }
        return attrType.equalsIgnoreCase(extractAttributeType(defaultValue));
    }

    /***
     * This function parses an alter table statement and then alters the table accordingly.
     * @param stmt - a String denoting an alter table statement
     * @return true if teha alter was a success, false otherwise
     */
    public static boolean alterTable(String stmt){
        // Replace all newlines with spaces, remove all leading and trailing spaces, split on space
        String[] words = stmt.replaceAll("\\n", " ").trim().split("\\s+");

        // Get the name  of the table we're altering and check if it's a keyword
        String tableName = words[2];
        ACatalog catalog = ACatalog.getCatalog();
        AStorageManager sm = AStorageManager.getStorageManager();
        boolean drop = false;
        if(isKeyWord(tableName)){
            System.err.println("Tried to alter a table with a name that is a key word.");
            return false;
        }
        ITable table = catalog.getTable(tableName);

        // Get the attribute name and check if it's a key word ot attribute type
        String attrName = words[4];
        if(isKeyWord(attrName) || isAttributeType(attrName)){
            System.err.println("Tried to drop an attribute with a name that isn't valid.");
            return false;
        }
        int attrIndex = -1;
        String attrType = "";

        // Check if we're dropping an attribute
        if(words[3].equalsIgnoreCase("drop")){
            if(table.getPrimaryKey().equals(table.getAttrByName(attrName))){
                System.err.println("Tried to drop primary key of table");
                return false;
            }
            //If the attribute being dropped is part of any foreign key constraint, that constraint must be dropped as well.
            ArrayList<ForeignKey> foreignKeys = table.getForeignKeys();
            foreignKeys.removeIf(fk -> fk.getAttrName().equals(attrName));

            // save the index of the attribute that was dropped. This will come in handy later.
            attrIndex = table.getAttributes().indexOf(table.getAttrByName(attrName));
            table.dropAttribute(attrName);
            drop = true;
        }

        // Check if we're adding an attribute
        else if (words[3].equalsIgnoreCase("add")){
            attrType += words[5];
            if(isKeyWord(attrType) || !isAttributeType(attrType)){
                System.err.println("Tried to add an attribute with a type that isn't valid.");
                return false;
            }
            if(!table.addAttribute(attrName, attrType)){
                System.err.println("Attempt to add a duplicate attribute");
                return false;
            }
        }

        // STEPS FOR TABLE ALTERATION

        // Get the records of the old table before we drop it
        ArrayList<ArrayList<Object>> records = sm.getRecords(table);

        // Drop old table
        catalog.dropTable(tableName);

        // Create new table with the old schema (reflecting the changes after we added or dropped an attribute)
        ITable newTable = catalog.addTable(tableName, table.getAttributes(), table.getPrimaryKey());
        ArrayList<ForeignKey> foreignKeys = table.getForeignKeys();
        ArrayList<Attribute> nonNullAttrs = ((Table) table).getNonNulls();
        for(ForeignKey fk : foreignKeys){
            newTable.addForeignKey(fk);
        }
        for(Attribute nna : nonNullAttrs){
            ((Table) newTable).addNonNullAttribute(nna);
        }

        // Now insert old records into new table. As we insert, we have to be mindful that we're either:
        // 1) dropping an attribute,
        // 2) adding an attribute with a default value of null, or
        // 3) adding an attribute with a defined default value
        for(ArrayList<Object> record : records){
            if(drop){
                record.remove(attrIndex); // case 1
            }
            else{
                if(words.length == 6){
                    record.add(null); // case 2
                }
                else if(words.length == 8 && words[6].equalsIgnoreCase("default")) {
                    String defaultValue = words[7];
                    if(validateDefaultValue(defaultValue, attrType)){
                        record.add(extractDefaultValue(defaultValue)); // case 3
                    }
                    else {
                        System.err.println("Attribute type and type of default value don't match.");
                        return false;
                    }
                }
                else {
                    System.err.println("Incorrect format for alter table statement.");
                    return false;
                }
            }
            // After making the changes to the record, we insert it into th new table
            sm.insertRecord(newTable, record);
        }
        // If all went well then we return true
        return true;
    }

    public static void main(String[] args){
        String stmt1 = "create table yuh( " +
                            "baz integer, " +
                            "bar Double notnull, " +
                            "bal boolean, " +
                            "bat varchar(30), " +
                            "bam char(3), " +
                            "primarykey( bar ), " +
                            "foreignkey( bar ) references bazzle( baz ) " +
                        ");";
        String stmt2 = "drop table foo;";
        String fkTable = "create table table2( attr7 integer primarykey, " +
                "foreignkey( attr7 ) references table1( attr1 ) );";
        String createTable =
                "create table large( " +
                        "attr1 integer primarykey, " +
                        "attr2 double, " +
                        "attr3 boolean, " +
                        "attr4 char(5), " +
                        "attr5 varchar(10) );";
        Catalog.createCatalog("please_dont_exist", 4096, 1);
        parseDDLStatement(fkTable);
    }

}


