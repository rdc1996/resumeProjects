package common;

import java.io.*;
import java.util.ArrayList;
import catalog.Catalog;

public class Page {

    public enum RecordInserted {
        INSERTED,
        TOO_LARGE,
        DUPLICATE;
    }

    private ArrayList<Record> records;
    private int id;
    private boolean changed = false;
    private int size = 0;
    private int maxSize;


    /**
     * Constructor for a Page
     *
     * @param id the page ID for this page
     * @param maxSize the biggest size the page can hold
     */
    public Page(int id, int maxSize) {
        this.id = id;
        this.maxSize = maxSize;
        this.records = new ArrayList<>();
    }


    /**
     * Splits the data in the given page so that the larger second half of the data gets inserted into the new page.
     *
     * @param page the original page that needs its data split.
     * @param newPage empty page to store the larger second half of the data from the original page.
     * @return the second with the larger second half of the data stored
     */
    public static Page splitPage(Page page, Page newPage) {
        // splits page between page and newPage
        // newPage is assumed to be empty
        // larger values should go to newPage
        int num2remove = page.records.size() / 2;
        for(int i = 0; i < num2remove; i++) {
            Record record = page.deleteRecord(page.records.size() - 1);
            newPage.insertRecord(record);
        }
        return newPage;
    }


    /**
     * Returns the changed field. True if the page has been modified and false otherwise.
     *
     * @return whether this page has been modified or not.
     */
    public boolean getChanged() {
        return this.changed;
    }


    /**
     * Gets the ID of this page.
     *
     * @return the ID of this page
     */
    public int getId() {
        return id;
    }


    /**
     * Get the list of records in this page.
     *
     * @return the list of records in the current page.
     */
    public ArrayList<Record> getRecords() {
        return this.records;
    }


    /**
     * Gets the current size.
     *
     * @return returns the current size
     */
    public int getSize() {
        return this.size;
    }


    /**
     * Checks to see if the Record can fit in the Page.
     * @param record the Record to be inserted into the Page
     * @return true if the Record can be inserted, false otherwise
     */
    public boolean canFitRecord(Record record) {
        return (this.size + record.getSize()) <= this.maxSize;
    }


    /**
     * Checks to see if the Record goes before any of the Records currently on the Page.
     *
     * @param newRecord the Record to be inserted
     * @return -1 if the Record belongs before, 0 if there already exists a Record with the same primary key, 1 otherwise
     */
    public int belongsToPage(Record newRecord) {
        for (Record current : records) {
            if (newRecord.compareTo(current) < 0) {
                return -1;
            } else if (newRecord.compareTo(current) == 0) {
                return 0;
            }
        }
        return 1;
    }


    /**
     * Checks to see if the size of the Page is larger than the maximum size the Page can hold.
     *
     * @return true is the current size of the Page is too large, false otherwise
     */
    public boolean tooLarge() {
        return this.size > this.maxSize;
    }


    /**
     * Inserts a Record into the Page.
     *
     * @param newRecord the record to insert
     * @return an enum that indicates the status of the insert.
     */
    public RecordInserted insertRecord(Record newRecord) {
        /*
        If there are no Records on the Page, then it adds the Record to the Page,
        add the size of the Record to the Page size, indicate that the Page has been modified,
        then returns the status that the Record has been successfully inserted.
         */
        if(this.records.size() == 0) {
            this.size += newRecord.getSize();
            this.records.add(newRecord);
            this.changed = true;
            return RecordInserted.INSERTED;
        }

        /*
        Loops through the list of Records in this Page. If the Record to be inserted has the same primary key, then
        it returns the status that the Record is a duplicate. If the Record to be inserted belongs before the Record
        then it adds the Record at the correct location based of the primary key, indicate that the Page has been modified,
        then returns the status that the Record has been successfully inserted.
         */
        for (int i = 0; i < records.size(); i++) {
            Record current = records.get(i);
            if (newRecord.compareTo(current) == 0)
                return RecordInserted.DUPLICATE;
            if (newRecord.compareTo(current) < 0) {
                this.records.add(i, newRecord);
                this.size += newRecord.getSize();
                this.changed = true;
                return RecordInserted.INSERTED;
            }
        }

        return RecordInserted.TOO_LARGE;
    }


    /**
     * Inserts in a Record to the page regardless of the primary key value and forgoes sorting by primary key
     *
     * @param newRecord the record to be inserted.
     */
    public void forceInsertRecord(Record newRecord) {
        this.records.add(newRecord);
        this.size += newRecord.getSize();
        this.changed = true;
    }


    /**
     * Deletes a Record from the Page based off the index.
     *
     * @param index the index of the Record to be removed
     * @return the Record that was removed
     */
    public Record deleteRecord(int index) {
        Record removed = records.remove(index);
        if(removed != null) {
            this.size -= removed.getSize();
            this.changed = true;
        }

        return removed;
    }


