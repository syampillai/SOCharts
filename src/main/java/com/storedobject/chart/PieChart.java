package com.storedobject.chart;

public class PieChart extends AbstractChart {

    public PieChart() {
        this(null, null);
    }

    public PieChart(AbstractData<?> itemNames, Data values) {
        super(Type.Pie, itemNames, values);
    }

    public void setItemNames(AbstractData<?> itemNames) {
        setData(itemNames, 0);
    }

    public void setData(Data data) {
        setData(data, 1);
    }
}
