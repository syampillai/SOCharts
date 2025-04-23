package com.storedobject.chart;

import java.util.function.BiConsumer;

/**
 * Represents a common interface for objects that have a clickable functionality.
 *
 * @author Syam
 */
public interface Clickable {

    /**
     * Sets the clickable state of this object.
     *
     * @param clickable A boolean value indicating whether the object should be clickable or not.
     *                  If {@code true}, the object will be clickable; if {@code false}, it will not be clickable.
     */
    default void setClickable(boolean clickable) {
    }

    /**
     * Determines whether the current object is clickable.
     *
     * @return {@code true} if the object is clickable, {@code false} otherwise.
     */
    default boolean isClickable() {
        return false;
    }

    /**
     * Get the key name for the click event for encoding purposes.
     *
     * @return Key name.
     */
    default String getClickKeyName() {
        return "triggerEvent";
    }

    /**
     * Retrieves the clickable state for encoding purposes.
     *
     * @return {@code true} if the object is clickable, {@code false} otherwise.
     */
    default boolean getClickValue() {
        return isClickable();
    }

    /**
     * Matches the source of the provided {@code ChartEvent} with the current object,
     * if the object is an instance of {@code ComponentPart} and its ID matches the part ID of the event.
     * If the match is successful, the current object is added as a "source" entry in the event's data map.
     *
     * @param event The {@code ChartEvent} to be matched against the current object.
     *              It contains the part ID to compare and a data map for storing additional information.
     * @return {@code true} if the object matches the part ID in the event and is added as the source;
     *         {@code false} otherwise.
     */
    default boolean matchSource(ChartEvent event) {
        if(this instanceof ComponentPart cp && cp.getId() == event.getPartId()) {
            event.addData("source", this);
            return true;
        }
        return false;
    }

    /**
     * Matches the source of the provided {@code ChartEvent} against the components specified in the {@code parts} array.
     * If the event matches any of the parts and the matched component is a {@code Clickable} instance,
     * the match is recorded in the event's data map.
     *
     * @param event The {@code ChartEvent} to be matched against the provided components.
     *              It contains the part ID and additional data map for storing match information.
     * @param parts An array of {@code ComponentPart} instances to compare against the event's part ID.
     * @return {@code true} if any component in the {@code parts} array matches the part ID in the event;
     *         {@code false} otherwise.
     */
    static boolean matchSource(ChartEvent event, ComponentPart... parts) {
        return matchSource(event, null, parts);
    }

    /**
     * Matches the source of the provided {@code ChartEvent} with any of the given {@code ComponentPart} objects.
     * If a match is found, an optional customizer action can be executed on the matching part and event.
     *
     * @param event The {@code ChartEvent} to match against the provided {@code ComponentPart} objects.
     *              It contains details about the chart event, such as the part ID to match and additional data.
     * @param customizer An optional {@code BiConsumer} that accepts a {@code ComponentPart} and the {@code ChartEvent}.
     *                   If provided, this function is executed when a match is found.
     * @param parts An array of {@code ComponentPart} objects to check for a match with the given {@code ChartEvent}.
     *              These parts may implement clickable behavior to perform the matching.
     * @return {@code true} if any part in the array matches the source of the given {@code ChartEvent},
     *         {@code false} otherwise.
     */
    static boolean matchSource(ChartEvent event, BiConsumer<ComponentPart, ChartEvent> customizer, ComponentPart... parts) {
        for(ComponentPart part: parts) {
            if(part instanceof Clickable c && c.matchSource(event)) {
                if(customizer != null) {
                    customizer.accept(part, event);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the clickable state for a variable number of {@code ComponentPart} objects,
     * provided they are instances of {@code Clickable}.
     *
     * @param clickable A boolean value indicating whether the specified parts should be clickable.
     *                  If {@code true}, the parts will be set as clickable;
     *                  if {@code false}, the parts will not be clickable.
     * @param parts     An array of {@code ComponentPart} objects whose clickable state should be updated.
     *                  Components that are not instances of {@code Clickable} will be ignored.
     */
    static void setClickable(boolean clickable, ComponentPart... parts) {
        for(ComponentPart part: parts) {
            if(part instanceof Clickable c) {
                c.setClickable(clickable);
            }
        }
    }
}