    /**
     * Deletes a Record from the Page based off the Record.
     *
     * @param record the Record to remove
     * @return true if the Record was successfully removed, false otherwise.
     */
    public boolean deleteRecord(Record record) {
        boolean removed = records.remove(record);
        if(removed) {
            this.size -= record.getSize();
            this.changed = true;
        }
        return removed;
    }


    /**
     * Converts the Page into a byte array.
     * The structure of the byte array is as follows: [num of Records] [size of Record 1] [Record 1] [...]
     *
     * @return the byte array that the Page was converted into
     * @throws Exception if there was an issue with writing the Page into a byte array
     */
    public byte[] toBytes() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bytes);
        try {
            dos.writeInt(this.id);
            dos.writeInt(getRecords().size());
            for (Record record : records) {
                dos.write(record.toBytes());
            }
        } catch (Exception e) {
            System.err.println("Error in converting the Page to bytes");
        } finally {
            bytes.close();
            dos.close();
        }

        return bytes.toByteArray();
    }


    /**
     * Takes a byte array and converts the information to make the Page object from that information.
     *
     * @param table the Table that the Page belongs to
     * @param recordBytes the byte array with the information about the Page
     * @return the Page with the information from the byte array
     * @throws Exception if there was an error with reading in the bytes
     */
    public static Page fromBytes(Table table, byte[] recordBytes) throws Exception {
        Page page = null;
        ByteArrayInputStream bytes = new ByteArrayInputStream(recordBytes);
        DataInputStream dos = new DataInputStream(bytes);
        try {
            int id = dos.readInt();
            int maxSize = Catalog.getCatalog().getPageSize();
            page = new Page(id, maxSize);

            int numR = dos.readInt();
            for (int i = 0; i < numR; i++) {
                int recordSize = dos.readInt();
                byte[] info = dos.readNBytes(recordSize);
                Record record = Record.fromBytes(info, table.getPrimaryKeyIndex(), table.getAttributes());

                // we know records are stored to disk in sequential order
                // so we just want to add them back in the order they come in
                page.forceInsertRecord(record);
            }
        } catch (Exception e) {
            System.err.println("Error in converting the byte array to a Page object.");
        } finally {
            bytes.close();
            dos.close();
        }

        return page;
    }


    /**
     * Checks to see if the instance of the Object is a Page.
     *
     * @param o the Object to check
     * @return true if the Object is a Page, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Page page))
            return false;

        return this.id == page.id;
    }


    /**
     * Checks to see if the instance of the Object is a Page.
     * It then checks to see if the Page id of the object matches.
     * It then checks to see if the list of Records in the Object matches.
     *
     * @param o the Object to compare to
     * @return true if the conditions all match, false otherwise
     */
    private boolean deepEquals(Object o) {
        if(!(o instanceof Page page))
            return false;

        if(this.id != page.id)
            return false;
        
        for(int i = 0; i < records.size(); i++)
            if(!this.records.get(i).equals(page.records.get(i)))
                return false;

        return true;
    }

    public static void main(String[] args) throws Exception {
        int pageSize = 10000;
        Page testing_page = new Page(1, pageSize);
        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<Object> columns = new ArrayList<>();
        ArrayList<ForeignKey> fk = new ArrayList<>();
        ArrayList<Attribute> nonNulls = new ArrayList<>();
        ArrayList<Integer> pageIds = new ArrayList<>();
        pageIds.add(1);

        Attribute pk = new Attribute("int", "Integer");
        attributes.add(pk);
        attributes.add(new Attribute("double", "Double"));
        attributes.add(new Attribute("bool", "Boolean"));
        attributes.add(new Attribute("char3", "Char(3)"));
        attributes.add(new Attribute("varchar13", "Varchar(13)"));
        attributes.add(new Attribute("char3", "Char(3)"));

        columns.add(2);
        columns.add(2.0);
        columns.add(true);
        columns.add(null);
        columns.add("astring");
        columns.add("hi");

        ArrayList<Object> column2 = new ArrayList<>();

        column2.add(1);
        column2.add(3.0);
        column2.add(true);
        column2.add(null);
        column2.add("hello");
        column2.add("his");

        Table table = new Table("1", attributes, pk, fk, pageIds, nonNulls);
        Record record1 = new Record(0, attributes, columns);
        Record record2 = new Record(0, attributes, column2);

        testing_page.insertRecord(record1);
        testing_page.insertRecord(record2);

        // fromBytes is expecting the catalog to be running
        Catalog.createCatalog("please_dont_exist", pageSize, 1);
        Page newPage = Page.fromBytes(table, testing_page.toBytes());

        System.out.printf("does testing_page equal newPage: %s\n", testing_page.equals(newPage));
        System.out.printf("does testing_page deep equal newPage: %s\n", testing_page.deepEquals(newPage));
    }
}
