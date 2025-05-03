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
 * Heatmap chart. A heatmap chart is plotted on a {@link RectangularCoordinate} system.
 * <p>A {@link VisualMap} is automatically created unless you set it explicitly to <code>null</code> via
 * {@link #setVisualMap(VisualMap)}.</p>
 *
 * @author Syam
 */
public class HeatmapChart extends XYDataChart {

    /**
     * Constructor.
     *
     * @param xData Labels on the X-axis.
     * @param yData Labels on the Y-axis.
     */
    public HeatmapChart(CategoryData xData, CategoryData yData) {
        super(ChartType.Heatmap, xData, yData);
    }
}
