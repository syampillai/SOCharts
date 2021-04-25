package com.storedobject.chart;

/**
 * A ring shape (donut shape) that can be added to {@link SOChart}.
 *
 * @author Syam
 */
public class Ring extends CirclePart {

    private Number holeRadius;

    /**
     * Constructor.
     *
     * @param radius radius.
     */
    public Ring(Number radius, Number holeRadius) {
        super(radius);
        this.holeRadius = holeRadius;
    }

    @Override
    protected final String getType() {
        return "ring";
    }

    @Override
    protected void encodeShape(StringBuilder sb) {
        super.encodeShape(sb);
        encode(sb, "r0", holeRadius);
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
