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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Bubble chart. A bubble chart is plotted on a {@link RectangularCoordinate} system and at each (x, y) point, there
 * can be an associated "value". A "bubble" is drawn for each such data point and size of the "bubble" is proportional
 * to the value. However, the size of the "bubble" can be controlled by setting a "multiplication factor" via
 * {@link #setBubbleSize(double)}.
 * <p>Internally, the "bubble" is an instance of the {@link PointSymbol} but, it may not be directly customized unless
 * necessary.</p>
 * <p>A {@link VisualMap} is automatically created unless you set it explicitly to <code>null</code> via
 * {@link #setVisualMap(VisualMap)}.</p>
 *
 * @author Syam
 */
public class BubbleChart extends Chart {

    private final List<BubbleData> data = new ArrayList<>();
    private final BD bd = new BD();
    private VisualMap visualMap = new VisualMap(this);
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
        super(ChartType.Scatter);
        setData(xData, yData, bd);
        getTooltip(true).append(this);
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        if(coordinateSystem == null || !RectangularCoordinate.class.isAssignableFrom(coordinateSystem.getClass())) {
            throw new ChartException("Bubble chart must be plotted on a rectangular coordinate system");
        }
        RectangularCoordinate rc = (RectangularCoordinate) coordinateSystem;
        rc.validate();
        rc.axes.get(0).setData(getData()[0]);
        rc.axes.get(1).setData(getData()[1]);
    }

    @Override
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
     * Add bubble data point.
     *
     * @param xIndex X-index at which data needs to added.
     * @param yIndex Y-index at which data needs to added.
     * @param value Value to be added.
     */
    public void addData(int xIndex, int yIndex, Number value) {
        data.add(new BubbleData(xIndex, yIndex, value));
    }

    /**
     * Add bubble data point.
     *
     * @param xValue X-value at which data needs to added.
     * @param yIndex Y-index at which data needs to added.
     * @param value Value to be added.
     */
    public void addData(String xValue, int yIndex, Number value) {
        data.add(new BubbleData(xValue, yIndex, value));
    }

    /**
     * Add bubble data point.
     *
     * @param xIndex X-index at which data needs to added.
     * @param yValue Y-value at which data needs to added.
     * @param value Value to be added.
     */
    public void addData(int xIndex, String yValue, Number value) {
        data.add(new BubbleData(xIndex, yValue, value));
    }

    /**
     * Add bubble data point.
     *
     * @param xValue X-value at which data needs to added.
     * @param yValue Y-value at which data needs to added.
     * @param value Value to be added.
     */
    public void addData(String xValue, String yValue, Number value) {
        data.add(new BubbleData(xValue, yValue, value));
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return bd;
    }

    @Override
    protected int dataValueIndex() {
        return 2;
    }

    @Override
    public void addParts(SOChart soChart) {
        super.addParts(soChart);
        if(visualMap != null) {
            soChart.addParts(visualMap);
        }
    }

    /**
     * Get the {@link VisualMap} associated with this chart.
     *
     * @return The {@link VisualMap} instance.
     */
    public final VisualMap getVisualMap() {
        return visualMap;
    }

    /**
     * Set a {@link VisualMap} for this chart. Set <code>null</code> if you don't want any {@link VisualMap}.
     *
     * @param visualMap {@link VisualMap} to set.
     */
    public void setVisualMap(VisualMap visualMap) {
        this.visualMap = visualMap;
    }

    /**
     * Set the bubble size.
     * 
     * @param multiplicationFactor Value at each data point will be multiplied by this factor to determine the size
     *                             of the bubble.
     */
    public void setBubbleSize(double multiplicationFactor) {
        this.bubbleSize = multiplicationFactor;
    }

    /**
     * Set a prefix text to the value when tooltip is displayed for the bubble.
     *
     * @param valuePrefix Prefix text.
     */
    public void setValuePrefix(String valuePrefix) {
        this.valuePrefix = valuePrefix;
        tooltip();
    }

    /**
     * Set a suffix text to the value when tooltip is displayed for the bubble.
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
     * Set a different point-symbol that represents the "bubble". If set via this method, "bubble size" will no more
     * be controlled automatically unless set it again via the {@link #setBubbleSize(double)} method.
     *
     * @param pointSymbol An instance of the {@link PointSymbol}.
     */
    public void setPointSymbol(PointSymbol pointSymbol) {
        this.pointSymbol = pointSymbol;
        bubbleSize = -1;
    }

    private static class BubbleData {

        Object xIndex, yIndex;
        Number value;

        private BubbleData(Object xIndex, Object yIndex, Number value) {
            this.xIndex = xIndex;
            this.yIndex = yIndex;
            this.value = value;
        }
    }

    private class BD extends BasicInternalDataProvider<Object> {

        @Override
        public Stream<Object> stream() {
            return data.stream().map(o -> o);
        }

        @Override
        public Object getMin() {
            return data.stream().map(o -> o.value).min(new NumberComparator()).orElse(0);
        }

        @Override
        public Object getMax() {
            return data.stream().map(o -> o.value).max(new NumberComparator()).orElse(0);
        }

        @Override
        public void encode(StringBuilder sb, Object value) {
            sb.append('[');
            BubbleData d = (BubbleData) value;
            if(d.xIndex instanceof Number) {
                sb.append(((Number)d.xIndex).intValue());
            } else {
                sb.append('"').append(d.xIndex).append('"');
            }
            sb.append(',');
            if(d.yIndex instanceof Number) {
                sb.append(((Number)d.yIndex).intValue());
            } else {
                sb.append('"').append(d.yIndex).append('"');
            }
            sb.append(',');
            sb.append(d.value).append(']');
        }
    }
}
