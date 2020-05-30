package com.storedobject.chart;

/**
 * Representation of angle axis.
 *
 * @param <T> Data type.
 * @author Syam
 */
public class AngleAxis<T> extends Axis<T> {

    /**
     * Constructor.
     *
     * @param dataType Data type.
     */
    public AngleAxis(Class<T> dataType) {
        super(dataType);
    }
}
