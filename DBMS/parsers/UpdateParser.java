package parsers;

import java.util.ArrayList;

import catalog.ACatalog;
import catalog.Catalog;
import common.Attribute;
import common.AttributeType;
import common.Table;
import parsers.where.Node;
import parsers.where.Shunter;
import storagemanager.AStorageManager;
import storagemanager.StorageManager;

public class UpdateParser {

    /**
     * Basic check if the set of update tokens are correct
     * @param tokens list of tokens to chekc
     *
     * @return false if statement looks bad, true if it looks okay
     * */
    private static boolean updateStatementErrorCheck(ArrayList<String> tokens) {
        if(tokens == null || tokens.size() < 6)
            return false;

        if(!tokens.get(0).toLowerCase().equals("update")) {
            System.err.println("Expceted update statement to begin with update keyword");
            return false;
        }

        if(!tokens.get(2).toLowerCase().equals("set")) {
            System.err.println("Expceted set keyword");
            return false;
        }

        if(!tokens.get(4).toLowerCase().equals("=")) {
            System.err.println("Expceted equal sign");
            return false;
        }

        return true;
    }

    /**
     * Creates a new record
     * @param oldRecord the old record we want to update
     * @param table table oldRecord belongs to
     * @param colIndex the index of the attribute we want to update
     * @param valueToken the value (or mathmatical expression) we want to update to
     * @param mathOp the math operation (if any) to do
     *
     * @return new Record as an arraylist of objects
     * */
    private static ArrayList<Object> createNewRecord(ArrayList<Object> oldRecord, Table table, int colIndex,
            String valueToken, String mathOp) throws Exception {
        ArrayList<Object> newRecord = new ArrayList<>();
        Object value = null;
        Object oldValue = oldRecord.get(colIndex);

        if(table.getNonNulls().contains(table.getAttributes().get(colIndex)))
            throw new Exception("Trying to update a non-null column");

        if(!(mathOp == null) && !mathOp.isBlank()) {
            Object left, right = null;
            String leftToken = valueToken.split(" ")[0];
            String rightToken = valueToken.split(" ")[2];

            try {
                left = Double.parseDouble(leftToken);
            } catch(NumberFormatException e) {
                left = oldRecord.get(table.findAttrIndex(leftToken));
            }

            try {
                right = Double.parseDouble(rightToken);
            } catch(NumberFormatException e) {
                right = oldRecord.get(table.findAttrIndex(rightToken));
            }

            if(!(left instanceof Integer) && !(left instanceof Double))
                throw new ArithmeticException("left hand side is not a math value");

            if(!(right instanceof Integer) && !(right instanceof Double))
                throw new ArithmeticException("right hand side is not a math value");

            switch(mathOp) {
                case "+":
                    value = (Double) left + (Double) right;
                    break;
                case "-":
                    value = (Double) left - (Double) right;
                    break;
                case "*":
                    value = (Double) left * (Double) right;
                    break;
                case "/":
                    value = (Double) left / (Double) right;
                    break;
            }

            if(oldValue instanceof Integer)
                value = (Integer) value;
            if(oldValue instanceof Double)
                value = (Double) value;
        }
        else if(valueToken.equals("null")) {
            value = null;
        }
        else {
            value = AttributeType.parseType(valueToken);
            if(value == null)
                value = oldRecord.get(table.findAttrIndex(valueToken));
        }

        if(value != null)
            if(!value.getClass().equals(oldValue.getClass()))
                throw new Exception("Cannot update. Value types do not match");

        for(int i = 0; i < oldRecord.size(); i++)
            newRecord.add(i == colIndex ? value : oldRecord.get(i));

        return newRecord;
    }

    /**
     * Parses the update statement
     *
     * @param stmt statement to parse
     *
     * @return true if parsed fully, false if something went wrong
     * */
    public static boolean parseUpdateStatement(String stmt){
        Catalog catalog = (Catalog) ACatalog.getCatalog();
        StorageManager sm = (StorageManager) AStorageManager.getStorageManager();

        ArrayList<String> tokens = null;
        try {
            tokens = QuerySplitter.splitQuery(stmt);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        if(!updateStatementErrorCheck(tokens))
            return false;

        Table table = (Table) catalog.getTable(tokens.get(1));
        if(table == null)
            return false;

        String attrName = tokens.get(3);
        Shunter shunter = new Shunter(table.getAttributes());
        int index = table.findAttrIndex(attrName);
        if(index == -1)
            return false;

        String valueToken = tokens.get(5);

        String mathOp = "";
        if(tokens.size() > 6)
            mathOp = tokens.get(6);
        // we can only do these kinds of math
        // if the location of where the mathop could be is not a math op, assume we are not doing math
        if(!mathOp.matches("[+*/-]"))
            mathOp = "";

        if(!mathOp.isBlank())
            valueToken += ' ' + mathOp +  ' ' + tokens.get(7);

        StringBuilder whereClause = new StringBuilder();
        int whereIndex = mathOp.isBlank() ? 7 : 9;
        for(; whereIndex < tokens.size(); whereClause.append(tokens.get(whereIndex) + ' '), whereIndex++);

        Node parseTree = null;
        boolean updated = false;
        try {
            parseTree = shunter.shunt(whereClause.toString());

            ArrayList<ArrayList<Object>> records = sm.getRecords(table);
            for(ArrayList<Object> record : records) {
                if(parseTree != null) 
                    // there was a where clause we need to abide by
                    if(!parseTree.validate(record))
                        continue;

                ArrayList<Object> newRecord = createNewRecord(record, table, index, valueToken, mathOp);
                if(!sm.updateRecord(table, record, newRecord))
                    return false;
                else
                    updated = true;
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }

        return updated;
    }

    public static void main(String[] args) throws Exception {
        ArrayList<Object> record = new ArrayList<>();
        record.add(1);
        record.add("'Star Platinum'");
        record.add(2.2);
        record.add("'The World'");

        ArrayList<Object> record2 = new ArrayList<>();
        record2.add(3);
        record2.add("'Star Platinum'");
        record2.add(2.2);
        record2.add("'The World'");


        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("attr1", "Integer"));
        attributes.add(new Attribute("attr2", "Varchar"));
        attributes.add(new Attribute("attr3", "Double"));
        attributes.add(new Attribute("attr4", "Varchar"));

        Catalog cat = (Catalog) ACatalog.createCatalog("temp", 1024, 5);
        StorageManager.createStorageManager();
        StorageManager sm = (StorageManager) StorageManager.getStorageManager();
        cat.addTable("Table", attributes, attributes.get(0));

        Table table = (Table) cat.getTable("Table");
        sm.insertRecord(table, record);
        sm.insertRecord(table, record2);

        // should change 1 to 2
        //ArrayList<Object> newRecord = UpdateParser.createNewRecord(record, table, 0, "2", "");

        // should fail
        //ArrayList<Object> newRecord = UpdateParser.createNewRecord(record, table, 1, "2", "");

        // should change 'star platinum' to 'the world'
        //ArrayList<Object> newRecord = UpdateParser.createNewRecord(record, table, 1, "attr4", "");

        //System.out.println(updated);

        for(ArrayList<Object> records : sm.getRecords(table)) {
            for(Object o : records)
                System.out.println(o);
            System.out.println("===");
        }
    }
}
