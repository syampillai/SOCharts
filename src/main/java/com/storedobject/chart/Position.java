package com.storedobject.chart;

/**
 * Representation of a position within the chart. Chart always occupies a rectangular part of the screen and the size of
 * this rectangle is determined by the methods used from Vaadin's {@link com.vaadin.flow.component.HasSize} on the
 * chart ({@link SOChart} implements {@link com.vaadin.flow.component.HasSize} interface). Using the same
 * standard used by CSS for absolute positioning, a part that supports positioning can use this class to define its
 * positioning requirements within the chart's boundary. Only just enough details need to be set for both horizontal
 * and vertical positions. For example: if "left" and "right" are specified, "width" will be automatically
 * computed. Each attribute can use absolute pixels values or percentage values. For example, left = 30% means
 * a horizontal position of 30% of the chart's width from the left edge of the chart.
 *
 * @author Syam
 */
public class Position implements ComponentPart {

    // Value: -200 = null
    // Value: -101 = "left", -102 = "center", -103 = "right" (only for left attribute).
    // Value: -111 = "top", -112 = "middle", -113 = "bottom" (only for top attribute).
    // Other negative values means percentage of its negated value.
    // 0 and positive values are for number of pixels.
    private int left = -200, right = -200, top = -200, bottom = -200, width = -200, height = -200;
    private int paddingTop = 5, paddingRight = 5, getPaddingBottom = 5, getPaddingLeft = 5;

    /**
     * Constructor.
     */
    public Position() {
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = Math.max(0, paddingTop);
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = Math.max(0, paddingRight);
    }

    public int getGetPaddingBottom() {
        return getPaddingBottom;
    }

    public void setGetPaddingBottom(int getPaddingBottom) {
        this.getPaddingBottom = Math.max(0, getPaddingBottom);
    }

    public int getGetPaddingLeft() {
        return getPaddingLeft;
    }

    public void setGetPaddingLeft(int getPaddingLeft) {
        this.getPaddingLeft = Math.max(0, getPaddingLeft);
    }

    private static String s(int v) {
        switch (v) {
            case -200:
                return null;
            case -101:
                return "left";
            case -102:
                return "center";
            case -103:
                return "right";
            case -111:
                return "top";
            case -112:
                return "middle";
            case -113:
                return "bottom";
        }
        if(v < 0) {
            return -v + "%";
        }
        return v + "px";
    }

    private static int p(int v) {
        return -Math.max(0, Math.min(100, v));
    }

    private static int v(int v) {
        return v < 0 ? -101 : v;
    }

    /**
     * Get the "left" attribute.
     *
     * @return The "Left" attribute.
     */
    public String getLeft() {
        return s(left);
    }

    /**
     * Set the "left" attribute in pixels.
     *
     * @param left The "left" attribute.
     */
    public void setLeft(int left) {
        this.left = v(left);
    }

    /**
     * Set the "left" attribute.
     *
     * @param left The "left" attribute.
     */
    public void setLeftAsPercentage(int left) {
        this.left = p(left);
    }

    /**
     * Get the "right" attribute.
     *
     * @return The "right" attribute.
     */
    public String getRight() {
        return s(right);
    }

    /**
     * Set the "right" attribute in pixels.
     *
     * @param right "right" attribute.
     */
    public void setRight(int right) {
        this.right = v(right);
    }

    /**
     * Set the "right" attribute.
     *
     * @param right "right" attribute.
     */
    public void setRightAsPercentage(int right) {
        this.right = p(right);
    }

    /**
     * Get the "top" attribute.
     *
     * @return The "top" attribute.
     */
    public String getTop() {
        return s(top);
    }

    /**
     * Set the "top" attribute in pixels.
     *
     * @param top The "top" attribute.
     */
    public void setTop(int top) {
        this.top = v(top);
    }

    /**
     * Set the "top" attribute.
     *
     * @param top The "top" attribute.
     */
    public void setTopAsPercentage(int top) {
        this.top = p(top);
    }

    /**
     * Get the "bottom" attribute.
     *
     * @return The "bottom" attribute.
     */
    public String getBottom() {
        return s(bottom);
    }

    /**
     * Set the "bottom" attribute in pixels.
     *
     * @param bottom The "bottom" attribute.
     */
    public void setBottom(int bottom) {
        this.bottom = v(bottom);
    }

