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
 * Some charts can not encode chart data in a centralized way because its data require some special encoding. This
 * is the base class for such charts.
 *
 * @author Syam
 */
public class AbstractDataChart extends AbstractChart {

    boolean skippingData = false;

    /**
     * Create a chart of a given type and data.
     *
     * @param type type of the chart.
     * @param data Data to be used (multiples of them for charts that use multi-axis coordinate systems).
     */
    public AbstractDataChart(ChartType type, AbstractDataProvider<?>... data) {
        super(type, data);
    }

    @Override
    public final void skippingData(boolean skipping) {
        this.skippingData = skipping;
    }
}
