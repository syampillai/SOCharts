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
 * Scatter chart.
 * (Future versions will provide more chart-specific methods).
 *
 * @author Syam
 */
public class ScatterChart extends XYChart {

    /**
     * Constructor. (Data can be set later).
     */
    public ScatterChart() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param xData Data for X axis.
     * @param yData Data for Y axis.
     */
    public ScatterChart(AbstractData<?> xData, Data yData) {
        super(Type.Scatter, xData, yData);
    }
}
