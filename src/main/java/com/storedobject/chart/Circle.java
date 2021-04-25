package com.storedobject.chart;

/**
 * Circle shape class that can be added to {@link SOChart}.
 *
 * @author Syam
 */
public class Circle extends CirclePart {

    /**
     * Constructor.
     *
     * @param radius radius.
     */
    public Circle(Number radius) {
        super(radius);
    }

    @Override
    protected final String getType() {
        return "circle";
    }
}
