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
 * Basic XY-type chart (mostly plotted on {@link RectangularCoordinate} system.
 *
 * @author Syam
 */
public abstract class XYChart extends AbstractChart {

    /**
     * Constructor.
     *
     * @param type Type.
     * @param xData Data for X axis.
     * @param yData Data for Y axis.
     */
    public XYChart(Type type, AbstractDataProvider<?> xData, DataProvider yData) {
        super(type, xData, yData);
    }

    /**
     * Set data for X axis.
     *
     * @param xData Data for X axis.
     */
    public void setXData(AbstractDataProvider<?> xData) {
        setData(xData, 0);
    }

    /**
     * Set data for Y axis.
     *
     * @param yData Data for Y axis.
     */
    public void setYData(DataProvider yData) {
        setData(yData, 1);
    }
}