package com.storedobject.chart;

/**
 * Representation of radius axis.
 *
 * @param <T> Data type.
 * @author Syam
 */
public class RadiusAxis<T> extends Axis<T> {

    /**
     * Constructor.
     *
     * @param dataType Data type.
     */
    public RadiusAxis(Class<T> dataType) {
        super(dataType);
    }
}
