package com.storedobject.chart;

import java.util.function.Function;

/**
 * Represents base for the label used by other parts such as {@link Axis}, {@link Chart} etc.
 *
 * @author Syam
 */
public abstract class Label extends AbstractLabel {

    private int rotation = Integer.MIN_VALUE;
    private boolean inside = false;
    protected String formatter;
    protected Function<String, String> formatParser;

    /**
     * Constructor.
     */
    public Label() {
    }

    /**
     * Get the rotation of the label.
     *
     * @return Rotation in degrees.
     */
    public final int getRotation() {
        return rotation;
    }

    /**
     * Set the rotation of the label.
     *
     * @param rotation Rotation in degrees. (Must be between -90 and 90).
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    /**
     * Check if the label is drawn inside or outside of the part.
     *
     * @return True if inside.
     */
    public final boolean isInside() {
        return inside;
    }

    /**
     * Setting for drawing the label inside the part.
     *
     * @param inside True if inside.
     */
    public void setInside(boolean inside) {
        this.inside = inside;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.addComma(sb);
        sb.append("\"inside\":").append(inside);
        if(rotation >= -90 && rotation <= 90) {
            sb.append(",\"rotate\":").append(rotation);
        }
        if(formatter != null) {
            String f = formatParser == null ? formatter : formatParser.apply(formatter);
            if(f != null) {
                sb.append(",\"formatter\":").append(ComponentPart.escape(f));
            }
        }
    }

    /**
     * Get the label formatter currently set.
     *
     * @return Label formatter.
     */
    public final String getFormatter() {
        return formatter;
    }

    /**
     * Set the label formatter.
     *
     * @param formatter Label formatter to be set.
     */
    public abstract void setFormatter(String formatter);
}
