package com.storedobject.chart;

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
}
