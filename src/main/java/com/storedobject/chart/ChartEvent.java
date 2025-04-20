package com.storedobject.chart;

/**
 * Represents an event that occurs in a chart.
 * A {@code ChartEvent} encapsulates the type of the event, the ID of the part
 * of the chart related to the event, and optionally, any additional data related to the event.
 *
 * @author Syam
 */
public class ChartEvent {

    private final SOChart soChart;
    private final ChartEventType type;
    private final long partId;
    private Object data;

    /**
     * Constructs a new ChartEvent with the specified type and part ID.
     *
     * @param soChart The {@code SOChart} instance associated with this event.
     * @param type The type of the chart event. This determines the kind of event that occurred.
     * @param partId The ID of the chart part associated with this event.
     */
    public ChartEvent(SOChart soChart, ChartEventType type, long partId) {
        this.soChart = soChart;
        this.type = type;
        this.partId = partId;
    }

    /**
     * Retrieves the {@code SOChart} instance associated with this chart event.
     *
     * @return The {@code SOChart} instance linked to this event.
     */
    public SOChart getSoChart() {
        return soChart;
    }

    /**
     * Retrieves the type of the chart event.
     *
     * @return The type of the chart event as a {@code ChartEventType}.
     */
    public ChartEventType getType() {
        return type;
    }

    /**
     * Retrieves the ID of the part associated with the chart event.
     *
     * @return The ID of the part as a long value.
     */
    public long getPartId() {
        return partId;
    }

    /**
     * Retrieves the additional data associated with the event.
     *
     * @return The data associated with the event, or null if no data is set.
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the additional data associated with the chart event.
     *
     * @param data The data to associate with the event. This can be any object
     *             that provides additional information relevant to the event.
     */
    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Event(" + type + ", " + partId + ", " + data + ")";
    }
}
