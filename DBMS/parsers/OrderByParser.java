package parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

import common.Attribute;
import common.AttributeType;
import parsers.ResultSet;

public class OrderByParser {
    private static ArrayList<Integer> findAttrIndexes(ResultSet rset, ArrayList<String> attrs) throws Exception {
        ArrayList<Integer> indexes = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();

        for(String attr : attrs) {
            int index = -1;
            for(int i = 0; i < rset.attrs().size(); i++)
                if(rset.attrs().get(i).attributeName().equals(attr)) {
                    index = i;
                    break;
                }

            if(index == -1)
                throw new Exception("Could not sort on attribute " + attr);

            if(seen.contains(index))
                throw new Exception("Trying to sort on attribute " + attr + " twice");
            indexes.add(index);
            seen.add(index);
        }

        return indexes;
    }

    public static void orderBy(ResultSet rset, String attr) throws Exception {
        int index = -1;
        for(int i = 0; i < rset.attrs().size(); i++)
            if(rset.attrs().get(i).attributeName().equals(attr)) {
                index = i;
                break;
            }

        if(index == -1)
            throw new Exception("Could not sort on attribute " + attr);

        AttributeType type = AttributeType.toAttributeType(rset.attrs().get(index));
        final int findex = index;
        Collections.sort(rset.results(), (a, b) -> {
            switch(type) {
                case Integer:
                    return Integer.compare((Integer)a.get(findex), (Integer)b.get(findex));
                case Double:
                    return Double.compare((Double)a.get(findex), (Double)b.get(findex));
                case Boolean:
                    return Boolean.compare((Boolean)a.get(findex), (Boolean)b.get(findex));
                default:
                    return a.get(findex).toString().compareTo(b.get(findex).toString());
            }
        });
    }

    private static ArrayList<Object> record(int aint, double adouble, boolean abool, String achar, String avarchar) {
        ArrayList<Object> record = new ArrayList<>();

        record.add(aint);
        record.add(adouble);
        record.add(abool);
        record.add(achar);
        record.add(avarchar);

        return record;
    }

    public static void main(String[] args) throws Exception {
        ArrayList<Attribute> attrs = new ArrayList<>();

        attrs.add(new Attribute("attr1", "Integer"));
        attrs.add(new Attribute("attr2", "Double"));
        attrs.add(new Attribute("attr3", "Boolean"));
        attrs.add(new Attribute("attr4", "Char(5)"));
        attrs.add(new Attribute("attr5", "Varchar(10)"));

        ArrayList<ArrayList<Object>> records = new ArrayList<ArrayList<Object>>();
        records.add(record(3, 1.3, false, "asd", "there"));
        records.add(record(2, 1.2, true, "123", "there"));
        records.add(record(1, 1.1, true, "hi", "there"));

        ResultSet rs = new ResultSet(attrs, records);
        for(ArrayList<Object> record : rs.results()) {
            for(Object o : record)
                System.out.println(o);
            System.out.println("===");
        }

        System.out.println("ordering...");
        ArrayList<String> orderList = new ArrayList<>();
        orderBy(rs, "attr4");
        for(ArrayList<Object> record : rs.results()) {
            for(Object o : record)
                System.out.println(o);
            System.out.println("===");
        }

    }
}
