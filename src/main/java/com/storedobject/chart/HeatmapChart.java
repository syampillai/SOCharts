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
 * Heatmap chart. A heatmap chart is plotted on a {@link RectangularCoordinate} system.
 * <p>A {@link VisualMap} is automatically created unless you set it explicitly to <code>null</code> via
 * {@link #setVisualMap(VisualMap)}.</p>
 *
 * @author Syam
 */
public class HeatmapChart extends Chart {

    private final List<HeatmapData> data = new ArrayList<>();
    private final HD hd = new HD();
    private VisualMap visualMap = new VisualMap(this);

    /**
     * Constructor.
     *
     * @param xData Labels on the X-axis.
     * @param yData Labels on the Y-axis.
     */
    public HeatmapChart(CategoryData xData, CategoryData yData) {
        super(ChartType.Heatmap);
        setData(xData, yData, hd);
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        if(coordinateSystem == null || !RectangularCoordinate.class.isAssignableFrom(coordinateSystem.getClass())) {
            throw new ChartException("Heatmap chart must be plotted on a rectangular coordinate system");
        }
        RectangularCoordinate rc = (RectangularCoordinate) coordinateSystem;
        rc.validate();
        rc.axes.get(0).setData((CategoryData) getData()[0]);
        rc.axes.get(1).setData((CategoryData) getData()[1]);
    }

    /**
     * Add heatmap data point.
     *
     * @param xIndex X-index at which data needs to added.
     * @param yIndex Y-index at which data needs to added.
     * @param value Value to be added.
     */
    public void addData(int xIndex, int yIndex, Number value) {
        data.add(new HeatmapData(xIndex, yIndex, value));
    }

    /**
     * Add heatmap data point.
     *
     * @param xValue X-value at which data needs to added.
     * @param yIndex Y-index at which data needs to added.
     * @param value Value to be added.
     */
    public void addData(String xValue, int yIndex, Number value) {
        data.add(new HeatmapData(xValue, yIndex, value));
    }

    /**
     * Add heatmap data point.
     *
     * @param xIndex X-index at which data needs to added.
     * @param yValue Y-value at which data needs to added.
     * @param value Value to be added.
     */
    public void addData(int xIndex, String yValue, Number value) {
        data.add(new HeatmapData(xIndex, yValue, value));
    }

    /**
     * Add heatmap data point.
     *
     * @param xValue X-value at which data needs to added.
     * @param yValue Y-value at which data needs to added.
     * @param value Value to be added.
     */
    public void addData(String xValue, String yValue, Number value) {
        data.add(new HeatmapData(xValue, yValue, value));
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return hd;
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

    private static class HeatmapData {

        Object xIndex, yIndex;
        Number value;

        private HeatmapData(Object xIndex, Object yIndex, Number value) {
            this.xIndex = xIndex;
            this.yIndex = yIndex;
            this.value = value;
        }
    }

    private class HD extends BasicInternalDataProvider<Object> {

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
            HeatmapData d = (HeatmapData) value;
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
