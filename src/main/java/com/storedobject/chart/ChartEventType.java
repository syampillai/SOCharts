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

    /**
     * Represents a double-click event type that can occur on a chart.
     */
    DoubleClick("dblclick", 0),
    ;

    private final String name;
    private final int category;

    ChartEventType() {
        this(null, 0);
    }

    ChartEventType(String name, int category) {
        this.name = name == null ? name().toLowerCase() : name;
        this.category = category; // 0 - Mouse
    }

    public String getName() {
        return name;
    }

    public int getCategory() {
        return category;
    }
}
