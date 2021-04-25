package com.storedobject.chart;

/**
 * Square shape class that can be added to {@link SOChart}.
 *
 * @author Syam
 */
public class Square extends Rectangle {

    /**
     * Constructor.
     *
     * @param side Side of the square.
     * @param borderRadius Border radius [top-left radius, top-right radius, bottom-right radius, bottom-left radius]
     */
    public Square(Number side, Number... borderRadius) {
        super(side, side, borderRadius);
    }

    /**
     * Set the side of the square.
     *
     * @param side Side.
     */
    public void setSide(Number side) {
        setWidth(side);
        setHeight(side);
    }
}
