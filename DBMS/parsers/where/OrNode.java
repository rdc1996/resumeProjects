package parsers.where;

import java.util.ArrayList;

import common.AttributeType;

public class OrNode extends Node { 

    public OrNode() {
        // lowest precedence
        super(1, AttributeType.Boolean);
    }

    @Override
    public boolean validate(ArrayList<Object> columns) throws Exception {
        // handle nulls
        if(this.getLeft() == null && this.getRight() == null)
            return false;

        // this is dumb and spaghetti
        // too bad!
        if(this.getLeft() == null)
            return this.getRight().validate(columns);

        return this.getLeft().validate(columns) || this.getRight().validate(columns);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("OR");

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
