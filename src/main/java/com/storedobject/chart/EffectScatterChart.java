package com.storedobject.chart;

public class EffectScatterChart extends XYChart {

    public EffectScatterChart() {
        this(null, null);
    }

    public EffectScatterChart(AbstractData<?> xData, Data yData) {
        super(Type.EffectScatter, xData, yData);
    }
}
