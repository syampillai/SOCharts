package com.storedobject.chart;

/**
 * Represents the type of event that can occur on a chart / part.
 *
 * @author Syam
 */
public enum EventType {

    /**
     * Represents a "blank area click event type" that occurs when a blank area within the chart is clicked.
     */
    BlankAreaClick("click", EventCategory.BlankArea),

    /**
     * Represents a click event type that can occur on a chart.
     */
    Click,

    /**
     * Represents a double-click event type that can occur on a chart.
     */
    DoubleClick("dblclick", EventCategory.Mouse),

    Legend("legendselectchanged", EventCategory.Legend),

    ;

    private final String name;
    private final EventCategory category;

    EventType() {
        this(null, EventCategory.Mouse);
    }

    EventType(String name, EventCategory category) {
        this.name = name == null ? name().toLowerCase() : name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public EventCategory getCategory() {
        return category;
    }
}
