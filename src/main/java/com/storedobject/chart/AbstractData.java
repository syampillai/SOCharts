package com.storedobject.chart;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>Representation of data as a {@link java.util.List}. The type of data can be anything that can be used
 * for charting. In charting, we need to distinguish between "numeric", "date/time", "categories" and "logarithmic"
 * values types.
 *
 * @param <T> Data type.
 * @author Syam
 */
public class AbstractData<T> extends ArrayList<T> implements ComponentPart {

    private int index;
    private final Class<T> dataType;
    private String name;

    /**
     * Constructor.
     *
     * @param dataType Data type.
     * @param data Initial data to add
     */
    @SafeVarargs
    public AbstractData(Class<T> dataType, T... data) {
        this.dataType = dataType;
        if(data != null && data.length > 0) {
            addAll(Arrays.asList(data));
        }
    }

    public final Class<T> getDataType() {
        return dataType;
    }

    @Override
    public int getSerial() {
        return index;
    }

    @Override
    public void setSerial(int serial) {
        this.index = serial;
    }

    /**
     * Get the name of this data set.
     *
     * @return Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set a name for this data set.
     *
     * @param name Name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the type of the data.
     *
     * @return value type.
     */
    public final String getType() {
        return getType(dataType);
    }

    static String getType(Class<?> dataType) {
        if(Number.class.isAssignableFrom(dataType)) {
            return "value";
        }
        return "category";
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        sb.append("\"d").append(index).append("\":");
        append(sb);
    }

    void append(StringBuilder sb) {
        boolean numeric = Number.class.isAssignableFrom(dataType);
        sb.append("[");
        boolean first = true;
        for(T v: this) {
            if(first) {
                first = false;
            } else {
                sb.append(',');
            }
            if(numeric) {
                sb.append(v);
            } else {
                sb.append(ComponentPart.escape(v));
            }
        }
        sb.append("]");
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void validate() throws Exception {
    }
}
