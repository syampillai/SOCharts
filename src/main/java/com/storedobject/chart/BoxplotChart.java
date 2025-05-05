/*
 *  Copyright Syam Pillai
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
 * Boxplot chart.
 *
 * @author Syam
 */
public class BoxplotChart extends XYChart {

    private boolean horizontal = true;
    private final AbstractDataProvider<?> category;
    private final AbstractDataProvider<BoxplotData.Boxplot> data;

    /**
     * Constructs a new BoxplotChart with the specified category data for the X-axis and
     * boxplot data for the Y-axis.
     *
     * @param xData the categorical data to be used as the X-axis values
     * @param yData the boxplot data to be used as the Y-axis values, containing the statistical
     *              values necessary for the boxplot (lower, Q1, median, Q3, upper)
     */
    public BoxplotChart(CategoryData xData, AbstractDataProvider<BoxplotData.Boxplot> yData) {
        super(ChartType.Boxplot, xData, yData);
        this.category = xData;
        this.data = yData;
    }

    /**
     * Constructs a BoxplotChart with the specified data for the X and Y axes.
     *
     * @param xData Data provider for the X-axis. This should provide Boxplot data represented as
     *              {@link BoxplotData.Boxplot}.
     * @param yData Data provider for the Y-axis. This should be categorized data represented as {@link CategoryData}.
     */
    public BoxplotChart(AbstractDataProvider<BoxplotData.Boxplot> xData, CategoryData yData) {
        super(ChartType.Boxplot, xData, yData);
        horizontal = false;
        this.category = yData;
        this.data = xData;
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return data;
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        Axis axis = getCoordinateSystem().getAxis(horizontal ? 0 : 1);
        if(axis.getDataType() != DataType.CATEGORY) {
            throw new ChartException((horizontal ? "X" : "Y") + "-axis must be a category axis");
        }
        axis.setData(category);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.encode(sb, "layout", horizontal ? "horizontal" : "vertical");
    }
}
