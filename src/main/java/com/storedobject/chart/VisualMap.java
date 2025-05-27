package com.storedobject.chart;

import com.storedobject.helper.ID;

/**
 * Define features to visually highlight portions of the chart.
 * Note: Beta version, not fully tested and the API is not yet finalized.
 *
 * @author Syam
 */
public class VisualMap implements Component, HasPosition {

    private int serial;
    private final long id = ID.newID();
	private boolean continuous = true;
	private boolean calculable = true;
	private boolean vertical = false;
	private boolean show = true;
    private Chart chart;
    private Position position = new Position();
	private Number min;
	private Number max;
	private InRange inRange;

    /**
     * Constructor.
     */
    public VisualMap() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param chart Chart for which this map will be used.
     */
    public VisualMap(Chart chart) {
        this.chart = chart;
        position.alignBottom();
        position.justifyCenter();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setSerial(int serial) {
        this.serial = serial;
    }

    @Override
    public int getSerial() {
        return serial;
    }

    @Override
    public void validate() throws ChartException {
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        ComponentPart.encode(sb, "id", id);
        ComponentPart.encode(sb, "calculable", calculable);
        ComponentPart.encode(sb, "type", continuous ? "continuous" : "piecewise");
        if(chart != null) {
            ComponentPart.encode(sb, "seriesIndex", chart.getSerial());
            if(min == null) {
                ComponentPart.encode(sb, "min", chart.getMin());
            }
            if(max == null) {
                ComponentPart.encode(sb, "max", chart.getMax());
            }
        }
        if(min != null) {
            ComponentPart.encode(sb, "min", min);
        }
		if (max != null) {
            ComponentPart.encode(sb, "max", max);
        }
		ComponentPart.encode(sb, "orient", vertical ? "vertical" : "horizontal");
		ComponentPart.encode(sb, null, position);
		ComponentPart.encode(sb, "show", show);
		ComponentPart.encode(sb, "inRange", inRange);
    }

    /**
     * Is the rendering continuous?
     *
     * @return True/false.
     */
    public final boolean isContinuous() {
        return continuous;
    }

    /**
     * Set the rendering continuous. (By default, it is continuous).
     *
     * @param continuous True/false.
     */
    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    /**
     * Is the values calculable?
     *
     * @return True/false.
     */
    public final boolean isCalculable() {
        return calculable;
    }

    /**
     * Set that the value is calculable or not.
     *
     * @param calculable True/false.
     */
    public void setCalculable(boolean calculable) {
        this.calculable = calculable;
    }

    /**
     * Get the chart for which this visual map is used. (It could be null and in that case, it will be used for all
     * the charts).
     *
     * @return Chart.
     */
    public final Chart getChart() {
        return chart;
    }

    /**
     * Set the chart for which this visual map is used. (If it is null, it will be used for all
     * the charts).
     *
     * @param chart Chart.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * Is the orientation vertical?
     *
     * @return True/false.
     */
    public boolean isVertical() {
        return vertical;
    }

    /**
     * Set vertical orientation for the visual map. (By default, it is horizontal).
     *
     * @param vertical True/false.
     */
    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    @Override
    public final Position getPosition(boolean create) {
        if(position == null && create) {
            position = new Position();
        }
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Get the minimum value to be used by this map.
     * <p>Note: This will return whatever value set via {@link #setMin(Number)}. If not set, it will try to
     * determine this value from the associated chart if possible when rendering occurs.</p>
     *
     * @return Minimum value.
     */
    public final Number getMin() {
        return min;
    }

    /**
     * Set the minimum value to be used by this map when rendering. If a non-null value is set via this method,
     * it will be used while rendering and no attempt is made to determine it from the associated chart if any.
     *
     * @param min Value to set.
     */
    public void setMin(Number min) {
        this.min = min;
    }

    /**
     * Get the maximum value to be used by this map.
     * <p>Note: This will return whatever value set via {@link #setMax(Number)}. If not set, it will try to
     * determine this value from the associated chart if possible when rendering occurs.</p>
     *
     * @return Maximum value.
     */
    public final Number getMax() {
        return max;
    }

    /**
     * Set the maximum value to be used by this map when rendering. If a non-null value is set via this method,
     * it will be used while rendering and no attempt is made to determine it from the associated chart if any.
     *
     * @param max Value to set.
     */
    public void setMax(Number max) {
        this.max = max;
    }

	public boolean getShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public InRange getInRange() {
		return inRange;
	}

	public void setInRange(InRange inRange) {
		this.inRange = inRange;
	}

}
