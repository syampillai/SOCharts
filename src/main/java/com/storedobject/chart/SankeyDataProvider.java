package com.storedobject.chart;

import java.util.stream.Stream;

/**
 * Data provider interface for the {@link SankeyChart}.
 *
 * @author Syam
 */
public interface SankeyDataProvider extends ComponentPart {

    /**
     * Get the nodes.
     *
     * @return Nodes as a stream.
     */
    Stream<SankeyDataProvider.Node> getNodes();

    /**
     * Get the edges.
     *
     * @return Edges as a stream.
     */
    Stream<SankeyDataProvider.Edge> getEdges();

    /**
     * Sankey data node class.
     *
     * @author Syam
     */
    class Node implements ComponentProperty {

        private final String name;
        private Number value;
        private int depth = -1;

        /**
         * Constructor.
         *
         * @param name Name of the node.
         */
        public Node(String name) {
            this.name = name;
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
            sb.append("{\"name\":").append(ComponentPart.escape(getName()));
            if(depth >= 0) {
                sb.append(",\"depth\":").append(depth);
            }
            if(value != null) {
                sb.append(",\"value\":").append(value);
            }
            sb.append('}');
        }
    }

    /**
     * Class that defines an edge between 2 Sankey nodes.
     *
     * @author Syam
     */
    class Edge implements ComponentProperty {

        private final Node from, to;
        private Number value;

        /**
         * Constructor.
         *
         * @param from Starting node.
         * @param to Ending node.
         * @param value The value of edge, which decides the width of edge..
         */
        public Edge(Node from, Node to, Number value) {
            this.from = from;
            this.to = to;
            this.value = value;
        }

        /**
         * Get the starting node of the edge.
         *
         * @return Starting node.
         */
        public Node getFrom() {
            return from;
        }


        /**
         * Get the ending node of the edge.
         *
         * @return Ending node.
         */
        public Node getTo() {
            return to;
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
            sb.append("{\"source\":").append(ComponentPart.escape(from.getName())).append(",\"target\":")
                    .append(ComponentPart.escape(to.getName())).append(",\"value\":").append(value).append('}');
        }
    }
}
