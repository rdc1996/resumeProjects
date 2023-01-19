package common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Class representing a record/tuple.
 */
public class Record implements Comparable<Record>{
    private ArrayList<Object> columns;
    private ArrayList<Attribute> attributes;
    private int primaryKeyIndex;


    /**
     * The record that is being stored in the pages. Holds a list of columns, attributes and the index for where
     * the primary key can be found
     *
     * @param primaryKeyIndex the index of the primary key in the list of attributes
     * @param attributes the list of attributes this record has
     * @param columns the list of columns for this record
     */
    public Record (int primaryKeyIndex, ArrayList<Attribute> attributes, ArrayList<Object> columns) {
        this.primaryKeyIndex = primaryKeyIndex;
        this.attributes = attributes;
        this.columns = columns;
    }


    /**
     * Get the number of columns for this record
     *
     * @return the number of columns
     */
    public int getNumColumns() {
        return this.columns.size();
    }


    /**
     * Get the columns that are in this record
     *
     * @return the list of columns
     */
    public ArrayList<Object> getColumns() {
        return columns;
    }


    /**
     * Get the column that is stored at the given index
     *
     * @param index the index the column is stored at
     * @return the column at the given index
     */
    public Object getColumn(int index) {
        return this.columns.get(index);
    }


    /**
     * Get the primary key for this record
     *
     * @return the primary key
     */
    public Object getPrimaryKey() {
        return this.columns.get(this.primaryKeyIndex);
    }


