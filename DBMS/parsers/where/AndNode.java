package parsers.where;

import java.util.ArrayList;

import common.AttributeType;

public class AndNode extends Node { 

    public AndNode() {
        // lowest precedence
        super(1, AttributeType.Boolean);
    }

    @Override
    public boolean validate(ArrayList<Object> columns) throws Exception {
        // handle nulls
        if(this.getLeft() == null || this.getRight() == null)
            return false;

        if(!this.getLeft().type.equals(this.getRight().type))
            throw new Exception("Trying to compare invalid types: " + this.getLeft().type + this.getRight().type);

        return this.getLeft().validate(columns) && this.getRight().validate(columns);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("AND");

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
