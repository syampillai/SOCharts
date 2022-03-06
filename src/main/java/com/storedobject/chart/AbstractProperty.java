package com.storedobject.chart;

/**
 * Abstract base implementation of {@link ComponentProperty}.
 *
 * @author Syam
 */
public abstract class AbstractProperty implements ComponentProperty {

    protected boolean disabled;

    /**
     * Set it disabled.
     *
     * @param disabled True/false.
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        encodeStatus(sb);
        if(disabled) {
            return;
        }
        encodeProperty(sb);
    }

    /**
     * Encode the JSON for status for the disabled status.
     *
     * @param sb Buffer to which encoding to be embedded.
     */
    protected abstract void encodeStatus(StringBuilder sb);

    /**
     * Encode the JSON for this property.
     *
     * @param sb Buffer to which encoding to be embedded.
     */
    protected abstract void encodeProperty(StringBuilder sb);
}
