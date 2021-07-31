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
 * Basic XY-type chart - mostly plotted on a {@link RectangularCoordinate} system.
 *
 * @author Syam
 */
public abstract class XYChart extends AbstractChart {

    String stackName;
    private ItemStyle itemStyle;

    /**
     * Constructor.
     *
     * @param type Type.
     * @param xData Data for X axis.
     * @param yData Data for Y axis.
     */
    public XYChart(ChartType type, AbstractDataProvider<?> xData, AbstractDataProvider<?> yData) {
        super(type, xData, yData);
    }

    XYChart(ChartType type, AbstractDataProvider<?> xData, AbstractDataProvider<?> yData,
            AbstractDataProvider<?> values) {
        super(type, xData, yData, values);
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
    public void setYData(AbstractDataProvider<?> yData) {
        setData(yData, 1);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(stackName != null) {
            ComponentPart.encode(sb, "stack", stackName, true);
        }
        if(itemStyle != null) {
            ComponentPart.encode(sb, "itemStyle", itemStyle, true);
        }
    }

    /**
     * Certain charts (example: {@link LineChart}, {@link BarChart}) can stack multiples of them when drawn on the
     * same coordinate system with shared axis. If stacking needs to be enabled, those charts should have the same
     * stack name.
     *
     * @param stackName Stack name.
     */
    public void setStackName(String stackName) {
        this.stackName = stackName;
    }

    /**
     * Get item style.
     *
     * @param create If passed true, a new style is created if not exists.
     * @return Item style.
     */
    public final ItemStyle getItemStyle(boolean create) {
        if(itemStyle == null && create) {
            itemStyle = new ItemStyle();
        }
        return itemStyle;
    }

    /**
     * Set item style.
     *
     * @param itemStyle Style to set.
     */
    public void setItemStyle(ItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }
}