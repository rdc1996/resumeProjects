package common;

public enum AttributeType {
    // attribute types
    Integer,
    Double,
    Boolean,
    Char,
    Varchar;

    /***
     * Converts the String attribute type to an enum AttributeType
     * @param attribute the attribute of a table
     * @return an AttributeType
     */
    public static AttributeType toAttributeType(Attribute attribute) {
        String type = attribute.getAttributeType();

        if(type.toLowerCase().equals("integer"))
            return AttributeType.Integer;
        if(type.toLowerCase().equals("double"))
            return AttributeType.Double;
        if(type.toLowerCase().equals("boolean"))
            return AttributeType.Boolean;
        if(type.toLowerCase().startsWith("char"))
            return AttributeType.Char;
        if(type.toLowerCase().startsWith("varchar"))
            return AttributeType.Varchar;

        return null; // this should never happen
    }

    /***
     * Calculates the size of an attribute.
     * @param attribute the attribute of a table
     * @return the size of the attribute in bytes
     */
    // gets the size for attributes
    // in the case of varchar and char, get the given number of chars to go in
    public static int getAttributeSize(Attribute attribute) {
        AttributeType type = toAttributeType(attribute);

        switch(type){
            case Integer:
                return java.lang.Integer.BYTES;
            case Double:
                return java.lang.Double.BYTES;
            case Boolean:
                return 1;
            case Varchar:
            case Char:
                return java.lang.Integer.parseInt(attribute.getAttributeType().split("[()]")[1]);
            default:
                return -1;
        }
    }

    public static Object parseType(String token) throws Exception {
        try {
            return java.lang.Integer.parseInt(token);
        } catch(Exception e) {}

        try {
            return java.lang.Double.parseDouble(token);
        } catch(Exception e) {}

        if(token.toLowerCase().matches("true|false"))
            return java.lang.Boolean.parseBoolean(token);


        // if it is encased in quotations, assume it is a string
        if(token.matches("^'.*'$") || token.matches("^\".*\"$"))
            return token;

        if(!isTokenVariable(token))
            throw new Exception(token + " is not a valid variable name");
        return null;
    }

    public static boolean isTokenVariable(String token) {
        // duh
        if(token == null)
            return false;

        // token
        //return token.matches("[a-zA-Z]\\.*[a-zA-Z_0-9]*");
        return token.matches("[a-zA-Z][a-zA-Z_0-9]*([.][a-zA-Z][a-zA-Z_0-9]*)?");
    }

    // use this when parsing statements
    public static AttributeType guessType(Object value) {
        if(value instanceof Integer)
            return AttributeType.Integer;

        if(value instanceof Double)
            return AttributeType.Double;

        if(value instanceof Boolean)
            return AttributeType.Boolean;

        if(value instanceof String)
            // chars and varchars will be compared the same. it doesn't really matter what we choose here
            // it must start and end with quotes. these must be the same.
            if(value.toString().matches("^'.*'$") || value.toString().matches("^\".*\"$"))
                return AttributeType.Varchar;

        return null; // in this case, it is probably a variable name
    }
}

