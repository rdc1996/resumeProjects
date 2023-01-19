package parsers.where;

import java.util.ArrayList;

import common.AttributeType;

public class OpNode extends Node {
    private final Operation op;

    // lower precedence implies a higher PEMDAS value (if that makes sense)
    // ex. parens should be of highest precedence. they will have the value of -1
    public OpNode(Operation op) {
        super(0, AttributeType.Boolean);
        this.op = op;
    }

    private int compare(Object leftVal, Object rightVal, AttributeType type) {
        switch(type) {
            case Integer:
                return ((Integer) leftVal).compareTo((Integer) rightVal);
            case Double:
                return ((Double) leftVal).compareTo((Double) rightVal);
            case Char:
            case Varchar:
                return leftVal.toString().compareTo(rightVal.toString());
            case Boolean:
                return ((Boolean) leftVal).compareTo((Boolean) rightVal);
            default: // this should never happen
                return 0;
        }
    }

    // this saves a messy compare later
    private boolean isStringType(AttributeType type) {
        return type == AttributeType.Char || type == AttributeType.Varchar;
    }

    @Override
    public ValNode getLeft() {
        return (ValNode) super.getLeft();
    }

    @Override
    public ValNode getRight() {
        return (ValNode) super.getRight();
    }

    @Override
    public boolean validate(ArrayList<Object> columns) throws Exception {
        Object leftVal = this.getLeft() instanceof VarNode ? columns.get(((VarNode) this.getLeft()).index) : this.getLeft().getValue();
        Object rightVal = this.getRight() instanceof VarNode ? columns.get(((VarNode) this.getRight()).index) : this.getRight().getValue();

        if(leftVal == null || rightVal == null)
            return false;

        if(!isStringType(this.getLeft().type) || !isStringType(this.getRight().type))
            if(!this.getLeft().type.equals(this.getRight().type))
                throw new Exception("Trying to compare invalid types: " + 
                        this.getLeft().type + ' ' + this.getRight().type);

        AttributeType type = getLeft().type;

        switch(this.op){
            case Eql:
                return compare(leftVal, rightVal, type) == 0;
            case NotEql:
                return compare(leftVal, rightVal, type) != 0;
            case Greater:
                return compare(leftVal, rightVal, type) > 0;
            case GreaterEql:
                return compare(leftVal, rightVal, type) >= 0;
            case Less:
                return compare(leftVal, rightVal, type) < 0;
            case LessEql:
                return compare(leftVal, rightVal, type) <= 0;
            default:
                throw new Exception("reached supposedly unreachable code in validate");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        switch(this.op) {
            case Eql:
                sb.append("=");
                break;
            case NotEql:
                sb.append("!=");
                break;
            case Greater:
                sb.append(">");
                break;
            case GreaterEql:
                sb.append(">=");
                break;
            case Less:
                sb.append("<");
                break;
            case LessEql:
                sb.append("<=");
                break;
        }

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
