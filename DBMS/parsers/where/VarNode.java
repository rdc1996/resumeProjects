package parsers.where;

import common.AttributeType;

public class VarNode extends ValNode {
    public final String attrName;
    // gets set to a real index when we know what index attrName is at
    public final int index;

    public VarNode(String attrName, AttributeType type, int index) {
        super(null, type);
        this.attrName = attrName;
        this.index = index;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.attrName);
        sb.append(':');
        sb.append(this.type);

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
