package parsers;

import java.util.ArrayList;

public class QuerySplitter {


    /**
     * Splits a query, but respecting strings
     * @param query the query
     *
     * @return the list of tokens
     */
    public static ArrayList<String> splitQuery(String query) throws Exception {
        ArrayList<String> tokens = new ArrayList<>();
        query = query.strip();

        while(!query.isEmpty()) {
            if(query.startsWith("'") || query.startsWith("\"")) {
                // unfortunately, java does not have tuples
                // index 0 is the token, index 1 is the rest
                // first quote, second quote, rest
                String[] tokenRest = query.split(String.valueOf(query.charAt(0)), 3);

                // append the quotes around so that the parser knows this is a string, not a variable name
                tokens.add('"' + tokenRest[1] + '"');
                query = tokenRest[2].strip();
                continue;
            }

            String[] tokenRest = query.split("\s+", 2);
            tokens.add(tokenRest[0].toLowerCase());
            if(tokenRest.length > 1)
                query = tokenRest[1];
            else
                query = "";
        }

        return tokens;
    }

    public static void main(String[] args) throws Exception {
        String foo = "hello world. I am a normal string";

        String bar = "'hello world' I am a \"string with quotes in it\"";

        for(String token : splitQuery(bar)) {
            System.out.println(token);
        }
    }
}
