package com.storedobject.chart;

/**
 * Defines a polygon shape  that can be added to {@link SOChart}.
 *
 * @author Syam
 */
public class Polygon extends Polyline {

    /**
     * Constructor.
     *
     * @param points Points of the polygon.
     */
    public Polygon(Point... points) {
        super(points);
    }
}
