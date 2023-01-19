package parsers;

import catalog.ACatalog;
import common.*;
import parsers.where.Node;
import parsers.where.Shunter;
import storagemanager.AStorageManager;

import java.util.*;

/*
  Class for DML parser

  This class is responsible for parsing DDL statements

  You will implement the parseDMLStatement and parseDMLpaddedQuery functions.
  You can add helper functions as needed, but the must be private and static.

  @author Scott C Johnson (sxjcs@rit.edu)

 */
public class DMLParser {

    private final static ArrayList<String> keyWords = new ArrayList<>(Arrays.asList("create", "table", "drop",
            "alter", "primarykey", "foreignkey", "references", "add", "default", "notnull",  "insert", "into",
            "values", "delete", "from", "where", "update", "set"));

    /**
     * This function will parse and execute DML statements (insert, delete, update, etc)
     *
     * This will be used for parsing DML statement that do not return data
     *
     * @param stmt the statement to parse/execute
     * @return true if successfully parsed/executed; false otherwise
     */
    public static boolean parseDMLStatement(String stmt){
        // statements should end with a semicolen
        if(!stmt.strip().endsWith(";"))
            return false;

        // strip semicolons and spaces from the end
        stmt = stmt.replaceAll(";*\s*$", "");

        if (stmt.toLowerCase().startsWith("insert")) {
            return InsertParser.insertStatement(stmt);
        } else if (stmt.toLowerCase().startsWith("update")) {
            return UpdateParser.parseUpdateStatement(stmt);
        } else if (stmt.toLowerCase().startsWith("delete")) {
            return DeleteParser.deleteStatement(stmt);
        } else {
            System.err.println("DML Statement does not start with a valid keyword.");
            return false;
        }
    }

    /**
     * This function will parse and execute DML statements (select)
     *
     * This will be used for parsing DML statement that return data
     * @param query the query to parse/execute
     * @return the data resulting from the query; null upon error.
     *         Note: No data and error are two different cases.
     */
    public static ResultSet parseDMLQuery(String query){
        if(!query.strip().endsWith(";"))
            return null;
        query = query.replaceAll(";*\s*$", "");
        if (query.toLowerCase().startsWith("select")) {
            return SelectParser.selectStatement(query);
        } else {
            System.err.println("DML Query does not start with a valid keyword.");
            return null;
        }
    }

    public static void main(String[] args){
        String select01 = "select * from foo;";
        String select02 = "select name, gpa\n" +
                            "from student;";
        String select03 = "select name, dept_name\n" +
                            "from student, department\n" +
                            "where student.dept_id = department.dept_id;";
        String select04 = "select *\n" +
                            "from foo\n" +
                            "orderby x;";
        String select05 = "select t1.a, t2.b, t2.c, t3.d from t1, t2, t3 where t1.a = t2.b and t2.c = t3.d orderby t1.a;";
//        int length = select01.length();
//        String select01WithDelimiters = placeDelimiterInQuery(select01);
//        System.out.println(select01WithDelimiters);
        parseDMLQuery(select03);

    }
}
