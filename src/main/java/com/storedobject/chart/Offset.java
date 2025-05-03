package com.storedobject.chart;

/**
 * Represents an "offset" configuration for a component, allowing the specification of
 * x and y offset values. This class implements {@link ComponentProperty} and provides functionality to encode the
 * offset values into a JSON-compatible format for client-side usage.
 *
 * @author Syam
 */
public class Offset implements ComponentProperty {

    private int x = 0, y = 0;

    @Override
    public void encodeJSON(StringBuilder sb) {
        encodeJSON("offset", sb);
    }

    public void encodeJSON(String name, StringBuilder sb) {
        String s = "[" + (x < 0 ? ("'" + -x + "%'") : x)
                + "," + (y < 0 ? ("'" + -y + "%'") : y) + "]";
        ComponentPart.encode(sb, name, s);
    }

    /**
     * Sets the x and y offset values for this object based on the given {@link Size} parameters.
     * Only values of type {@link Size#pixels(int)} and {@link Size#percentage(int)} are accepted.
     *
     * @param x The x offset value as a {@link Size} object. Null values are ignored.
     * @param y The y offset value as a {@link Size} object. Null values are ignored.
     */
    public void set(Size x, Size y) {
        if(x != null && (x.get() > 0 || (x.get() < 0 && x.get() > -100))) {
            this.x = x.get();
        }
        if(y != null && (y.get() > 0 || (y.get() < 0 && y.get() > -100))) {
            this.y = y.get();
        }
    }
}
