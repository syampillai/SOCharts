package com.storedobject.chart;

/**
 * Defines a sector shape that can be added to {@link SOChart}.
 * <p>Note: Angle increases in the clockwise direction and zero angle lies on the normal X-axis direction
 * (i.e., on the horizontal line).</p>
 *
 * @author Syam
 */
public class Sector extends ArcPart {

    /**
     * Constructor. A anticlockwise sector.
     *
     * @param radius     radius.
     * @param startAngle Starting angle in degrees.
     * @param endAngle   Ending angle in degrees.
     */
    public Sector(Number radius, Number startAngle, Number endAngle) {
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
    public Sector(Number radius, Number startAngle, Number endAngle, boolean clockwise) {
        super(radius, startAngle, endAngle, clockwise);
    }

    @Override
    protected final String getType() {
        return "sector";
    }
}
