package com.storedobject.chart;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A concrete implementation of {@link SankeyDataProvider} as an {@link ArrayList} of {@link Node}s. It detects
 * duplicate {@link Node}s and circular references in {@link Edge}s.
 *
 * @author Syam
 */
public class SankeyData extends AbstractData<SankeyData.Node> implements SankeyDataProvider {

    private final List<SankeyData.Edge> edges = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param nodes Nodes to be added.
     */
    public SankeyData(SankeyData.Node... nodes) {
        super(DataType.OBJECT, nodes);
    }

    /**
     * Add an {@link Edge}.
     *
     * @param edge {@link Edge} to be added. If the {@link Edge} contains {@link Node}s that are not part of this, it
     *                         will be automatically added.
     */
    public void addEdge(SankeyData.Edge edge) {
        SankeyData.Node from = edge.getFrom(), to = edge.getTo();
        if(from == null || to == null) {
            edges.add(edge);
            return;
        }
        if(stream().noneMatch(n -> n.getName().equals(from.getName()))) {
            add(from);
        }
        if(stream().noneMatch(n -> n.getName().equals(to.getName()))) {
            add(to);
        }
        edges.add(edge);
    }

    @Override
    public Stream<SankeyData.Node> getNodes() {
        return stream();
    }

    @Override
    public Stream<SankeyData.Edge> getEdges() {
        return edges.stream();
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        int i, j;
        String name;
        for(i = 0; i < size(); i++) {
            name = get(i).getName();
            for(j = i + 1; j < size(); j++) {
                if(get(j).getName().equals(name)) {
                    throw new ChartException("Duplicate node name - " + name);
                }
            }
        }
        for(SankeyData.Edge e: edges) {
            e.validate();
        }
        if(edges.isEmpty()) {
            return;
        }
        if(hasCircularReference()) {
            throw new ChartException("Circular edge detected");
        }
    }

    private boolean hasCircularReference() {
        Set<SankeyData.Node> visitedNodes = new HashSet<>();
        for (SankeyData.Edge edge : edges) {
            SankeyData.Node fromNode = edge.getFrom();
            if (!visitedNodes.contains(fromNode)) {
                if (hasCycle(fromNode, visitedNodes, new HashSet<>())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasCycle(SankeyData.Node node, Set<SankeyData.Node> visited, Set<SankeyData.Node> path) {
        visited.add(node);
        path.add(node);
        for (SankeyData.Edge edge : edges) {
            if (edge.getFrom() == node) {
                SankeyData.Node toNode = edge.getTo();
                if (!visited.contains(toNode)) {
                    if (hasCycle(toNode, visited, path)) {
                        return true;
                    }
                } else if (path.contains(toNode)) {
                    return true;
                }
            }
        }
        path.remove(node);
        return false;
    }


    /**
     * Sankey data node class.
     *
     * @author Syam
     */
    public static class Node extends ComposedPart {

        private final String name;
        private Number value;
        private int depth = -1;

        /**
         * Constructor.
         *
         * @param name Name of the node.
         */
        public Node(String name) {
            super(false, false, false, false, false, true, true, true);
            this.name = name == null ? "NULL" : name;
        }

        @Override
        protected boolean hasId() {
            return false;
        }

        /**
         * Get the name of the node.
         *
         * @return Name of the node.
         */
        public String getName() {
            return name;
        }

        /**
         * Set the value of the node.
         *
         * @param value The value of the node, which determines the height of the node in horizontal orientation or
         *              width in the vertical orientation.
         */
        public void setValue(Number value) {
            this.value = value;
        }

        /**
         * Set the depth of the node.
         *
         * @param depth The layer of the node, value starts from 0.
         */
        public void setDepth(int depth) {
            this.depth = depth;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            ComponentPart.encode(sb, "name", getName());
            if(depth >= 0) {
                ComponentPart.encode(sb, "depth", depth);
            }
            if(value != null) {
                ComponentPart.encode(sb, "value", value);
            }
        }

        @Override
        protected Class<? extends com.storedobject.chart.Label> getLabelClass() {
            return Chart.Label.class;
        }
    }

    /**
     * Class that defines an edge between 2 Sankey nodes.
     *
     * @author Syam
     */
    public static class Edge extends ComposedPart {

        private final SankeyData.Node from, to;
        private Number value;
        private LineStyle lineStyle;

        /**
         * Constructor.
         *
         * @param from Starting node.
         * @param to Ending node.
         * @param value The value of edge, which decides the width of edge..
         */
        public Edge(SankeyData.Node from, SankeyData.Node to, Number value) {
            super(false, false, false, false, true, true, true, false);
            this.from = from;
            this.to = to;
            this.value = value;
        }

        @Override
        protected boolean hasId() {
            return false;
        }

        /**
         * Get the starting node of the edge.
         *
         * @return Starting node.
         */
        public SankeyData.Node getFrom() {
            return from;
        }


        /**
         * Get the ending node of the edge.
         *
         * @return Ending node.
         */
        public SankeyData.Node getTo() {
            return to;
        }

        @Override
        public void validate() throws ChartException {
            if(from == null || to == null) {
                throw new ChartException("Invalid edge");
            }
        }

        /**
         * Set the value of the edge.
         *
         * @param value The value of edge, which decides the width of edge..
         */
        public void setValue(Number value) {
            this.value = value;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            ComponentPart.encode(sb, "source", from.getName());
            ComponentPart.encode(sb, "target", to.getName());
            ComponentPart.encode(sb, "value", value);
            if(lineStyle != null) {
                ComponentPart.encode(sb, "lineStyle", lineStyle);
            }
        }

        @Override
        protected Class<? extends com.storedobject.chart.Label> getLabelClass() {
            return Chart.Label.class;
        }

        @Override
        protected String getLabelTag() {
            return "edgeLabel";
        }

        /**
         * Set the line style.
         *
         * @param lineStyle Line style.
         */
        public final void setLineStyle(LineStyle lineStyle) {
            this.lineStyle = lineStyle;
        }

        /**
         * Get line style.
         *
         * @param create Pass <code>true</code> if it needs to be created if not exists.
         * @return Line style.
         */
        public final LineStyle getLineStyle(boolean create) {
            if(lineStyle == null && create) {
                lineStyle = new LineStyle();
            }
            return lineStyle;
        }
    }
}
