package com.storedobject.chart;

import java.util.Objects;

/**
 * Pie chart.
 *
 * @author Syam
 */
public class PieChart extends AbstractChart {

    private Position position;
    int holeRadius = 0, radius = -75;

    /**
     * Constructor.
     */
    public PieChart() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param itemNames Item names of the slices.
     * @param values Values of the slices.
     */
    public PieChart(AbstractData<?> itemNames, Data values) {
        super(Type.Pie, itemNames, values);
    }

    /**
     * Set names of the slices.
     *
     * @param itemNames Item names of the slices.
     */
    public void setItemNames(AbstractData<?> itemNames) {
        setData(itemNames, 0);
    }

    /**
     * Set data for the slices.
     *
     * @param data Data.
     */
    public void setData(Data data) {
        setData(data, 1);
    }

    /**
     * Get position of this chart within the chart display area.
     *
     * @return Position.
     */
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * Set position of this chart within the chart display area.
     *
     * @param position Position.
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Set size of the radius.
     * @param radius Size of the radius.
     */
    public void setRadius(Size radius) {
        this.radius = radius.get();
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        String ir = new Size(holeRadius).encode(0);
        String or = new Size(radius).encode(-75);
        if(ir != null || or != null) {
            sb.append(",\"radius\":");
            if(ir != null) {
                sb.append('[').append(ir).append(',');
                sb.append(Objects.requireNonNullElse(or, "\"75%\""));
                sb.append(']');
            } else {
                sb.append(or);
            }
        }
    }
}
