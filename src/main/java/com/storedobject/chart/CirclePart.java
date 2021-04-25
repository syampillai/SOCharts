package com.storedobject.chart;

/**
 * Shape that partially defines a circle.
 *
 * @author Syam
 */
public abstract class CirclePart extends Shape {

    private Point center;
    private Number radius;

    /**
     * Constructor.
     *
     * @param radius radius.
     */
    public CirclePart(Number radius) {
        this.radius = radius;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append("\"shape\":{");
        encodeShape(sb);
        sb.append('}');
    }

    @Override
    protected void encodeShape(StringBuilder sb) {
        super.encodeShape(sb);
        encodePoint(sb, "cx", "cy", center);
        encode(sb, "r", radius);
    }

    /**
     * Get center.
     *
     * @return Center.
     */
    public final Point getCenter() {
        return center;
    }

    /**
     * Set center.
     *
     * @param center Center.
     */
    public void setCenterX(Point center) {
        this.center = center;
    }

    /**
     * Get radius.
     *
     * @return Radius.
     */
    public final Number getRadius() {
        return radius;
    }

    /**
     * Set radius.
     *
     * @param radius Radius.
     */
    public void setRadius(Number radius) {
        this.radius = radius;
    }
}
