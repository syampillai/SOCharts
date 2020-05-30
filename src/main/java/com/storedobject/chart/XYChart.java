package com.storedobject.chart;

public abstract class XYChart extends AbstractChart {

    public XYChart(Type type, AbstractData<?> xData, Data yData) {
        super(type, xData, yData);
    }

    public void setXData(AbstractData<?> xData) {
        setData(xData, 0);
    }

    public void setYData(Data yData) {
        setData(yData, 1);
    }
}