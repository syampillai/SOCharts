package com.storedobject.chart;


public class Edge extends LineStyle {
    private Node sourceNode;
    private Node destinationNode;
    private Object info;

    public Edge(Node sourceNode, Node destinationNode) {
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
        this.info = null;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        sb.append('{');
        super.encodeJSON(sb);
        ComponentPart.encode(sb, "source", this.sourceNode.getName());
        ComponentPart.encode(sb, "target", this.destinationNode.getName());
        if (this.info != null) {
            sb.append(",\"tooltip\":{");
            ComponentPart.encode(sb, "formatter", this.getInfo().formatter().getElement().getOuterHTML());
            sb.append('}');
        }
        sb.append('}');
    }

    public Node getSourceNode() {
        return this.sourceNode;
    }

    public Edge setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
        return this;
    }

    public Node getDestinationNode() {
        return this.destinationNode;
    }

    public Edge setDestinationNode(Node destinationNode) {
        this.destinationNode = destinationNode;
        return this;
    }
    public TooltipView getInfo() {
        return (TooltipView) this.info;
    }

    public Edge setInfo(Object info) {
        this.info = (TooltipView) info;
        return this;
    }
}
