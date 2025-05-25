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

    /**
     * Represents a mouse-over event type that can occur on a chart.
     */
    MouseOver("mouseover", EventCategory.Mouse),

    Legend("legendselectchanged", EventCategory.Legend),
    ;

    private final String name;
    private final EventCategory category;

    /**
     * Default constructor for the EventType enum.
     * Assigns a default name of null and a default category of EventCategory.Mouse if not otherwise specified.
     */
    EventType() {
        this(null, EventCategory.Mouse);
    }

    /**
     * Constructs an instance of the EventType with the given name and category.
     *
     * @param name     The name of the event type. If null, the default name in lowercase is used.
     * @param category The category of the event type.
     */
    EventType(String name, EventCategory category) {
        this.name = name == null ? name().toLowerCase() : name;
        this.category = category;
    }

    /**
     * Gets the name associated with this event type.
     *
     * @return The name of the event type as a string.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the category associated with this event type.
     *
     * @return the event category corresponding to this event type.
     */
    public EventCategory getCategory() {
        return category;
    }
}
