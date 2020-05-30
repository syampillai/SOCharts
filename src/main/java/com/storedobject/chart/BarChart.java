package com.storedobject.chart;

public class BarChart extends XYChart {

    public BarChart() {
        this(null, null);
    }

    public BarChart(AbstractData<?> xData, Data yData) {
        super(Type.Bar, xData, yData);
    }
}
