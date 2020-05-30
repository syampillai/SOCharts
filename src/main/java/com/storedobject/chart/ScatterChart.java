package com.storedobject.chart;

public class ScatterChart extends XYChart {

    public ScatterChart() {
        this(null, null);
    }

    public ScatterChart(AbstractData<?> xData, Data yData) {
        super(Type.Scatter, xData, yData);
    }
}
