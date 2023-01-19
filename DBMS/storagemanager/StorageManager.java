package storagemanager;

import common.*;
import catalog.*;
import common.Record;
import common.Page.RecordInserted;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class StorageManager extends AStorageManager {
    private int pageID;
    private BufferManager bufferManager;

    /**
     * The constructor for the storage manager. Creates a new page buffer.
     */
    public StorageManager() {
        ACatalog catalog = ACatalog.getCatalog();
        String pagesPath = catalog.getDbLocation() + "/pages";
        File pagesDir = new File(pagesPath);
        if (!pagesDir.exists()) {
            if (!pagesDir.mkdirs()) {
                System.err.println("Error creating the pages directory.");
            }
        }

        this.pageID = 0;
        for(File file : Objects.requireNonNull(pagesDir.listFiles())) {
            int pageNum = Integer.parseInt(file.getName());
            this.pageID = Math.max(pageID, pageNum);
        }
        this.pageID++;
        this.bufferManager = new BufferManager(catalog.getPageSize(), catalog.getPageBufferSize());
    }


    /**
     * Clears all of the underlying data stored in this table. Includes all pages, both on the physical
     * disk and the buffer.
     *
     * @param table the table to drop
     * @return true if the table was cleared, false otherwise
     */
    public boolean clearTableData(ITable table) {
        Table readTable = (Table) table;
        ArrayList<Integer> pageIds = readTable.clearTable();
        return bufferManager.removePages(pageIds);
    }


    /**
     * Gets the record from the given table with the matching primary key value.
     *
     * @param table the table to get the record from
     * @param pkValue the value of the primary key
     * @return the record with the matching primary key value
     */
    public ArrayList<Object> getRecord(ITable table, Object pkValue) {
        Table tableToRead = (Table) table;
        ArrayList<Integer> pageIDs = tableToRead.getPageIds();
        for (int pageIDofPage : pageIDs) {
            Page page = bufferManager.readPage(tableToRead, pageIDofPage);
            ArrayList<Record> records = page.getRecords();
            for (Record record : records) {
                if (record.getPrimaryKey().equals(pkValue)) {
                    return record.getColumns();
                }
            }
        }
        return null;
    }


    /**
     * Get all of the records for the given table. Data is returned in primary key order.
     *
     * @param table the table to get the records for
     * @return all of the records in the given table.
     */
    public ArrayList<ArrayList<Object>> getRecords(ITable table) {
        ArrayList<ArrayList<Object>> allRecords = new ArrayList<>();
        Table tableToRead = (Table) table;
        ArrayList<Integer> pageIDs = tableToRead.getPageIds();
        for (int pageID : pageIDs) {
            Page page = bufferManager.readPage(tableToRead, pageID);
            ArrayList<Record> records = page.getRecords();
            for (Record record : records) {
                allRecords.add(record.getColumns());
            }
        }

        return allRecords;
    }


    /**
     * Insert a record into the given table
     *
     * @param table the table to insert the data into
     * @param record the record to insert
     * @return true if inserted properly, false otherwise
     */
    public boolean insertRecord(ITable table, ArrayList<Object> record) {
        Table tableToInsert = (Table) table;
        ArrayList<Integer> pageIDs = tableToInsert.getPageIds();
        int pkIndex = tableToInsert.getPrimaryKeyIndex();
        Record newRecord = new Record(pkIndex, table.getAttributes(), record);
        if (pageIDs.size() == 0) {
            Page page = bufferManager.makePage(pageID);
            pageIDs.add(pageID);
            pageID++;
            page.insertRecord(newRecord);
            return true;
        } else {
            Page currentPage = null;
            RecordInserted success = null;
            for (Integer id : pageIDs) {
                currentPage = bufferManager.readPage(tableToInsert, id);
                success = currentPage.insertRecord(newRecord);
                if(success == RecordInserted.INSERTED)
                    break;

                switch(success) {
                    case DUPLICATE:
                        return false;
                    case INSERTED:
                    case TOO_LARGE:
                        //noop
                }
            }

            // check if too large
            if(success == RecordInserted.TOO_LARGE)
                currentPage.forceInsertRecord(newRecord);

            if(currentPage.tooLarge()) {
                Page newPage = bufferManager.makePage(pageID);
                Page.splitPage(currentPage, newPage);
                int addIndex = pageIDs.indexOf(currentPage.getId()) + 1;
                pageIDs.add(addIndex, pageID);
                pageID++;
            }
            // if the record can't fit, this fails

            return true;
        }
    }


    /**
     * Delete a record with the matching primary key from the given table
     *
     * @param table the table to delete the record from
     * @param primaryKey the value of the primary key of the record to delete
     * @return true if the record was deleted, false otherwise
     */
    public boolean deleteRecord(ITable table, Object primaryKey) {
        Table readTable = (Table) table;
        ArrayList<Integer> pageIds = readTable.getPageIds();
        for (int i = 0; i < pageIds.size(); i++) {
            Page currentPage = bufferManager.readPage(readTable, pageIds.get(i));
            for (Record record : currentPage.getRecords()) {
                Object recPkValue = record.getPrimaryKey();
                if(recPkValue.equals(primaryKey)) {
                    currentPage.deleteRecord(record);
                    if (currentPage.getSize() < 1) {
                        readTable.removePage(i);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Update the old record with the new record in the provided table.
     *
     * @param table the table to update the record in
     * @param oldRecord the old record data
     * @param newRecord the new record data
     * @return true if the record was updated successfully, false otherwise
     */
    public boolean updateRecord(ITable table, ArrayList<Object> oldRecord, ArrayList<Object> newRecord) {
        Table readTable = (Table) table;

        Object pkValue = oldRecord.get(readTable.getPrimaryKeyIndex());

        if (deleteRecord(table, pkValue)) {
            if(!insertRecord(table, newRecord)) {
                insertRecord(table, oldRecord);
                return false;
            }

            return true;
        }

        return false;
    }


    /**
     * Forces a purge of the page buffer. Basically empties the buffer
     */
    public void purgePageBuffer() {
        bufferManager.purgeBuffer();
    }


    /**
     * Adds the provided default value to the end of all record in the provided table.
     *
     * @param table the table to add the attribute value to
     * @param defaultValue the default value to add
     * @return true if successful; false otherwise
     */
    public boolean addAttributeValue(ITable table, Object defaultValue) { return false; }

    /**
     * Drops the attribute value at the provided index in the provided table.
     * All other attribute values will be shifted down
     *
     * @param table the table to add the attribute value to
     * @param attrIndex the index of the attribute value to drop
     * @return true if successful; false otherwise
     */
    public boolean dropAttributeValue(ITable table, int attrIndex) { return false; }


    /**
     * Drops the attribute values at the provided index in the provided table.
     * All other attribute values will be shifted down
     *
     * @param table the table to drop the attribute value to
     * @param attrIndex the index of the attribute value to drop
     */
    public void dropAttributeValues(ITable table, int attrIndex) {
        Table readTable = (Table) table;
        ArrayList<Integer> pageIds = readTable.getPageIds();
        for (Integer pageId : pageIds) {
            Page currentPage = bufferManager.readPage(readTable, pageId);
            for (Record record : currentPage.getRecords()) {
                ArrayList<Object> recordCols = record.getColumns();
                Object attributeValue = record.getColumn(attrIndex);
                recordCols.remove(attributeValue);
            }
        }
    }

    /**
     * Adds the provided default value to the end of all record in the provided table.
     *
     * @param table the table to add the attribute value to
     * @param defaultValue the default value to add
     * @return true if successful; false otherwise
     */
    public void addAttributeValues(ITable table, Object defaultValue) {
        Table readTable = (Table) table;
        ArrayList<Integer> pageIds = readTable.getPageIds();
        for (Integer pageId : pageIds) {
            Page currentPage = bufferManager.readPage(readTable, pageId);
            for (Record record : currentPage.getRecords()) {
                ArrayList<Object> recordCols = record.getColumns();
                recordCols.add(defaultValue);
            }
        }
    }
}
