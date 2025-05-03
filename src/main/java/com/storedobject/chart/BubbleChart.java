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

import java.util.List;

/**
 * Bubble chart. A bubble chart is plotted on a {@link RectangularCoordinate} system and at each (x, y) point, there
 * can be an associated "value". A "bubble" is drawn for each such data point, and the size of the "bubble" is proportional
 * to the value. However, the size of the "bubble" can be controlled by setting a "multiplication factor" via
 * {@link #setBubbleSize(double)}.
 * <p>Internally, the "bubble" is an instance of the {@link PointSymbol} but, it may not be directly customized unless
 * necessary.</p>
 * <p>A {@link VisualMap} is automatically created unless you set it explicitly to <code>null</code> via
 * {@link #setVisualMap(VisualMap)}.</p>
 *
 * @author Syam
 */
public class BubbleChart extends XYDataChart {

    private PointSymbol pointSymbol;
    private double bubbleSize = 1;
    private String valuePrefix, valueSuffix;

    /**
     * Constructor.
     *
     * @param xData Data for X axis.
     * @param yData Data for Y axis.
     */
    public BubbleChart(AbstractDataProvider<?> xData, AbstractDataProvider<?> yData) {
        super(ChartType.Scatter, xData, yData);
        getTooltip(true).append(this);
    }

    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(bubbleSize >= 0) {
            StringBuilder s = new StringBuilder();
            ComponentPart.encodeFunction(s, null, "return v[2]*" + bubbleSize, "v");
            getPointSymbol(true).size = s.toString();
        }
        if(pointSymbol != null) {
            pointSymbol.encodeJSON(sb);
        }
    }

    /**
     * Set the bubble size.
     * 
     * @param multiplicationFactor This factor will multiply value at each data point to determine the size
     *                             of the bubble.
     */
    public void setBubbleSize(double multiplicationFactor) {
        this.bubbleSize = multiplicationFactor;
    }

    /**
     * Set a prefix text to the value when the tooltip is displayed for the bubble.
     *
     * @param valuePrefix Prefix text.
     */
    public void setValuePrefix(String valuePrefix) {
        this.valuePrefix = valuePrefix;
        tooltip();
    }

    /**
     * Set a suffix text to the value when the tooltip is displayed for the bubble.
     *
     * @param valueSuffix Suffix text.
     */
    public void setValueSuffix(String valueSuffix) {
        this.valueSuffix = valueSuffix;
        tooltip();
    }

    private void tooltip() {
        Tooltip tooltip = getTooltip(true);
        List<Object> parts = tooltip.parts;
        parts.clear();
        tooltip.append(valuePrefix);
        tooltip.append(this);
        tooltip.append(valueSuffix);
    }

    /**
     * Get the {@link PointSymbol} that represents the "bubble".
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
     * Set a different {@link PointSymbol} that represents the "bubble". If set via this method, "bubble size" will no more
     * be controlled automatically unless set it again via the {@link #setBubbleSize(double)} method.
     *
     * @param pointSymbol An instance of the {@link PointSymbol}.
     */
    public void setPointSymbol(PointSymbol pointSymbol) {
        this.pointSymbol = pointSymbol;
        bubbleSize = -1;
    }
}
