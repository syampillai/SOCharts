package com.storedobject.chart;

/**
 * Representation of X axis.
 *
 * @param <T> Data type.
 * @author Syam
 */
public class XAxis<T> extends Axis<T> {

    /**
     * Constructor.
     *
     * @param dataType Data type.
     */
    public XAxis(Class<T> dataType) {
        super(dataType);
    }
}
