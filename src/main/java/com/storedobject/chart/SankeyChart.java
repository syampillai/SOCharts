/*
 *  Copyright 2019-2021 Syam Pillai
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.storedobject.chart;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Sankey chart.
 *
 * @author Syam
 */
public class SankeyChart extends SelfPositioningSpecialChart {

    private SankeyDataProvider data;
    private final SD sd;
    private boolean horizontal = true;
    private int width = 0, height = 0, nodeWidth = 0, nodeGap = 0;
    private Alignment nodeAlignment;
    private Label edgeLabel;
    private LineStyle lineStyle;

    /**
     * Create a tree chart of the set of provided data.
     *
     * @param data Data to be used.
     */
    public SankeyChart(SankeyDataProvider data) {
        super(ChartType.Sankey);
        this.data = data;
        super.setData(sd = new SD());
    }

    @Override
    public final void setData(AbstractDataProvider<?>... data) {
    }

    /**
     * Get the Sankey data associated with this chart.
     *
     * @return Sankey data.
     */
    public SankeyDataProvider getSankeyData() {
        return data;
    }

    /**
     * Set the Sankey data associated with this chart.
     *
     * @param data Sankey data.
     */
    public void setSankeyData(SankeyDataProvider data) {
        this.data = data;
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return sd;
    }

    /**
     * Set the orientation as vertical.
     */
    public final void setVertical() {
        horizontal = false;
    }

    /**
     * Set height.
     *
     * @param height Height.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Set width.
     *
     * @param width Width.
     */
    public final void setWidth(int width) {
        this.width = width;
    }

    /**
     * Set node width.
     *
     * @param nodeWidth The node width of rectangle in Sankey diagram.
     */
    public final void setNodeWidth(int nodeWidth) {
        this.nodeWidth = nodeWidth;
    }

    /**
     * Set node gap.
     *
     * @param nodeGap The gap between any two rectangles in each column of the Sankey diagram.
     */
    public final void setNodeGap(int nodeGap) {
        this.nodeGap = nodeGap;
    }

    /**
     * Set node alignment.
     *
     * @param nodeAlignment Controls the horizontal alignment of nodes in the diagram. When orientation is vertical,
     *                      it controls vertical alignment.
     */
    public final void setNodeAlignment(Alignment nodeAlignment) {
        this.nodeAlignment = nodeAlignment;
    }

    /**
     * Get the current node alignment. If <code>true</code> is passed as the parameter, a new alignment will be
     * created if it doesn't exist.
     *
     * @param create True/false.
     * @return Alignment.
     */
    public final Alignment getNodeAlignment(boolean create) {
        if(nodeAlignment == null && create) {
            nodeAlignment = new Alignment();
        }
        return nodeAlignment;
    }

    /**
     * Set the label for the edges.
     *
     * @param edgeLabel Edge label to set.
     */
    public final void setEdgeLabel(Label edgeLabel) {
        this.edgeLabel = edgeLabel;
    }

    /**
     * Get the label for the edges.
     *
     * @param create Pass <code>true</code> if it needs to be created if not exists.
     * @return Label.
     */
    public final Label getEdgeLabel(boolean create) {
        if(edgeLabel == null && create) {
            edgeLabel = new Label();
        }
        return edgeLabel;
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

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(!horizontal) {
            ComponentPart.encode(sb, "orient", "vertical");
        }
        ComponentPart.addComma(sb);
        sb.append("\"edges\":[");
        AtomicBoolean first = new AtomicBoolean(true);
        data.getEdges().forEach(e -> {
            if(first.get()) {
                first.set(false);
            } else {
                sb.append(',');
            }
            sb.append('{');
            e.encodeJSON(sb);
            sb.append('}');
        });
        sb.append(']');
        if(width > 0) {
            ComponentPart.encode(sb, "width", width);
        }
        if(height > 0) {
            ComponentPart.encode(sb, "height", height);
        }
        if(nodeWidth > 0) {
            ComponentPart.encode(sb, "nodeWidth", nodeWidth);
        }
        if(nodeGap > 0) {
            ComponentPart.encode(sb, "nodeGap", nodeGap);
        }
        if(nodeAlignment != null) {
            ComponentPart.encode(sb, "align", nodeAlignment);
        }
        if(edgeLabel != null) {
            ComponentPart.encode(sb, "edgeLabel", edgeLabel);
        }
        if(lineStyle != null) {
            ComponentPart.encode(sb, "lineStyle", lineStyle);
        }
    }

    private class SD implements AbstractDataProvider<Object>, InternalDataProvider {

        private int serial = -1;

        @Override
        public Stream<Object> stream() {
            return data.getNodes().map(o -> o);
        }

        @Override
        public void encode(StringBuilder sb, Object value) {
            sb.append('{');
            ((SankeyChart.Node)value).encodeJSON(sb);
            sb.append('}');
        }

        @Override
        public DataType getDataType() {
            return DataType.OBJECT;
        }

        @Override
        public void setSerial(int serial) {
            this.serial = serial;
        }

        @Override
        public int getSerial() {
            return serial;
        }

        @Override
        public void validate() throws ChartException {
            data.validate();
        }
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
            return Label.class;
        }
    }

    /**
     * Class that defines an edge between 2 Sankey nodes.
     *
     * @author Syam
     */
    public static class Edge extends ComposedPart {

        private final SankeyChart.Node from, to;
        private Number value;
        private LineStyle lineStyle;

        /**
         * Constructor.
         *
         * @param from Starting node.
         * @param to Ending node.
         * @param value The value of edge, which decides the width of edge..
         */
        public Edge(SankeyChart.Node from, SankeyChart.Node to, Number value) {
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
        public SankeyChart.Node getFrom() {
            return from;
        }


        /**
         * Get the ending node of the edge.
         *
         * @return Ending node.
         */
        public SankeyChart.Node getTo() {
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
            return Label.class;
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
