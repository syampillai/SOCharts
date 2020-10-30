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
 * Line chart.
 * (Future versions will provide more chart-specific methods).
 *
 * @author Syam
 */
public class LineChart extends XYChart {

    private Object smoothness;
    private Object stepped;
    private boolean connectNulls;
    private PointSymbol pointSymbol;

    /**
     * Constructor. (Data can be set later).
     */
    public LineChart() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param xData Data for X axis.
     * @param yData Data for Y axis.
     */
    public LineChart(AbstractDataProvider<?> xData, DataProvider yData) {
        super(ChartType.Line, xData, yData);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(smoothness != null) {
            sb.append(",\"smooth\":");
            if(smoothness instanceof Number) {
                sb.append(((Number)smoothness).intValue() / 100.0);
            } else {
                sb.append(smoothness);
            }
            smoothness = null;
        }
        if(stepped != null) {
            sb.append(",\"step\":").append(stepped);
            stepped = null;
        }
        if(pointSymbol != null) {
            pointSymbol.encodeJSON(sb);
        }
        ComponentPart.encode(sb,"connectNulls", connectNulls, true);
    }

    /**
     * Set the smoothness of the line.
     *
     * @param smoothness True or false.
     */
    public void setSmoothness(boolean smoothness) {
        this.smoothness = smoothness;
    }

    /**
     * Set the smoothness of the line as a percentage.
     *
     * @param percentage Percentage smoothness.
     */
    public void setSmoothness(int percentage) {
        percentage = Math.max(0, Math.min(percentage, 100));
        this.smoothness = percentage;
    }

    /**
     * Set whether the points should connected as steps or not.
     *
     * @param stepped True or false.
     */
    public void setStepped(boolean stepped) {
        this.stepped = stepped;
    }

    /**
     * Set whether the points should connected as steps or not.
     *
     * @param location The turning point of the step.
     */
    public void setStepped(Location location) {
        this.stepped = location == null ? null : Location.h(location);
    }

    /**
     * Set whether to connect null points or not.
     *
     * @param connectNullPoints True or false.
     */
    public void setConnectNullPoints(boolean connectNullPoints) {
        this.connectNulls = connectNullPoints;
    }

    /**
     * Get the line-style. (If <code>true</code> is passed as the parameter, a new line-style
     * will be created if not already exists).
     *
     * @param create Whether to create it or not.
     * @return Line-style.
     */
    public LineStyle getLineStyle(boolean create) {
        return getProperty(LineStyle.class, create);
    }

    /**
     * Set the line-style.
     *
     * @param lineStyle Line-style to set.
     */
    public void setLineStyle(LineStyle lineStyle) {
        setProperty(lineStyle);
    }

    /**
     * Get the area-style (to style the area under the line).
     * (If <code>true</code> is passed as the parameter, a new area-style will be created if not already exists).
     *
     * @param create Whether to create it or not.
     * @return Area-style.
     */
    public AreaStyle getAreaStyle(boolean create) {
        return getProperty(AreaStyle.class, create);
    }

    /**
     * Set the area-style.
     *
     * @param areaStyle Area-style to set.
     */
    public void setAreaStyle(AreaStyle areaStyle) {
        setProperty(areaStyle);
    }

    /**
     * Get the point symbol definition associated with this chart.
     *
     * @param create Whether to create it if doesn't exist.
     * @return Point symbol.
     */
    public PointSymbol getPointSymbol(boolean create) {
        if(pointSymbol == null && create) {
            pointSymbol = new PointSymbol();
        }
        return pointSymbol;
    }

    /**
     * Set the point symbol definition to be used.
     *
     * @param pointSymbol Point symbol to set.
     */
    public void setPointSymbol(PointSymbol pointSymbol) {
        this.pointSymbol = pointSymbol;
    }
}
