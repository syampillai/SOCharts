package com.storedobject.chart;

/**
 * Shape that partially defines an arc.
 * <p>Note: Angle increases in the clockwise direction and zero angle lies on the normal X-axis direction
 * (i.e., on the horizontal line).</p>
 *
 * @author Syam
 */
public abstract class ArcPart extends CirclePart{

    private Number startAngle, endAngle;
    private boolean clockwise;
    private Number holeRadius;

    /**
     * Constructor.
     *
     * @param radius radius.
     * @param startAngle Starting angle in degrees.
     * @param endAngle Ending angle in degrees.
     * @param clockwise Whether to draw clockwise from start angle to end angle or not.
     */
    public ArcPart(Number radius, Number startAngle, Number endAngle, boolean clockwise) {
        super(radius);
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.clockwise = clockwise;
    }

    @Override
    protected void encodeShape(StringBuilder sb) {
        super.encodeShape(sb);
        encode(sb, "r0", holeRadius);
        encode(sb, "startAngle", rad(startAngle));
        encode(sb, "endAngle", rad(endAngle));
        encode(sb, "clockwise", clockwise);
    }

    private static double rad(Number d) {
        if(d == null) {
            return 0;
        }
        return d.doubleValue() * Math.PI / 180.0;
    }

    /**
     * Get the starting angle.
     *
     * @return The starting angle.
     */
    public Number getStartAngle() {
        return startAngle;
    }

    /**
     * Set the starting angle.
     *
     * @param startAngle Starting angle in degrees.
     */
    public void setStartAngle(Number startAngle) {
        this.startAngle = startAngle;
    }

    /**
     * Get the ending angle.
     *
     * @return The ending angle.
     */
    public final Number getEndAngle() {
        return endAngle;
    }

    /**
     * Set the ending angle.
     *
     * @param endAngle Ending angle in degrees.
     */
    public void setEndAngle(Number endAngle) {
        this.endAngle = endAngle;
    }

    /**
     * See whether this will be drawn clockwise or not.
     *
     * @return  True for clockwise.
     */
    public final boolean isClockwise() {
        return clockwise;
    }

    /**
     * Set whether this should drawn clockwise or not.
     *
     * @param clockwise True for clockwise.
     */
    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    /**
     * Get radius of the hole.
     *
     * @return  Radius of the hole.
     */
    public final Number getHoleRadius() {
        return holeRadius;
    }

    /**
     * Set radius of the hole.
     *
     * @param holeRadius Radius of the hole.
     */
    public void setHoleRadius(Number holeRadius) {
        this.holeRadius = holeRadius;
    }
}
