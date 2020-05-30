package com.storedobject.chart;

public class LineChart extends XYChart {

    public LineChart() {
        this(null, null);
    }

    public LineChart(AbstractData<?> xData, Data yData) {
        super(Type.Line, xData, yData);
    }
}
