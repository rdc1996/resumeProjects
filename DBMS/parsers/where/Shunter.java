package parsers.where;

import java.util.ArrayList;
import java.util.Stack;
import common.Attribute;
import common.AttributeType;
import parsers.QuerySplitter;

public class Shunter {
    Stack<Node> valStack = new Stack<>();
    Stack<Node> opStack = new Stack<>();
    ArrayList<Attribute> attributes;

    public Shunter(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }


    /**
     * Gets the attribute type from the name of a variable
     * @param attrName the attribute name to lookup
     *
     * @return AttributeType or null if not found
     * */
    private AttributeType getAttrTypeFromName(String attrName) {
        for(Attribute attr : this.attributes) {
            if(attr.getAttributeName().equals(attrName))
                return AttributeType.toAttributeType(attr);
        }

        return null;
    }

    /**
     * Gets index of an attribute from it's name
     * @param attrName the attribute name to lookup
     *
     * @return index of attribute, or -1 if not foudn
     * */
    private int getAttrIndexFromName(String attrName) {
        int index = 0;
        for(Attribute attr : this.attributes) {
            if(attr.getAttributeName().equals(attrName))
                return index;
            index++;
        }

        // something has gone horribly wrong
        return -1;
    }

    /**
     * Checks that a varname exists in the attributes to shunt on
     * @param varName variable name to check
     *
     * @return true if exists false if not
     * */
    private boolean checkVarNameValid(String varName) {
        for(Attribute a : this.attributes)
            if(a.getAttributeName().equalsIgnoreCase(varName))
                return true;

        return false;
    }

    /**
     * Tokenizes a where clause to prepare for the shunter. Should not include the where keyword
     * @param whereclause the whereclause to shunt
     *
     * @return list of nodes
     * */
    private ArrayList<Node> tokenize(String whereclause) throws Exception {
        ArrayList<Node> nodes = new ArrayList<>();

        ArrayList<String> tokens = QuerySplitter.splitQuery(whereclause);

        int counter = 0;

        for(String token : tokens) {
            String lowerToken = token.toLowerCase();

            switch(counter) {
                case 2: // Var or Val
                    Object value = AttributeType.parseType(token);
                    AttributeType type = AttributeType.guessType(value);
                    if(type != null) {
                        nodes.add(new ValNode(value, type));
                        break;
                    }
                    // if null, then add as a var node. fallthrough
                case 0: // Var
                    if(!AttributeType.isTokenVariable(token))
                        throw new Exception(token + " is not a valid attribute name");
                    if(!this.checkVarNameValid(token))
                        throw new Exception(token + " is not an attribute that exists");

                    nodes.add(new VarNode(token, getAttrTypeFromName(token), getAttrIndexFromName(token)));
                    break;
                default:
                    switch(lowerToken) {
                        case "and":
                            nodes.add(new AndNode());
                            break;
                        case "or":
                            nodes.add(new OrNode());
                            break;
                        case "<":
                            nodes.add(new OpNode(Operation.Less));
                            break;
                        case "<=":
                            nodes.add(new OpNode(Operation.LessEql));
                            break;
                        case ">":
                            nodes.add(new OpNode(Operation.Greater));
                            break;
                        case ">=":
                            nodes.add(new OpNode(Operation.GreaterEql));
                            break;
                        case "=":
                            nodes.add(new OpNode(Operation.Eql));
                            break;
                        case "!=":
                            nodes.add(new OpNode(Operation.NotEql));
                            break;
                    }
            }

            counter = (counter + 1) % 4;
        }

        return nodes;
    }

    /**
     * Helper method. Empties the Operator stack and pops appropriate values off of value stack as well
     * */
    private void emptyOpStack(Stack<Node> valStack, Stack<Node> opStack) {
        while(!opStack.isEmpty()) {
            Node opNode = opStack.pop();
            // if we try to pop valStack and it's empty
            // something has gone horribly wrong
            // likely the where clause was not complete
            Node rightVal = valStack.pop();
            Node leftVal = valStack.pop();

            opNode.setLeft(leftVal);
            opNode.setRight(rightVal);

            valStack.push(opNode);
        }
    }

    /**
     * Creates a parse tree based off a where clause
     * @param whereClause the where clause
     *
     * @return the fully built parse tree
     * */
    public Node shunt(String whereClause) throws Exception {
        if(whereClause == null || whereClause.isEmpty())
            return null;

        ArrayList<Node> nodes = this.tokenize(whereClause);
        Stack<Node> valStack = new Stack<>();
        Stack<Node> opStack = new Stack<>();

        for(Node node : nodes) {
            // this covers val and varnodes (varnodes are valnodes)
            if(node instanceof ValNode) {
                valStack.push(node);
                continue;
            }

            if(opStack.isEmpty() || opStack.peek().precedence >= node.precedence) {
                opStack.push(node);
                continue;
            }

            emptyOpStack(valStack, opStack);
            opStack.push(node);
        }

        // empty one last time
        emptyOpStack(valStack, opStack);

        // there should be one value in the valStack left.
        // this is our tree
        return valStack.pop();
    }

    public static void main(String[] args) throws Exception {
        String test = "a = x and b = 2";
        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute("a", "Varchar"));
        attrs.add(new Attribute("b", "Integer"));
        attrs.add(new Attribute("x", "Char"));
        Shunter shunter = new Shunter(attrs);

        Node tree = shunter.shunt(test);
        System.out.println(tree);

        ArrayList<Object> vals = new ArrayList<>();
        vals.add("foo");
        vals.add(2);
        vals.add("foo");

        System.out.println(tree.validate(vals));
    }
}
