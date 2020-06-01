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

import java.util.Objects;

/**
 * Pie chart.
 * (Future versions will provide more chart-specific methods).
 *
 * @author Syam
 */
public class PieChart extends SelfPositioningChart {

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
