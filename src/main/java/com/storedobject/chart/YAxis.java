package com.storedobject.chart;

/**
 * Representation of Y axis.
 *
 * @param <T> Data type.
 * @author Syam
 */
public class YAxis<T> extends Axis<T> {

    /**
     * Constructor.
     *
     * @param dataType Data type.
     */
    public YAxis(Class<T> dataType) {
        super(dataType);
    }
}
