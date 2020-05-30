package com.storedobject.chart;

/**
 * Represents an abstract {@link ComponentPart} with some common base properties.
 *
 * @author Syam
 */
public abstract class AbstractDisplayablePart extends AbstractPart {

    private boolean show = true;

    /**
     * Show this part.
     */
    public void show() {
        show = true;
    }

    /**
     * Hide this part.
     */
    public void hide() {
        show = false;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append("\"show\":").append(show).append(',');
    }
}
