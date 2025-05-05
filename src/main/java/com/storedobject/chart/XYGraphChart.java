package com.storedobject.chart;

/**
 * Represents an XY Graph chart that extends the base functionality of {@link XYChart}.
 * This chart uses a graph representation of the data for visualization.
 *
 * @author Syam
 */
public class XYGraphChart extends XYChart{

    private final GraphData<GraphData.ValueNode> graphData;
    private final AbstractDataProvider<?> xData;

    /**
     * Constructs an XYGraphChart, a specialized chart type that uses graphical
     * representation of data in an XY format. This chart integrates an X-axis data
     * provider and a graph-based data model for visualizing relationships between data points.
     *
     * @param xData The data provider for the X-axis, specifying the values that will be plotted horizontally.
     * @param graphData The graph-based data model containing nodes and their connections
     *                  representing the values to be visualized and their relationships.
     */
    public XYGraphChart(AbstractDataProvider<?> xData, GraphData<GraphData.ValueNode> graphData) {
        super(ChartType.XYGraph, xData, graphData.getNodeData(false));
        this.xData = xData;
        this.graphData = graphData;
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        if (graphData.isEmpty()) {
            throw new ChartException("Data not set for " + className());
        }
        getCoordinateSystem().getAxis(0).setData(xData);
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return graphData.getNodeData(false);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        graphData.encodeJSON(sb);
    }
}
