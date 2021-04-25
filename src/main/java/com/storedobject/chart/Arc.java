package com.storedobject.chart;

/**
 * Defines an arc shape that can be added to {@link SOChart}.
 * <p>Note: Angle increases in the clockwise direction and zero angle lies on the normal X-axis direction
 * (i.e., on the horizontal line).</p>
 *
 * @author Syam
 */
public class Arc extends ArcPart {

    /**
     * Constructor. An anticlockwise arc.
     *
     * @param radius     radius.
     * @param startAngle Starting angle in degrees.
     * @param endAngle   Ending angle in degrees.
     */
    public Arc(Number radius, Number startAngle, Number endAngle) {
        super(radius, startAngle, endAngle, false);
    }

    /**
     * Constructor.
     *
     * @param radius     radius.
     * @param startAngle Starting angle in degrees.
     * @param endAngle   Ending angle in degrees.
     * @param clockwise  Whether to draw clockwise from start angle to end angle or not.
     */
    public Arc(Number radius, Number startAngle, Number endAngle, boolean clockwise) {
        super(radius, startAngle, endAngle, clockwise);
    }

    @Override
    protected final String getType() {
        return "arc";
    }
}
