package parsers.where;

import java.util.ArrayList;
import common.AttributeType;

public abstract class Node { 
    public final int precedence;
    public final AttributeType type;
    private Node left = null;
    private Node right = null;

    /**
     * A node. Has precedence, and a type
     * */
    public Node(int precedence, AttributeType type) {
        this.precedence = precedence;
        this.type = type;
    }

    /**
     * Sets the left of the node
     *
     * @param node node to set
     * */
    public void setLeft(Node node) {
        this.left = node;
    }

    /**
     * Access left node
     *
     * @return node
     * */
    public Node getLeft() {
        return this.left;
    }

    /**
     * Sets the right of the node
     * 
     * @param node node to set
     * */
    public void setRight(Node node) {
        this.right = node;
    }

    /**
     * Access right node
     *
     * @return node
     * */
    public Node getRight() {
        return this.right;
    }

    /**
     * Recursivly validates a record using left, right, and self nodes. Implementation dependent on node type
     *
     * @param columns a record to be validated
     *
     * @return validated or not
     * */
    public abstract boolean validate(ArrayList<Object> columns) throws Exception;
}
