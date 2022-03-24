package com.storedobject.chart;

/**
 * Shape class that can be added to {@link SOChart}. Positioning of the {@link Shape} on the screen is relative
 * to its parent. The parent could be an instance of the {@link SOChart} if this was added to that instance directly or
 * it could be a {@link ShapeGroup} class if this was added to a {@link ShapeGroup} instance. Positioning can be done by
 * setting appropriate values on the {@link Position} instance of this shape or by setting shape-specific parameters
 * like center coordinate for the {@link CirclePart} shape. When both are specified, values set on the {@link Position}
 * will be used.
 * <p>Note: Unless otherwise specified, all measurements used in this class are in pixels. Sub-pixels are supported.</p>
 *
 * @author Syam
 */
public abstract class Shape extends AbstractPart implements Component, HasPosition {

    private static final Color DEFAULT_COLOR = new Color("black");
    private Position position;
    private Point positionXY;
    private Style style;
    private boolean draggable = false;
    private boolean show = true;

    public Shape() {
        getStyle(true).setFillColor(DEFAULT_COLOR);
        style.setStrokeColor(DEFAULT_COLOR);
    }

    /**
     * Get the type of this shape.
     *
     * @return Type of the shape.
     */
    protected abstract String getType();

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.encode(sb, "type", getType());
        if(positionXY != null) {
            encodePoint(sb, positionXY);
        }
        ComponentPart.encode(sb, "invisible", !show);
        ComponentPart.encode(sb, "draggable", draggable);
        if(style != null) {
            sb.append(",\"style\":{");
            style.encodeJSON(sb);
            sb.append('}');
        }
        sb.append(',');
    }

    /**
     * Helper method: Encode a (name, value) pair.
     *
     * @param sb Encoded JSON string to be appended to this.
     * @param name Name to be encoded.
     * @param value Value to be encoded.
     */
    protected static void encode(StringBuilder sb, String name, Object value) {
        if(value == null) {
            return;
        }
        if(value instanceof Object[] o) {
            Object oo;
            StringBuilder s = new StringBuilder();
            for(int i = 0; i < o.length; i++) {
                oo = o[i];
                s.append(i == 0 ? '[' : ',').append(oo == null ? "null" : ComponentPart.escape(oo));
            }
            if(s.length() == 0) {
                return;
            }
            value = s.append(']').toString();
            ComponentPart.addComma(sb);
            sb.append('"').append(name).append("\":").append(value);
            return;
        }
        ComponentPart.encode(sb, name, value);
    }

    /**
     * Helper method: Encode a {@link Point}.
     *
     * @param sb Encoded JSON string to be appended to this.
     * @param nameX Name to be encoded for X value.
     * @param nameY Name to be encoded for Y value.
     * @param point Point to be encoded.
     */
    protected static void encodePoint(StringBuilder sb, String nameX, String nameY, Point point) {
        if(point == null) {
            return;
        }
        encode(sb, nameX, point.x);
        encode(sb, nameY, point.y);
    }

    /**
     * Helper method: Encode a {@link Point}. Names of the X value and Y value will be "x" and "y" respectively.
     *
     * @param sb Encoded JSON string to be appended to this.
     * @param point Point to be encoded.
     */
    protected static void encodePoint(StringBuilder sb, Point point) {
        if(point == null) {
            return;
        }
        encode(sb, "x", point.x);
        encode(sb, "y", point.y);
    }

    /**
     * Encode shape details.
     *
     * @param sb Encoded JSON string to be appended to this.
     */
    protected void encodeShape(StringBuilder sb) {
    }

    @Override
    public void validate() throws ChartException {
    }

    /**
     * Show this shape.
     */
    public void show() {
        show = true;
    }

    /**
     * Hide this shape.
     */
    public void hide() {
        show = false;
    }

    /**
     * Whether this shape is draggable or not.
     *
     * @return True/false.
     */
    public final boolean isDraggable() {
        return draggable;
    }

    /**
     * Mark this shape as draggable.
     *
     * @param draggable True/false.
     */
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    /**
     * Get the position.
     *
     * @param create Whether to create it or not.
     * @return Position.
     */
    @Override
    public final Position getPosition(boolean create) {
        if(position == null && create) {
            position = new Position();
        }
        if(!create && positionXY != null) {
            return null;
        }
        return position;
    }

    /**
     * Set position.
     *
     * @param position Position to set.
     */
    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Set the X and Y positions of this shape in the coordinate system.
     * <p>Note: If X and Y positions are set this way, other position details set through
     * {@link #getPosition(boolean)} and/or {@link #setPosition(Position)} are ignored.</p>
     *
     * @param positionXY XY position in pixels.
     */
    public void setPosition(Point positionXY) {
        this.positionXY = positionXY;
    }

    /**
     * Set the X and Y positions of this shape in the coordinate system.
     * <p>Note: If X and Y positions are set this way, other position details set through
     * {@link #getPosition(boolean)} and/or {@link #setPosition(Position)} are ignored.</p>
     *
     * @param positionX X position in pixels.
     * @param positionY Y position in pixels.
     */
    public void setPosition(Number positionX, Number positionY) {
        this.positionXY = new Point(positionX, positionY);
    }

    /**
     * Get the style.
     *
     * @param create Whether to create it now or not.
     * @return Style.
     */
    public final Style getStyle(boolean create) {
        if(style == null && create) {
            style = new Style();
        }
        return style;
    }

    /**
     * Set style.
     *
     * @param style Style.
     */
    public void setStyle(Style style) {
        this.style = style;
    }

    /**
     * Class to specify the style of a {@link Shape} instance.
     *
     * @author Syam
     */
    public static class Style implements ComponentProperty {

        private AbstractColor fillColor, strokeColor;
        private Number lineWidth;
        private Shadow shadow;
        ComponentProperty extra; // Used by Text/Image class

        @Override
        public void encodeJSON(StringBuilder sb) {
            if(fillColor != null) {
                ComponentPart.encode(sb, "fill", fillColor.toString());
            }
            if(strokeColor != null) {
                ComponentPart.encode(sb, "stroke", strokeColor.toString());
            }
            ComponentPart.encode(sb, "lineWidth", lineWidth == null ? 1 : lineWidth);
            if(extra != null) {
                extra.encodeJSON(sb);
            }
            ComponentPart.encode(sb, null, shadow);
        }

        /**
         * Get the fill color.
         *
         * @return Fill color.
         */
        public final AbstractColor getFillColor() {
            return fillColor;
        }

        /**
         * Set the fill color.
         *
         * @param fillColor Fill color.
         */
        public void setFillColor(AbstractColor fillColor) {
            this.fillColor = fillColor;
        }

        /**
         * Get the stroke color.
         *
         * @return Stroke color.
         */
        public final AbstractColor getStrokeColor() {
            return strokeColor;
        }

        /**
         * Set the stroke color.
         *
         * @param strokeColor Stroke color.
         */
        public void setStrokeColor(AbstractColor strokeColor) {
            this.strokeColor = strokeColor;
        }

        /**
         * Get the line width.
         *
         * @return Line width.
         */
        public final Number getLineWidth() {
            return lineWidth;
        }

        /**
         * Set the line width.
         *
         * @param lineWidth Line width.
         */
        public void setLineWidth(Number lineWidth) {
            this.lineWidth = lineWidth;
        }

        /**
         * Get the shadow.
         *
         * @param create Whether to create if not exists or not.
         * @return Shadow.
         */
        public final Shadow getShadow(boolean create) {
            if(shadow == null && create) {
                shadow = new Shadow();
            }
            return shadow;
        }

        /**
         * Set the shadow.
         *
         * @param shadow Shadow.
         */
        public void setShadow(Shadow shadow) {
            this.shadow = shadow;
        }
    }

    public static class Point {

        Number x, y;

        public Point(Number x, Number y) {
            setX(x);
            setY(y);
        }

        public final Number getX() {
            return x;
        }

        public void setX(Number x) {
            this.x = x;
        }

        public final Number getY() {
            return y;
        }

        public void setY(Number y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "[" + (x == null ? "0" : x) + "," + (y == null ? "0" : y) + "]";
        }
    }
}
