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
 * Representation of rectangular (cartesian) coordinate system with X and Y axes. There could be one or more
 * X and Y axes.
 *
 * @author Syam
 */
public class RectangularCoordinate extends CoordinateSystem {

    private final List<XAxis<?>> xAxes = new ArrayList<>();
    private final List<YAxis<?>> yAxes = new ArrayList<>();

    /**
     * Constructor.
     */
    public RectangularCoordinate() {
    }

    /**
     * Constructor.
     *
     * @param xAxis X axis (primary).
     * @param yAxis Y axis (primary).
     */
    public RectangularCoordinate(XAxis<?> xAxis, YAxis<?> yAxis) {
        addXAxis(xAxis);
        addYAxis(yAxis);
    }

    /**
     * Get the X axes.
     *
     * @return X axis list.
     */
    public List<XAxis<?>> getXAxes() {
        return xAxes;
    }

    /**
     * Add a new X axis.
     *
     * @param xAxis X axis to add.
     */
    public void addXAxis(XAxis<?> xAxis) {
        if(xAxis != null) {
            this.xAxes.add(xAxis);
        }
    }

    /**
     * Get the Y axes.
     *
     * @return Y axis list.
     */
    public List<YAxis<?>> getYAxes() {
        return yAxes;
    }

    /**
     * Add a new Y axis.
     *
     * @param yAxis Y axis to add.
     */
    public void addYAxis(YAxis<?> yAxis) {
        if(yAxis != null) {
            this.yAxes.add(yAxis);
        }
    }

    @Override
    public void validate() throws Exception {
        if(xAxes.isEmpty()) {
            throw new Exception("X Axis not set");
        }
        if(yAxes.isEmpty()) {
            throw new Exception("Y Axis not set");
        }
        for(XAxis<?> xAxis: xAxes) {
            if (xAxis.coordinateSystem != null && xAxis.coordinateSystem != this) {
                throw new Exception("X Axis is used by some other coordinate system");
            }
            xAxis.coordinateSystem = this;
            xAxis.validate();
        }
        for(YAxis<?> yAxis: yAxes) {
            if (yAxis.coordinateSystem != null && yAxis.coordinateSystem != this) {
                throw new Exception("Y Axis is used by some other coordinate system");
            }
            yAxis.coordinateSystem = this;
            yAxis.validate();
        }
    }

    @Override
    public void addParts(SOChart soChart) {
        for(XAxis<?> xAxis: xAxes) {
            xAxis.coordinateSystem = this;
            soChart.addParts(xAxis);
        }
        for(YAxis<?> yAxis: yAxes) {
            yAxis.coordinateSystem = this;
            soChart.addParts(yAxis);
        }
        super.addParts(soChart);
    }
}
