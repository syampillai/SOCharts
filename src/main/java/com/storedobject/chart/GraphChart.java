package com.storedobject.chart;

import java.util.stream.Stream;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;


public class GraphChart extends Chart {

    private final List<Node> nodes;
    private final NodeDataProvider nd;
    private int repulsion = 50;
    private int symbolSize = 10;
    private double gravity = 0.1;
    private double edgeLength = 30;
    private boolean roam = false;
    private boolean draggable = false;
    private boolean autoCurveness = true;
    private final String edgeStartSymbol = "circle";
    private final String edgeEndSymbol = "arrow";
    private final List<Node.Type> categories;
    private GraphLayout layout = GraphLayout.FORCE;

    public GraphChart() {
        super(ChartType.Graph);
        this.nodes = new ArrayList<>();
        this.nd = new NodeDataProvider();
        this.categories = new ArrayList<>();
        super.setData(nd);
    }

    public int getRepulsion() {
        return this.repulsion;
    }

    public GraphChart setRepulsion(int repulsion) {
        this.repulsion = repulsion;
        return this;
    }

    public double getGravity() {
        return this.gravity;
    }

    public GraphChart setLayout(GraphLayout layout) {
        this.layout = layout;
        return this;
    }

    public GraphLayout getLayout() {
        return this.layout;
    }

    public GraphChart setRoam(boolean roam) {
        this.roam = roam;
        return this;
    }

    public boolean isRoam() {
        return this.roam;
    }

    public GraphChart setAutoCurveness(boolean autoCurveness) {
        this.autoCurveness = autoCurveness;
        return this;
    }

    public boolean isAutoCurveness() {
        return this.autoCurveness;
    }

    public GraphChart setDraggable(Boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    public Boolean isDraggable() {
        return this.draggable;
    }

    public String getEdgeStartSymbol() {
        return this.edgeStartSymbol;
    }
    public String getEdgeEndSymbol() {
        return this.edgeEndSymbol;
    }

    public int getSymbolSize() {
        return this.symbolSize;
    }
    public GraphChart setSymbolSize(int size) {
        this.symbolSize = size;
        return this;
    }


    public GraphChart setGravity(double gravity) {
        this.gravity = gravity;
        return this;
    }

    public double getEdgeLength() {
        return edgeLength;
    }

    public GraphChart setEdgeLength(double edgeLength) {
        this.edgeLength = edgeLength;
        return this;
    }

    public GraphChart addNode(Node node) {
        this.nodes.add(node);
        for (Map.Entry<String, Runnable> eventListener : node.getEventListeners().entrySet()) {
            this.addEvent(new SOEvent(eventListener.getKey(), node.getName()), eventListener.getValue());
        }
        return this;
    }

    public GraphChart addCategory(Node.Type category) {
        this.categories.add(category);
        return this;
    }

    @Override
    public void validate() throws ChartException {
        if (this.nodes == null) {
            throw new ChartException("Data not set for " + className());
        }
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return nd;
    }


    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.addComma(sb);
        ComponentPart.encode(sb, "roam", this.isRoam());
        ComponentPart.encode(sb, "draggable", this.isDraggable());
        ComponentPart.encode(sb, "autoCurveness", this.isAutoCurveness());
        ComponentPart.encode(sb, "symbolSize", this.getSymbolSize());
        sb.append(String.format(",\"edgeSymbol\": [\"%s\", \"%s\"]", this.getEdgeStartSymbol(), this.getEdgeEndSymbol()));
        sb.append(",\"force\": {");
        ComponentPart.encode(sb, "repulsion", this.getRepulsion());
        ComponentPart.encode(sb, "gravity", this.getGravity());
        ComponentPart.encode(sb, "edgeLength", this.getEdgeLength());
        sb.append("}");
        this.encodeObjectList(sb,"links", this.nodes);
        this.encodeObjectList(sb, "categories", this.categories);
    }

    private void encodeObjectList(StringBuilder sb, String property, List<?> objects) {
        AtomicBoolean first = new AtomicBoolean(true);
        objects.forEach(object -> {
            if(first.get()) {
                first.set(false);
                sb.append(String.format(",\"%s\":[", property));
            } else {
                sb.append(',');
            }
            if (property.equals("links")) {
                ((Node) object).getEdges().forEach(edge -> {
                    edge.encodeJSON(sb);
                    sb.append(",");
                });
            } else {
                ((Node.Type) object).encodeJSON(sb);
                sb.append(",");
            }
            sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1, "");
        });
        if(!first.get()) {
            sb.append(']');
        }
    }


    private class NodeDataProvider extends BasicDataProvider<Node> {

        @Override
        public Stream<Node> stream() {
            return nodes.stream();
        }

        @Override
        public void encode(StringBuilder sb, Node value) {
            value.encodeJSON(sb);
        }
    }
}