    /**
     * Set the "bottom" attribute.
     *
     * @param bottom The "bottom" attribute.
     */
    public void setBottomAsPercentage(int bottom) {
        this.bottom = p(bottom);
    }

    /**
     * Get "width" attribute.
     *
     * @return The "width" attribute.
     */
    public String getWidth() {
        return s(width);
    }

    /**
     * Set the "width" attribute in pixels.
     *
     * @param width The "width" attribute.
     */
    public void setWidth(int width) {
        this.width = v(width);
    }

    /**
     * Set the "width" attribute.
     *
     * @param width The "width" attribute.
     */
    public void setWidthAsPercentage(int width) {
        this.width = p(width);
    }

    /**
     * Get "height" attribute.
     *
     * @return The "height" attribute.
     */
    public String getHeight() {
        return s(height);
    }

    /**
     * Set the "height" attribute in pixels.
     *
     * @param height The "height" attribute.
     */
    public void setHeight(int height) {
        this.height = v(height);
    }

    /**
     * Set the "height" attribute.
     *
     * @param height The "height" attribute.
     */
    public void setHeightAsPercentage(int height) {
        this.height = p(height);
    }

    /**
     * Reset value of "left" attribute.
     */
    public void resetLeft() {
        left = -200;
    }

    /**
     * Reset value of "right" attribute.
     */
    public void resetRight() {
        right = -200;
    }

    /**
     * Reset value of "top" attribute.
     */
    public void resetTop() {
        top = -200;
    }

    /**
     * Reset value of "bottom" attribute.
     */
    public void resetBottom() {
        bottom = -200;
    }

    /**
     * Reset value of "width" attribute.
     */
    public void resetWidth() {
        width = -200;
    }

    /**
     * Reset value of "height" attribute.
     */
    public void resetHeight() {
        height = -200;
    }

    /**
     * Justify to the left side (horizontal).
     */
    public void justifyLeft() {
        left = -101;
        width = right = -200;
    }

    /**
     * Justify to the center (horizontal).
     */
    public void justifyCenter() {
        left = -102;
        width = right = -200;
    }

    /**
     * Justify to the right side (horizontal).
     */
    public void justifyRight() {
        left = -103;
        width = right = -200;
    }

    /**
     * Align to the top side (vertical).
     */
    public void alignTop() {
        top = -111;
        height = bottom = -200;
    }

    /**
     * Align to the center (vertical).
     */
    public void alignCenter() {
        top = -112;
        height = bottom = -200;
    }

    /**
     * Align to the bottom side (vertical).
     */
    public void alignBottom() {
        top = -113;
        height = bottom = -200;
    }

    /**
     * Center it.
     */
    public void center() {
        alignCenter();
        justifyCenter();
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        boolean comma;
        comma = stuff(sb, "left", left, false);
        comma = stuff(sb, "right", right, comma);
        comma = stuff(sb, "width", width, comma);
        comma = stuff(sb, "top", top, comma);
        comma = stuff(sb, "bottom", bottom, comma);
        comma = stuff(sb, "height", height, comma);
        if(paddingTop != 5 || paddingRight != 5 || getPaddingBottom != 5 || getPaddingLeft != 5) {
            if(comma) {
                sb.append(',');
            }
            sb.append("\"padding\":");
            if(paddingTop == getPaddingBottom && getPaddingLeft == paddingRight) {
                if(paddingTop == getPaddingLeft) {
                    sb.append(paddingTop);
                } else {
                    sb.append('[').append(paddingTop).append(',').append(getPaddingLeft).append(']');
                }
            } else {
                sb.append('[').append(paddingTop).append(',').append(paddingRight).append(',').
                        append(getPaddingBottom).append(',').append(getPaddingLeft).append(']');
            }
        }
    }

    private static boolean stuff(StringBuilder sb, String attribute, int value, boolean comma) {
        if(value == -200) {
            return comma;
        }
        if(comma) {
            sb.append(',');
        }
        sb.append('"').append(attribute).append("\":");
        if(value >= 0) {
            sb.append(value);
        } else {
            sb.append('"').append(s(value)).append('"');
        }
        return true;
    }

    @Override
    public void validate() {
    }
}
