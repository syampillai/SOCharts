package com.storedobject.chart;

/**
 * Defines Bezier curve that can be added to {@link SOChart}.
 *
 * @author Syam
 */
public class BezierCurve extends Shape {

    private Point from, to;
    private Point cp1, cp2;

    /**
     * Construct a quadratic Bezier curve.
     *
     * @param from Point from.
     * @param to Point to.
     * @param controlPoint Control point.
     */
    public BezierCurve(Point from, Point to, Point controlPoint) {
        this(from, to, controlPoint, null);
    }

    /**
     * Construct a cubic Bezier curve.
     *
     * @param from Point from.
     * @param to Point to.
     * @param controlPoint1 First control point.
     * @param controlPoint2 Second control point.
     */
    public BezierCurve(Point from, Point to, Point controlPoint1, Point controlPoint2) {
        this.from = from;
        this.to = to;
        this.cp1 = controlPoint1;
        this.cp2 = controlPoint2;
    }

    @Override
    protected final String getType() {
        return "bezierCurve";
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append("\"shape\":{");
        encodePoint(sb, "x1", "y1", from);
        encodePoint(sb, "x2", "y2", to);
        encodePoint(sb, "cpx1", "cpy1", cp1);
        encodePoint(sb, "cpx2", "cpy2", cp2);
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

    /**
     * Get the first control point for the cubic Bezier curve.
     *
     * @return First control point.
     */
    public final Point getControlPoint1() {
        return cp1;
    }

    /**
     * Set the first control point for the cubic Bezier curve.
     *
     * @param controlPoint First control point.
     */
    public void setControlPoint1(Point controlPoint) {
        this.cp1 = controlPoint;
    }

    /**
     * Get the second control point for the cubic Bezier curve.
     *
     * @return Second control point.
     */
    public final Point getControlPoint2() {
        return cp2;
    }

    /**
     * Set the second control point for the cubic Bezier curve. If this is set to null, the curve will become
     * quadratic.
     *
     * @param controlPoint Second control point.
     */
    public void setControlPoint2(Point controlPoint) {
        this.cp2 = controlPoint;
    }

    /**
     * Get the control point for the quadratic Bezier curve.
     *
     * @return Control point.
     */
    public final Point getControlPoint() {
        return cp1;
    }

    /**
     * Set the control point for the quadratic Bezier curve. If this is set to null, the curve will become
     * a straight line.
     *
     * @param controlPoint Second control point.
     */
    public void setControlPoint(Point controlPoint) {
        this.cp1 = controlPoint;
    }
}
