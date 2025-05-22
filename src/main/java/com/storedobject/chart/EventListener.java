package com.storedobject.chart;

/**
 * Functional interface to listen to events occurring on a chart or its part.
 * <p>This interface should be implemented to handle {@link Event} instances, providing a mechanism to react to
 * various interactions or updates on the chart components.</p>
 *
 * @author Syam
 */
@FunctionalInterface
public interface EventListener {

    /**
     * Handles an event that occurs on a chart or its part. This method is called whenever a {@code ChartEvent} is triggered,
     * allowing the implementing class to respond to the event.
     *
     * @param event The chart event to handle. This provides details about the event, such as its type, the part
     *              of the chart the event pertains to, and any additional data associated with the event.
     */
    void onEvent(Event event);
}
