/*
 *  Copyright 2019-2020 Syam Pillai
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.storedobject.chart;

import java.util.ArrayList;
import java.util.List;

/**
 * Tooltip to display.
 * <p>In most cases, default tooltips are good enough. However, you can still create customized tooltips using
 * these methods: {@link #append(String)}, {@link #append(AbstractDataProvider)}, {@link #append(Chart)},
 * {@link #newline()}.</p>
 *
 * @author Syam
 */
public class Tooltip extends VisiblePart implements Component {

    /**
     * Type of tooltips.
     *
     * @author Syam
     */
    public enum Type {
        /**
         * Tooltip for items (data-points).
         */
        Item,
        /**
         * Tooltip for an axis.
         */
        Axis,
        /**
         * Tooltip for special cases.
         */
        None
    }
    private Type type = null;
    final List<Object> parts = new ArrayList<>();

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(type != null) {
            ComponentPart.encode(sb, "trigger", ("" + type).toLowerCase());
        }
        if(!parts.isEmpty()) {
            ComponentPart.addComma(sb);
            sb.append("\"formatter\":");
            sb.append("{\"functionP\":{");
            sb.append("\"body\":[");
            boolean first = true;
            for(Object p: parts) {
                if(first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                if(p instanceof String) {
                    String s = (String) p;
                    s = s.replace("\n", "<br>");
                    sb.append(ComponentPart.escape(s));
                } else if(p instanceof AbstractDataProvider) { // Data
                    sb.append(((AbstractDataProvider<?>)p).getSerial());
                } else { // Chart
                    Chart chart = (Chart) p;
                    AbstractDataProvider<?> d = chart.dataValue();
                    if(d != null) {
                        int dataIndex = chart.dataValueIndex();
                        if(dataIndex < 0) {
                            sb.append(d.getSerial());
                        } else {
                            sb.append('[').append(d.getSerial()).append(',').append(dataIndex).append(']');
                        }
                    }
                }
            }
            sb.append("]}}");
        }
    }

    @Override
    public void validate() {
    }

    /**
     * Get the type of this tooltip.
     *
     * @return Type.
     */
    public final Type getType() {
        return type;
    }

    /**
     * Set the type of this tooltip.
     *
     * @param type Type of the tooltip.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Add some text as part of the tooltip.
     *
     * @param part Part to be added.
     * @return Self reference.
     */
    public Tooltip append(String part) {
        if(part != null && !part.isBlank()) {
            if(parts.isEmpty()) {
                parts.add(part);
            } else {
                Object last = parts.get(parts.size() - 1);
                if(last instanceof String) {
                    parts.remove(parts.size() - 1);
                    append(last + part);
                } else {
                    parts.add(part);
                }
            }
        }
        return this;
    }

    /**
     * Add a new line to the tooltip.
     *
     * @return Self reference.
     */
    public Tooltip newline() {
        return append("<br>");
    }

    /**
     * Add a data point to the tooltip.
     *
     * @param data Data point will be taken from this data.
     * @return Self reference.
     */
    public Tooltip append(AbstractDataProvider<?> data) {
        parts.add(data);
        return this;
    }

    /**
     * Add a data point of a specific chart to the tooltip. Mostly, you can use {@link #append(AbstractDataProvider)}
     * to add data point but some special charts (such as {@link HeatmapChart}) have specialized internal data
     * and data points from such data can be added using this method.
     *
     * @param chart Chart from which data point should be taken.
     * @return Self reference.
     */
    public Tooltip append(Chart chart) {
        parts.add(chart);
        return this;
    }
}
