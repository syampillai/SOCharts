package com.storedobject.chart;

/**
 * Rectangular shape class that can be added to {@link SOChart}.
 *
 * @author Syam
 */
public class Rectangle extends Shape {

    private Number x, y, width, height;
    private Number[] borderRadius;

    /**
     * Constructor.
     *
     * @param width Width.
     * @param height Height.
     * @param borderRadius Border radius [top-left radius, top-right radius, bottom-right radius, bottom-left radius]
     */
    public Rectangle(Number width, Number height, Number... borderRadius) {
        this.width = width;
        this.height = height;
        this.borderRadius = borderRadius;
    }

    @Override
    protected final String getType() {
        return "rect";
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append("\"shape\":{");
        encode(sb, "x", x);
        encode(sb, "y", y);
        encode(sb, "width", width);
        encode(sb, "height", height);
        encode(sb, "r", borderRadius);
        sb.append('}');
    }

    /**
     * Get X value of the top-left corner.
     *
     * @return X value.
     */
    public final Number getX() {
        return x;
    }

    /**
     * Set X value of the top-left corner.
     *
     * @param x X value.
     */
    public void setX(Number x) {
        this.x = x;
    }

    /**
     * Get Y value of the top-left corner.
     *
     * @return Y value.
     */
    public final Number getY() {
        return y;
    }

    /**
     * Set Y value of the top-left corner.
     *
     * @param y Y value.
     */
    public void setY(Number y) {
        this.y = y;
    }

    /**
     * Get width.
     *
     * @return Width.
     */
    public final Number getWidth() {
        return width;
    }

    /**
     * Set width.
     *
     * @param width Width.
     */
    public void setWidth(Number width) {
        this.width = width;
    }

    /**
     * Get height.
     *
     * @return Height.
     */
    public final Number getHeight() {
        return height;
    }

    /**
     * Seth height.
     *
     * @param height Height.
     */
    public void setHeight(Number height) {
        this.height = height;
    }

    /**
     * Get border radius.
     *
     * @return Border radius.
     */
    public final Number[] getBorderRadius() {
        return borderRadius;
    }

    /**
     * <p>Set border radius. This may be provided in one of the following ways:</p>
     * <pre>
     *     [] or null => No borders
     *     [1] => [1, 1, 1, 1]
     *     [1, 2] => [1, 2, 1, 2]
     *     [1, 2, 3] => [1, 2, 3, 2]
     *     [1, 2, 3, 4] => [1, 2, 3, 4]
     * </pre>
     *
     * @param borderRadius Border radius [top-left radius, top-right radius, bottom-right radius, bottom-left radius]
     */
    public void setBorderRadius(Number... borderRadius) {
        this.borderRadius = borderRadius;
    }
}