    /**
     * Get the list of attributes for this record
     *
     * @return the list of attributes
     */
    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }


    /**
     * Get the  primary key index for this record
     *
     * @return the index of the primary key
     */
    public int getPrimaryKeyIndex() {
        return primaryKeyIndex;
    }


    /**
     * Validates the record. Returns true if none of the rules are broken and returns false if the chars/varchars
     * are too long
     *
     * @return whether the record is validated
     */
    public boolean validateRecord() {
        for(int i = 0; i < this.getNumColumns(); i++) {
            Attribute attr = this.attributes.get(i);
            AttributeType type = AttributeType.toAttributeType(attr);
            Object value = this.columns.get(i);
            switch(type) {
                case Varchar:
                case Char:
                    if(value != null && value.toString().length() > AttributeType.getAttributeSize(attr))
                        return false;
                default:
            }
        }

        return true;
    }


    /**
     * Get the size of the record in bytes
     *
     * @return the current size of the record
     */
    public int getSize() {
        try {
            return this.toBytes().length;
        } catch(Exception e) {
            System.err.println(e);
        }
        return -1; // I don't want to deal with throwing exceptions here
    }


    /**
     * The compareTo method for records. Compares the current record to the one given.
     *
     * @param anotherRecord the record to be compared to
     * @return -1 if the records are not the same
     */
    public int compareTo(Record anotherRecord) {
        // both should be the same kind of reccord
        Object col = this.columns.get(primaryKeyIndex);
        Object anotherCol = anotherRecord.columns.get(primaryKeyIndex);

        AttributeType type = AttributeType.toAttributeType(this.attributes.get(primaryKeyIndex));

        switch(type) {
            case Integer:
                return Integer.compare((Integer)col, (Integer)anotherCol);
            case Double:
                return Double.compare((Double)col, (Double)anotherCol);
            case Boolean:
                return Boolean.compare((Boolean)col, (Boolean)anotherCol);
            default:
                return col.toString().compareTo(anotherCol.toString());
        }
    }


    /**
     * Generates the null bitmap
     *
     * @return the null bitmap
     */
    private BitSet generateNullBitmap() {
        BitSet bits = new BitSet(this.getNumColumns());

        int i = 0;
        for(i = 0; i < this.getNumColumns(); i++)
            if (columns.get(i) == null)
                bits.set(i);
        // always add an extra true bit
        // we only need to remember this bit when reading in a record from bytes
        // this way the actual size of the byte[] that the bitmap generates
        // should be consistant
        // is it spaghetti? maybe. does it work? yes
        bits.set(i);

        return bits;
    }

    // general structure will be: size, num attributes, null bitmap, attributes
    // if the attribute is not a varchar, put the data there
    // if the attribute is a varchar, write the length of it, then the chars
    // write bytes, but not the int needed to get the size
    // can be considered a helper for toBytes

    /**
     * general structure will be: size, num attributes, null bitmap, attributes
     * if the attribute is not a varchar, put the data there
     * if the attribute is a varchar, write the length of it, then the chars
     * write bytes, but not the int needed to get the size
     * can be considered a helper for toBytes
     *
     * @return the byte array of the columns
     * @throws Exception e
     */
    private byte[] toBytesNoSize() throws Exception {
        BitSet nullBitMap = this.generateNullBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bytes);

        try{
            dos.writeInt(this.getNumColumns());

            dos.write(nullBitMap.toByteArray());

            for(int i = 0; i < this.getNumColumns(); i++) {
                if(nullBitMap.get(i))
                    continue;

                Attribute attr = this.attributes.get(i);
                AttributeType type = AttributeType.toAttributeType(attr);

                Object value = this.columns.get(i);
                switch(type) {
                    case Integer:
                        dos.writeInt((int) value);
                        break;
                    case Double:
                        dos.writeDouble((double) value);
                        break;
                    case Boolean:
                        dos.writeBoolean((boolean) value);
                        break;
                    case Char:
                        dos.writeChars(value.toString());
                        // padding
                        int padding = AttributeType.getAttributeSize(attr) - value.toString().length();
                        for(int j = 0; j < padding; j++)
                            dos.writeChar(0);
                        break;
                    case Varchar:
                        dos.writeInt(value.toString().length());
                        dos.writeChars(value.toString());
                        break;
                    default:
                        throw new Exception("Unknown type encountered");
                }
            }
        } catch(Exception e) {
            throw e;
        } finally {
            dos.close();
            bytes.close();
        }
        return bytes.toByteArray();
    }


    /**
     * Calls the bytesNoSize method to convert the columns over to byts
     *
     * @return the byte array of the columns
     * @throws Exception e
     */
    public byte[] toBytes() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bytes);

        try {
            byte[] bytesNoSize = this.toBytesNoSize();
            dos.writeInt(bytesNoSize.length);
            dos.write(bytesNoSize);
        } catch(Exception e) {
            throw e;
        } finally {
            dos.close();
            bytes.close();
        }
        return bytes.toByteArray();
    }


    /**
     * Convert a byte array into a record.
     *
     * @param bytes the byte representation of the record
     * @param primaryKeyIndex the index of the primary key in the list of attributes
     * @param attributes the list of attributes
     * @return the record version of the byte array
     * @throws Exception e
     */
    public static Record fromBytes(byte[] bytes, int primaryKeyIndex, ArrayList<Attribute> attributes) throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(input);
        ArrayList<Object> columns = new ArrayList<>();

        try{
            int numAttrs = dis.readInt();
            int numBits = numAttrs + 1; // we always add a true bit to the bitset
            byte[] bitMapAsBytes = dis.readNBytes(numBits / 8 + 1);
            BitSet nullBitMap = BitSet.valueOf(bitMapAsBytes);

            for(int i = 0; i < numAttrs; i++) {
                if(nullBitMap.get(i))  {
                    columns.add(null);
                    continue;
                }

                Attribute attr = attributes.get(i);
                AttributeType type = AttributeType.toAttributeType(attr);
                Object value = null;
                String chars = ""; // used to reconstruct char and varchar attributes
                int charsLen = 0; // also used to reconstruct char and varchar attributes

                switch(type) {
                    case Varchar:
                        charsLen = dis.readInt();
                        break;
                    case Char:
                        charsLen = AttributeType.getAttributeSize(attr);
                        break;
                    default:
                }

                switch(type) {
                    case Integer:
                        value = dis.readInt();
                        break;
                    case Double:
                        value = dis.readDouble();
                        break;
                    case Boolean:
                        value = dis.readBoolean();
                        break;
                    case Varchar:
                    case Char:
                        for(int j = 0; j < charsLen; j++) {
                            char achar = dis.readChar();
                            if(achar != 0)
                                chars += achar;
                        }
                        value = chars;
                        break;
                    default:
                        throw new Exception("Unknown type encountered");
                }

                columns.add(value);
            }

        } catch(Exception e) {
            throw e;
        } finally {
            dis.close();
            input.close();
        }

        Record record = new Record(primaryKeyIndex, attributes, columns);
        if(!record.validateRecord())
            throw new Exception("There was some invalid data in the record");
        return record;
    }

    /**
     * Convert the record into a readable string
     *
     * @return the string representation of the record
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < this.getNumColumns(); i++) {
            sb.append("Attribute: ");
            sb.append(this.columns.get(i));
            sb.append(" : ");
            sb.append(this.attributes.get(i).getAttributeType());
            sb.append('\n');
        }

        return sb.toString();
    }


    /**
     * Equals method for records. Returns true if the current record is the same as the given record
     *
     * @param o the record to be compared to
     * @return true if the given record is the same as the current record, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Record anotherRecord))
            return false;

        if(this.primaryKeyIndex != anotherRecord.primaryKeyIndex)
            return false;

        if(this.getNumColumns() != anotherRecord.getNumColumns())
            return false;

        if(!this.attributes.get(this.primaryKeyIndex).equals(anotherRecord.attributes.get(this.primaryKeyIndex)))
            return false;

        return this.columns.get(this.primaryKeyIndex).equals(anotherRecord.columns.get(this.primaryKeyIndex));
    }

    public static void main(String[] args) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<Object> columns = new ArrayList<>();

        attributes.add(new Attribute("int", "Integer"));
        attributes.add(new Attribute("double", "Double"));
        attributes.add(new Attribute("bool", "Boolean"));
        attributes.add(new Attribute("char3", "Char(3)"));
        attributes.add(new Attribute("varchar13", "Varchar(13)"));
        attributes.add(new Attribute("char3", "Char(3)"));

        columns.add(1);
        columns.add(2.0);
        columns.add(true);
        columns.add(null);
        columns.add("astring");
        columns.add("hi");

        Record record = new Record(0, attributes, columns);
        System.out.printf("Record %s\n record size: %d\n", record, record.getSize());
        try {
            byte[] bytes = record.toBytes();
            // theoretically, primaryKeyIndex and attributes should be information
            // that can come from table

            // simulate page reading the int containing size information
            bytes = Arrays.copyOfRange(bytes, Integer.BYTES, bytes.length);
            Record newRecord = Record.fromBytes(bytes, record.primaryKeyIndex, attributes);
            System.out.printf("Record %s\n record size: %d\n", newRecord, newRecord.getSize());
            System.out.println(newRecord);

            System.out.printf("Records equal(should be)? %s\n", record.equals(newRecord));

            Record emptyRecord = new Record(0, new ArrayList<Attribute>(), new ArrayList<Object>());
            System.out.printf("Records equal(should not be)? %s\n", record.equals(emptyRecord));


        } catch(Exception e ) { System.err.println(e); }
    }
}
