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

    private final Size left = new Size();
    private final Size right = new Size();
    private final Size top = new Size();
    private final Size bottom = new Size();
    private final Size width = new Size();
    private final Size height = new Size();
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

    /**
     * Set the "left" size.
     *
     * @param size Size.
     */
    public void setLeft(Size size) {
        this.left.set(size);
    }

    /**
     * Set the "right" size.
     *
     * @param size Size.
     */
    public void setRight(Size size) {
        this.right.set(size);
    }

    /**
     * Set the "top" size.
     *
     * @param size Size.
     */
    public void setTop(Size size) {
        this.top.set(size);
    }

    /**
     * Set the "bottom" size.
     *
     * @param size Size.
     */
    public void setBottom(Size size) {
        this.bottom.set(size);
    }

    /**
     * Set the "width".
     *
     * @param size Size.
     */
    public void setWidth(Size size) {
        this.width.set(size);
    }

    /**
     * Set the "height".
     *
     * @param size Size.
     */
    public void setHeight(Size size) {
        this.height.set(size);
    }

    /**
     * Justify to the left side (horizontal).
     */
    public void justifyLeft() {
        left.left();
    }

    /**
     * Justify to the center (horizontal).
     */
    public void justifyCenter() {
        left.center();
    }

    /**
     * Justify to the right side (horizontal).
     */
    public void justifyRight() {
        left.right();
    }

    /**
     * Align to the top side (vertical).
     */
    public void alignTop() {
        top.top();
    }

    /**
     * Align to the center (vertical).
     */
    public void alignCenter() {
        top.middle();
    }

    /**
     * Align to the bottom side (vertical).
     */
    public void alignBottom() {
        top.bottom();
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

    private static boolean stuff(StringBuilder sb, String attribute, Size value, boolean comma) {
        String size = value.encode();
        if(size == null) {
            return comma;
        }
        if(comma) {
            sb.append(',');
        }
        sb.append('"').append(attribute).append("\":").append(size);
        return true;
    }

    @Override
    public void validate() {
    }
}
