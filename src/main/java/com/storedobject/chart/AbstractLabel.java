package com.storedobject.chart;

/**
 * Represents abstract base for the label used by other parts such as {@link Axis}, {@link Chart} etc.
 *
 * @author Syam
 */
public abstract class AbstractLabel extends TextStyle implements VisibleProperty {

    private boolean show = true;
    private int gap = Integer.MIN_VALUE;

    @Override
    public void setVisible(boolean visible) {
        show = visible;
    }

    @Override
    public boolean isVisible() {
        return show;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.addComma(sb);
        sb.append("\"show\":").append(show);
        if(gap > Integer.MIN_VALUE) {
            sb.append(",\"").append(getGapName()).append("\":").append(gap);
        }
    }

    /**
     * Name of the gap to be used when rendering (It is different when different parts are used).
     *
     * @return Name of the gap.
     */
    protected String getGapName() {
        return "margin";
    }

    /**
     * Set the gap between the part and labels.
     *
     * @return Gap in pixels.
     */
    public int getGap() {
        return gap;
    }

    /**
     * Set the gap between the part and labels.
     *
     * @param gap Gap in pixels.
     */
    public void setGap(int gap) {
        this.gap = gap;
    }
}
