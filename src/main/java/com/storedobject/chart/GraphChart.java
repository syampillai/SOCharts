package com.storedobject.chart;

public class GraphChart extends SelfPositioningChart {

    private final GraphData<GraphData.XYNode> graphData;

    public GraphChart(GraphData<GraphData.XYNode> graphData) {
        super(ChartType.Graph, false);
        this.graphData = graphData;
        super.setData(graphData.getNodeData(true));
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        if (graphData.isEmpty()) {
            throw new ChartException("Data not set for " + className());
        }
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return graphData.getNodeData(true);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        graphData.encodeJSON(sb);
    }
}
