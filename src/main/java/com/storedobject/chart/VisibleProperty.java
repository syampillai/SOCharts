package com.storedobject.chart;

/**
 * Represents a property that can be shown or hidden.
 *
 * @author Syam
 */
public interface VisibleProperty {

    /**
     * Show this.
     */
    default void show() {
        setVisible(true);
    }

    /**
     * Hide this.
     */
    default void hide() {
        setVisible(false);
    }

    /**
     * Sets the visibility of this property.
     *
     * @param visible A boolean indicating whether this property should be visible (true)
     *                or hidden (false).
     */
    void setVisible(boolean visible);

    /**
     * Checks if this property is currently visible.
     *
     * @return true if the property is visible, false otherwise.
     */
    boolean isVisible();
}
