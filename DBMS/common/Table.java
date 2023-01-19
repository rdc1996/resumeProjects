package common;

import storagemanager.AStorageManager;
import storagemanager.StorageManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Table implements ITable {
    private  int                         primaryKeyIndex;
    private  ArrayList<Attribute>        attributes;
    private  ArrayList<ForeignKey>       foreignKeys;
    private  ArrayList<Integer>          pageIds;
    private  String                      name;
    private  int                         id;
    private ArrayList<Attribute>         nonNulls;

    /**
     * Makes a Table object with a name, attribute list, primaryKey, foreignKeys, and pageIds list
     * @param name String denoting the table's name
     * @param attributes the list of attributes of a given table
     * @param primaryKey the primary key of the table
     * @param foreignKeys the foreign keys of the table
     * @param pageIds - the list of ids where each id is the id of a page where the table resides
     */
    public Table(String name, ArrayList<Attribute> attributes, Attribute primaryKey, ArrayList<ForeignKey> foreignKeys,
                 ArrayList<Integer> pageIds, ArrayList<Attribute> nonNulls) {
        this.name = name;
        this.attributes = attributes;
        this.primaryKeyIndex = attributes.indexOf(primaryKey);
        this.foreignKeys = foreignKeys;
        this.nonNulls = nonNulls;
        this.pageIds = pageIds;
    }

    public String getTableName() {
        return this.name;
    }

    /**
     * Get the index of the primary key in the attributes list
     * @return
     */
    public int getPrimaryKeyIndex() {
        return primaryKeyIndex;
    }

    public void setTableName(String name) {
        this.name = name;
    }

    public int getTableId() {
        return this.id;
    }

    public ArrayList<Attribute> getAttributes() {
        return this.attributes;
    }

    public Attribute getAttrByName(String name) {
        for(Attribute attr : this.attributes)
            if(attr.getAttributeName().equals(name))
                return attr;
        return null;
    }

    public int findAttrIndex(String attrName) {
        int index = 0;
        // search for the index of the attribute
        for(Attribute a : this.getAttributes()) {
            if(a.getAttributeName().equals(attrName))
                return index;

            index++;
        }

        return -1;
    }

    public Attribute getPrimaryKey() {
        return this.attributes.get(this.primaryKeyIndex);
    }

    // remove table's pageIds. Actual deletion from disk is handled by the bufferManager
    public ArrayList<Integer> clearTable() {
        ArrayList<Integer> ids = this.pageIds;
        pageIds.clear();
        return ids;
    }

    public ArrayList<ForeignKey> getForeignKeys() {
        return this.foreignKeys;
    }

    public ArrayList<Attribute> getNonNulls() { return this.nonNulls; }

    /**
     * Get the list of page ids
     * @return the table's list of page ids
     */
    public ArrayList<Integer> getPageIds() {
        return this.pageIds;
    }

    /**
     * Resets the page id list of a Table
     */
    public void clearPageIds(){
        this.pageIds = new ArrayList<>();
    }

    public boolean addAttribute(String name, String type) {
        for(Attribute attr : this.attributes)
            if(attr.getAttributeName().equals(name))
                return false;
        this.attributes.add(new Attribute(name, type));
        return true;
    }

    public boolean dropAttribute(String name) {
        AStorageManager sm = AStorageManager.getStorageManager();
        for(int i = 0; i < this.attributes.size(); i++) {
            if(this.attributes.get(i).getAttributeName().equals(name)) {
                this.attributes.remove(i);
                //((StorageManager) sm).dropAttributeValues(this, i);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the specified pageid from the table's list of pages ids. This means the table is not on that page anymore
     */
    public void removePage(int pageId) {
        pageIds.remove(pageId);
    }

    public boolean addForeignKey(ForeignKey fk) {
        return this.foreignKeys.add(fk);
    }

    public boolean addNonNullAttribute(Attribute nonNullAttr){
        return this.nonNulls.add(nonNullAttr);
    }

    // stubbed for now. Ignore until phase 4
    public boolean addIndex(String attributeName){
        return false;
    }

    /**
     * Writes out an attribute of the table to the disk.
     * @param dos The DataOutputStream object used for writing
     * @param attribute - The Attribute being written
     */
    public void writeAttribute(DataOutputStream dos, Attribute attribute) {
        try {
            String attributeName = attribute.getAttributeName();
            dos.writeInt(attributeName.length());
            dos.writeChars(attributeName);
            AttributeType type = AttributeType.toAttributeType(attribute);
            switch (type) {
                case Integer -> dos.writeInt(0);
                case Double -> dos.writeInt(1);
                case Boolean -> dos.writeInt(2);
                case Char -> {
                    dos.writeInt(3);
                    dos.writeInt(AttributeType.getAttributeSize(attribute));
                }
                case Varchar -> {
                    dos.writeInt(4);
                    dos.writeInt(AttributeType.getAttributeSize(attribute));
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * Writes out a Foreign Key of the table to the disk.
     * @param dos The DataOutputStream object used for writing
     * @param foreignKey - The foreign key being written
     */
    public void writeForeignKey(DataOutputStream dos, ForeignKey foreignKey) {
        try {
            String attributeName = foreignKey.getAttrName();
            dos.writeInt(attributeName.length());
            dos.writeChars(attributeName);

            String refTableName = foreignKey.getRefTableName();
            dos.writeInt(refTableName.length());
            dos.writeChars(refTableName);

            String refAttributeName = foreignKey.getRefAttribute();
            dos.writeInt(refAttributeName.length());
            dos.writeChars(refAttributeName);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /***
     * Writes out this Table to the disk.
     * @param filename the file that the table is being written to
     */
    public void writeTable(String filename){
        File tableFile = new File(filename);
        try {
            if(!tableFile.createNewFile()){
                System.err.println("Error creating the table file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream fos = new FileOutputStream(tableFile);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeInt(this.name.length());
            dos.writeChars(name);
            dos.writeInt(this.attributes.size());
            for (Attribute attribute : attributes) {
                writeAttribute(dos, attribute);
            }
            dos.writeInt(this.primaryKeyIndex);
            dos.writeInt(this.foreignKeys.size());
            for (ForeignKey foreignKey : foreignKeys) {
                writeForeignKey(dos, foreignKey);
            }
            dos.writeInt(this.nonNulls.size());
            for(Attribute nonNullAttr : nonNulls){
                writeAttribute(dos, nonNullAttr);
            }
            dos.writeInt(this.pageIds.size());
            for (int pageId : pageIds){
                dos.writeInt(pageId);
            }
            dos.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
