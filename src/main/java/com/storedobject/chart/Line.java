package com.storedobject.chart;

/**
 * Defines a line segment that can be added to {@link SOChart}.
 *
 * @author Syam
 */
public class Line extends Shape {

    private Point from, to;

    /**
     * Constructor.
     *
     * @param from From point.
     * @param to To point.
     */
    public Line(Point from, Point to) {
        this.from = from;
        this.to = to;
    }

    @Override
    protected final String getType() {
        return "line";
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append("\"shape\":{");
        encodePoint(sb, "x1", "y1", from);
        encodePoint(sb, "x2", "y2", to);
        sb.append('}');
    }

    /**
     * Get the "from" point.
     *
     * @return From point.
     */
    public final Point getFrom() {
        return from;
    }

    /**
     * Set the "from" point.
     *
     * @param from From point.
     */
    public void setFrom(Point from) {
        this.from = from;
    }

    /**
     * Get the "to" point.
     *
     * @return To point.
     */
    public final Point getTo() {
        return to;
    }

    /**
     * Set the "to" point.
     *
     * @param to To point.
     */
    public void setTo(Point to) {
        this.to = to;
    }
}
