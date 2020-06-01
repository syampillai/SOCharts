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

/**
 * Nightingale Rose chart.
 * (Future versions will provide more chart-specific methods).
 *
 * @author Syam
 */
public class NightingaleRoseChart extends DonutChart {

    private boolean percentage = true;

    /**
     * Constructor.
     */
    public NightingaleRoseChart() {
        holeRadius = -10;
    }

    /**
     * Constructor.
     *
     * @param itemNames Item names of the slices.
     * @param values Values of the slices.
     */
    public NightingaleRoseChart(AbstractData<?> itemNames, Data values) {
        super(itemNames, values);
        holeRadius = -10;
    }

    /**
     * Hide the percentage representation. In this case, the angles of the slices
     * will not represent the percentage values of the slices and values will be represented by the
     * size of the radius of each slice.
     */
    public void hidePercentage() {
        percentage = false;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append(",\"roseType\":\"");
        sb.append(percentage ? "radius" : "area");
        sb.append('"');
    }
}
