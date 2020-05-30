package com.storedobject.chart;

import java.util.ArrayList;
import java.util.List;

public abstract class CoordinateSystem extends AbstractDisplayablePart implements Component {

    private final List<Chart> charts = new ArrayList<>();

    public void add(Chart... charts) {
        if(charts != null) {
            for(Chart chart : charts) {
                this.charts.add(chart);
                chart.coordinateSystem = this;
            }
        }
    }

    public void remove(Chart... charts) {
        if(charts != null) {
            for(Chart chart : charts) {
                this.charts.remove(chart);
                chart.coordinateSystem = null;
            }
        }
    }

    @Override
    public void addParts(SOChart soChart) {
        for(Chart chart : charts) {
            soChart.addParts(chart);
            soChart.addParts(chart.getData());
        }
    }
}
