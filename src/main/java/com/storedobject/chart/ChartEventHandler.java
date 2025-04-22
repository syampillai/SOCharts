package com.storedobject.chart;

/**
 * Interface representing a handler for chart events. Implementing classes can define custom behavior
 * for handling events associated with chart components.
 *
 * @author Syam
 */
public interface ChartEventHandler {

    /**
     * Checks whether the given chart event meets certain conditions. If the conditions are met, the event data
     * may be set so that the event handler can take appropriate action when this event is dispatched to it.
     *
     * @param event The chart event to evaluate. It contains information such as the type
     *              of event, the ID of the associated chart part, and any related data.
     * @return true if the event satisfies the conditions, false otherwise.
     */
    boolean checkEvent(ChartEvent event);
}
