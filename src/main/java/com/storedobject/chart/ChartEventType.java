package com.storedobject.chart;

/**
 * Represents the type of event that can occur on a chart / part.
 *
 * @author Syam
 */
public enum ChartEventType {

    /**
     * Represents a click event type that can occur on a chart.
     */
    Click,

    DoubleClick("dblclick"),
    ;

    private final String name;

    ChartEventType() {
        this.name = name().toLowerCase();
    }

    ChartEventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
