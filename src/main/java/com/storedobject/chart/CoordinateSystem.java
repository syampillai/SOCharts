package com.storedobject.chart;

import java.util.ArrayList;
import java.util.List;

public abstract class CoordinateSystem implements Component {

    private final List<Chart> charts = new ArrayList<>();
    private int serial;

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

    @Override
    public int getSerial() {
        return serial;
    }

    @Override
    public void setSerial(int serial) {
        this.serial = serial;
    }
}
