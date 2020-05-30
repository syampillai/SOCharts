package com.storedobject.chart;

/**
 * Abstract representation of an axis.
 *
 * @param <T> Data type of the axis.
 * @author Syam
 */
public abstract class Axis<T> extends AbstractDisplayablePart implements ComponentPart {

    CoordinateSystem coordinateSystem;
    private final Class<T> dataType;

    /**
     * Constructor.
     *
     * @param dataType Data type.
     */
    public Axis(Class<T> dataType) {
        this.dataType = dataType;
    }

    /**
     * Get the type of the data.
     *
     * @return value type.
     */
    public final String getType() {
        return AbstractData.getType(dataType);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void validate() throws Exception{
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append("\"type\":\"").append(getType()).append('"');
        if(coordinateSystem != null) {
            sb.append(",\"").append(SOChart.encoderLabel(coordinateSystem));
            sb.append("Index\":").append(coordinateSystem.getSerial());
        }
    }
}