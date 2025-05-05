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
 * Candlestick chart.
 *
 * @author Syam
 */
public class CandlestickChart extends XYChart {

    private final AbstractDataProvider<?> xData;
    private final AbstractDataProvider<CandlestickData.Candlestick> data;

    public CandlestickChart(AbstractDataProvider<?> xData, AbstractDataProvider<CandlestickData.Candlestick> yData) {
        super(ChartType.Candlestick, xData, yData);
        this.xData = xData;
        this.data = yData;
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return data;
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        Axis axis = getCoordinateSystem().getAxis(0);
        if(axis.getDataType() != DataType.CATEGORY) {
            throw new ChartException("X-axis must be a category axis");
        }
        axis.setData(xData);
    }
}
