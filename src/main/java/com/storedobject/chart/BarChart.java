/*
 *  Copyright 2019-2021 Syam Pillai
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
 * Bar chart.
 * (Future versions will provide more chart-specific methods).
 *
 * @author Syam
 */
public class BarChart extends XYChart {

    private boolean roundCap;

    /**
     * Constructor. (Data can be set later).
     */
    public BarChart() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param xData Data for X axis.
     * @param yData Data for Y axis.
     */
    public BarChart(AbstractDataProvider<?> xData, AbstractDataProvider<?> yData) {
        super(ChartType.Bar, xData, yData);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.encode(sb,"roundCap", roundCap, true);
    }

    /**
     * Is round-cap feature set?
     *
     * @return True/false.
     */
    public final boolean isRoundCap() {
        return roundCap;
    }

    /**
     * Set round-cap feature. If set it to <code>true</code>, the ending portion of the bar will be rounded.
     * This will be effective only when plotting this chart on {@link PolarCoordinate}s.
     *
     * @param roundCap Round-cap feature.
     */
    public void setRoundCap(boolean roundCap) {
        this.roundCap = roundCap;
    }
}
