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
 * Scatter chart. A scatter chart is plotted on a {@link RectangularCoordinate} system.
 *
 * @author Syam
 */
public class ScatterChart extends XYChart {

    private PointSymbol pointSymbol;

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
    public ScatterChart(AbstractDataProvider<?> xData, AbstractDataProvider<?> yData) {
        super(ChartType.Scatter, xData, yData);
    }


    @Override
    public void validate() throws ChartException {
        super.validate();
        if(coordinateSystem == null || !RectangularCoordinate.class.isAssignableFrom(coordinateSystem.getClass())) {
            throw new ChartException("Scatter chart must be plotted on a rectangular coordinate system");
        }
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(pointSymbol != null) {
            pointSymbol.encodeJSON(sb);
        }
    }

    /**
     * Get the {@link PointSymbol}.
     *
     * @param create Whether to create it if not exists or not.
     * @return  The instance of the current {@link PointSymbol} or newly created instance if requested.
     */
    public PointSymbol getPointSymbol(boolean create) {
        if(pointSymbol == null && create) {
            pointSymbol = new PointSymbol();
        }
        return pointSymbol;
    }

    /**
     * Set a different point-symbol.
     *
     * @param pointSymbol An instance of the {@link PointSymbol}.
     */
    public void setPointSymbol(PointSymbol pointSymbol) {
        this.pointSymbol = pointSymbol;
    }
}
