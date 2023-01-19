package catalog;
import org.w3c.dom.Attr;
import storagemanager.AStorageManager;
import common.Attribute;
import common.ForeignKey;
import common.ITable;
import common.Table;
import storagemanager.StorageManager;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Catalog extends ACatalog{

    private String location;

    private int pageSize;

    private int pageBufferSize;

    private ArrayList<Table> tables;

    public Catalog(String location, int pageSize, int pageBufferSize) {
        //if there is a db file at location, read the saved metadata in and use the saved page size. If not, create
        // completely new catalog using the provided info
        this.location = location;
        this.pageBufferSize = pageBufferSize; // we're using the provided pBS regardless of whether or not there is an existing DB
        this.tables = new ArrayList<>();
        File databaseDir = new File(location + "/catalog");
        if(!databaseDir.exists()) {
            this.pageSize = pageSize;
        }
        else {
            // read in page size and the number of tables from catalog file
            File catalog = new File(location + "/catalog");
            int tableNum = 0;
            try {
                FileInputStream fis = new FileInputStream(catalog);
                DataInputStream dis = new DataInputStream(fis);
                this.pageSize = dis.readInt();
                tableNum = dis.readInt();
                dis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            // read in the table metadata from schema directory
            for (int i = 0; i < tableNum; i++) {
                try {
                    FileInputStream fis = new FileInputStream(location + "/schema/" + (i + 1));
                    DataInputStream dis = new DataInputStream(fis);
                    this.tables.add(readTable(dis));
                    dis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    /***
     * Reads a Table object stored on the disk.
     * @param dis a DataInputStream object
     * @return a Table containing a name, list of Attributes, PrimaryKey object, list of ForeignKeys, and list of pageIds
     */
    public Table readTable(DataInputStream dis){
        try
        {
            int tableNameLength= dis.readInt();
            StringBuilder tableName = new StringBuilder();
            for (int i = 0; i < tableNameLength; i++) {
                tableName.append(dis.readChar());
            }
            int attributeCount = dis.readInt();
            ArrayList<Attribute> attributes = new ArrayList<>();
            for (int i = 0; i < attributeCount; i++){
                Attribute attribute = readAttribute(dis);
                attributes.add(attribute);

            }
            int primaryKeyIndex = dis.readInt();
            Attribute primaryKey = attributes.get(primaryKeyIndex);
            int foreignKeyCount = dis.readInt();
            ArrayList<ForeignKey> foreignKeys = new ArrayList<>();
            for (int i = 0; i < foreignKeyCount; i++) {
                ForeignKey foreignKey = readForeignKey(dis);
                foreignKeys.add(foreignKey);
            }
            int nonNullAttrCount = dis.readInt();
            ArrayList<Attribute> nonNullAttrs = new ArrayList<>();
            for(int i = 0; i < nonNullAttrCount; i++){
                Attribute nonNullAttr = readAttribute(dis);
                nonNullAttrs.add(nonNullAttr);

            }
            int pageIdCount = dis.readInt();
            ArrayList<Integer> pageIds = new ArrayList<>();
            for (int i = 0; i < pageIdCount; i++){
                pageIds.add(dis.readInt());
            }
            return new Table(tableName.toString(), attributes, primaryKey, foreignKeys, pageIds, nonNullAttrs);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return null;
    }

    /***
     * Reads in an Attribute object from the disk.
     * @param dis a DataInputStream object used to read it in
     * @return the newly created Attribute
     */
    public Attribute readAttribute(DataInputStream dis){
        try {
            int attributeNameLength = dis.readInt();
            StringBuilder attributeName = new StringBuilder();
            for(int i = 0; i < attributeNameLength; i++){
                attributeName.append(dis.readChar());
            }
            int attributeTypeNum = dis.readInt();
            String attributeType = "";
            if(attributeTypeNum == 0){
                attributeType += "Integer";
            }
            else if(attributeTypeNum == 1){
                attributeType += "Double";
            }
            else if(attributeTypeNum == 2){
                attributeType += "Boolean";
            }
            else if(attributeTypeNum == 3){
                int charLength = dis.readInt();
                attributeType += "Char(" + charLength + ")";
            }
            else if(attributeTypeNum == 4){
                int varCharLength = dis.readInt();
                attributeType += "Varchar(" + varCharLength + ")";
            }
            return new Attribute(attributeName.toString(), attributeType);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    /***
     * Reads a ForeignKey object from the disk.
     * @param dis a DataInputStream object
     * @return a ForeignKey object
     */
    public ForeignKey readForeignKey(DataInputStream dis){
        try {
            int attributeNameLength = dis.readInt();
            StringBuilder attributeName = new StringBuilder();
            for(int i = 0; i < attributeNameLength; i++){
                attributeName.append(dis.readChar());
            }
            int refTableNameLength = dis.readInt();
            StringBuilder refTableName = new StringBuilder();
            for(int i = 0; i < refTableNameLength; i++){
                refTableName.append(dis.readChar());
            }
            int refAttributeNameLength = dis.readInt();
            StringBuilder refAttributeName = new StringBuilder();
            for(int i = 0; i < refAttributeNameLength; i++){
                refAttributeName.append(dis.readChar());
            }
            return new ForeignKey(refTableName.toString(), refAttributeName.toString(), attributeName.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    /***
     * Gets the location of the database (assume no trailing /)
     * @return the location of the database
     */
    public String getDbLocation() {
        return this.location;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPageBufferSize() {
        return this.pageBufferSize;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public boolean containsTable(String tableName) {
        for(Table table: tables){
            if(table.getTableName().equals(tableName)){
                return true;
            }
        }
        return false;
    }

    public ITable addTable(String tableName, ArrayList<Attribute> attributes, Attribute primaryKey) {
        Table table = new Table(tableName, attributes, primaryKey, new ArrayList<ForeignKey>(), new ArrayList<Integer>(), new ArrayList<Attribute>());
        HashSet<Attribute> attributeSet = new HashSet<>(attributes);
        if(containsTable(tableName)) {
            return null;
        }
        if (attributeSet.size() != attributes.size()) {
            return null;
        }
        if (!attributes.contains(primaryKey)) {
            return null;
        }
        this.tables.add(table);
        return table;
    }

    public ITable getTable(String tableName) {
        for(Table table: tables){
            if(table.getTableName().equals(tableName)){
                return table;
            }
        }
        return null;
    }

    public Table removeTable(String tableName) {
        int index = 0;
        for(Table table : this.tables) {
            if(table.getTableName().equals(tableName))
                return this.tables.remove(index);
            index++;
        }
        return null;
    }

    public boolean dropTable(String tableName) {
        boolean success = clearTable(tableName);
        if(!success)
            return false;

        Table removedTable = this.removeTable(tableName);
        File file = new File(location + "/schema/" + removedTable.getTableId());

        for (Table table : this.tables) {
            if (table.getTableName().equals(tableName)) {
                table.clearPageIds();
                tables.remove(table);
                break;
            }
        }

        // nothing to remove
        if(file.exists())
            file.delete();

        return success;
    }

    // stubbed for now. Ignore until phase 4
    public boolean alterTable(String tableName, Attribute attr, boolean drop, Object defaultValue) {

//        if(drop){
//            return getTable(tableName).dropAttribute(attr.getAttributeName());
//        } else {
//            if(getTable(tableName).addAttribute())
//            if(defaultValue == null){
//
//            } else {
//
//            }
//        }
//
       return false;
    }

    public boolean clearTable(String tableName) {
        AStorageManager sm = StorageManager.getStorageManager();
        ITable tableToRemove = getTable(tableName);
        if (tableToRemove == null) {
            return false;
        }
        sm.clearTableData(tableToRemove);
        return true;
    }

    // stubbed for now. Ignore until phase 4
    public boolean addIndex(String tableName, String indexName, String attrName) {
        return false;
    }

    // stubbed for now. Ignore until phase 4
    public boolean dropIndex(String tableName, String indexName) {
        return false;
    }

    public boolean saveToDisk() {
        // make a new file named catalog at specified directory path of our database
        String catalogPath = this.location + "/catalog";
        File catalogFile = new File(catalogPath);
        if (catalogFile.exists()) {
            try {
                boolean result = catalogFile.delete();
                if (!result) {
                    System.err.println("Error deleting " + this.location + "/catalog");
                }
            }
            catch (Exception e) {
                System.err.println("IO Exception trying to delete " + this.location + "/catalog");
            }
        }

        // Actually create the catalog file
        try {
            if(!catalogFile.createNewFile()){
                System.err.println("Error creating the catalog file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // make a new directory named schema at specified directory path of our database
        String schemaPath = this.location + "/schema";
        File schemaDir = new File(schemaPath);
        if (schemaDir.exists()) {
            try {
                boolean result = schemaDir.delete();
                if (!result) {
                    System.err.println("Error deleting " + this.location + "/schema");
                }
            }
            catch (Exception e) {
                System.err.println("IO Exception trying to delete " + this.location + "/schema");
            }
        }
        if(!schemaDir.mkdirs()){
            System.err.println("Error creating the schema directory.");
        }

        // make a new directory named index at specified directory path of our database
        String indexPath = this.location + "/index";
        File indexDir = new File(indexPath);
        if (indexDir.exists()) {
            try {
                boolean result = indexDir.delete();
                if (!result) {
                    System.err.println("Error deleting " + this.location + "/index");
                }
            }
            catch (Exception e) {
                System.err.println("IO Exception trying to delete " + this.location + "/index");
            }
        }
        if(!indexDir.mkdirs()){
            System.err.println("Error creating the index directory.");
        }

        boolean wasSuccess = false;
        try {
            FileOutputStream fos = new FileOutputStream(catalogPath);
            DataOutputStream dos = new DataOutputStream(fos);

            //store the page size and number of tables in catalog file
            dos.writeInt(this.pageSize);
            dos.writeInt(this.tables.size());
            dos.close();

            // write each table to its own file in schema directory
            for(int i = 0; i < this.tables.size(); i++){
                String tablePath = schemaPath + "/" + (i + 1);
                this.tables.get(i).writeTable(tablePath);
            }

            wasSuccess = true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return wasSuccess;
    }
}
