package parsers.where;

import java.util.ArrayList;

import common.AttributeType;

public class ValNode extends Node {
    protected Object value;

    public ValNode(Object value, AttributeType type) {
        super(-1, type);
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    // a val is a leaf node. It is just true by default
    // we also don't do anything with the columns here,
    // but we need it to satisfy the abstract class extension
    @Override
    public boolean validate(ArrayList<Object> columns) throws Exception {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.value);

        if(this.getLeft() != null) {
            sb.append("\n\t");
            sb.append(this.getLeft());
        }

        if(this.getRight() != null) {
            sb.append("\n\t");
            sb.append(this.getRight());
        }

        return sb.toString();
    }
}
