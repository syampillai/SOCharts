package com.storedobject.chart;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an event that occurs in a chart.
 * A {@code ChartEvent} encapsulates the type of the event, the ID of the part
 * of the chart related to the event, and optionally, any additional data related to the event.
 *
 * @author Syam
 */
public final class Event {

    private final SOChart soChart;
    private final EventType type;
    private final Object userData;
    private Map<String, Object> data;

    /**
     * Constructor.
     *
     * @param soChart The {@code SOChart} instance associated with the event.
     * @param type The type of the event as a {@code ChartEventType}.
     * @param userData The user-specific data associated with the event.
     */
    public Event(SOChart soChart, EventType type, Object userData) {
        this.soChart = soChart;
        this.type = type;
        this.userData = userData;
    }

    /**
     * Retrieves the {@code SOChart} instance associated with this chart event.
     *
     * @return The {@code SOChart} instance linked to this event.
     */
    public SOChart getSOChart() {
        return soChart;
    }

    /**
     * Retrieves the type of the chart event.
     *
     * @return The type of the chart event as a {@code ChartEventType}.
     */
    public EventType getType() {
        return type;
    }

    /**
     * Retrieves the additional data associated with the event.
     * <p>Note: The associated data depends on the type of event and event category.</p>
     *
     * @return The data associated with the event.
     */
    public Map<String, Object> getData() {
        if(this.data == null) {
            this.data = new HashMap<>();
        }
        return data;
    }

    /**
     * Adds a key-value pair to the additional data associated with the chart event.
     * If the data map is not initialized, it will create a new one before adding the entry.
     *
     * @param key The key representing the data entry.
     * @param data The value associated with the key.
     */
    public void addData(String key, Object data) {
        getData().put(key, data);
    }

    @Override
    public String toString() {
        return "Event(" + type + ", " + data + ")";
    }

    /**
     * Sets the additional data associated with the event.
     *
     * @param data The data to be associated with the event.
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * Retrieves the user-specific data associated with the event.
     *
     * @return The user-specific data as an {@code Object}.
     */
    public Object getUserData() {
        return userData;
    }
}
