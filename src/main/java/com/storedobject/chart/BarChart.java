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
 * Bar chart.
 *
 * @author Syam
 */
public class BarChart extends XYChart {

    private boolean roundCap;
    private int barGap = Integer.MAX_VALUE, barCategoryGap = Integer.MAX_VALUE, barWidth = Integer.MAX_VALUE;

    /**
     * Constructor. (Data can be set later).
     */
    public BarChart() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param xData Data for X axis.
     * @param yData Data for Y axis.
     */
    public BarChart(AbstractDataProvider<?> xData, AbstractDataProvider<?> yData) {
        super(ChartType.Bar, xData, yData);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.encode(sb,"roundCap", roundCap);
        if(barGap < Integer.MAX_VALUE) {
            ComponentPart.encode(sb, "barGap", barGap);
        }
        if(barCategoryGap < Integer.MAX_VALUE) {
            ComponentPart.encode(sb, "barCategoryGap", barCategoryGap);
        }
        if(barWidth < Integer.MAX_VALUE) {
            ComponentPart.encode(sb, "barWidth", barWidth);
        }
    }

    /**
     * Is round-cap feature set?
     *
     * @return True/false.
     */
    public final boolean isRoundCap() {
        return roundCap;
    }

    /**
     * Set round-cap feature. If set it to <code>true</code>, the ending portion of the bar will be rounded.
     * This will be effective only when plotting this chart on {@link PolarCoordinate}s.
     *
     * @param roundCap Round-cap feature.
     */
    public void setRoundCap(boolean roundCap) {
        this.roundCap = roundCap;
    }

    /**
     * Get the gap between bars when multiple bar charts are displayed on the same coordinate system.
     *
     * @return Gap as a percentage value. Negative values are supported to enable overlapping.
     */
    public final int getBarGap() {
        return barGap;
    }

    /**
     * Set the gap between bars when multiple bar charts are displayed on the same coordinate system. Need to set only
     * on one of the bar charts and it will be shared by all bar charts on the same coordinate system. If you set
     * different values on different bar charts, only the one set in the last one will be used.
     *
     * @param barGap Gap as a percentage value. Negative values are supported to enable overlapping.
     */
    public void setBarGap(int barGap) {
        this.barGap = barGap;
    }

    /**
     * Get the gap between the categories of the same bar chart.
     *
     * @return Gap as a percentage value. Negative values are supported to enable overlapping.
     */
    public final int getBarCategoryGap() {
        return barCategoryGap;
    }

    /**
     * Set the gap between the categories of the same bar chart.
     *
     * @param barCategoryGap Gap as a percentage value. Negative values are supported to enable overlapping.
     */
    public void setBarCategoryGap(int barCategoryGap) {
        this.barCategoryGap = barCategoryGap;
    }

    /**
     * Get the width of the bar.
     *
     * @return Width of the bar in pixels.
     */
    public final int getBarWidth() {
        return barWidth;
    }

    /**
     * Set the width of the bar.
     *
     * @param barWidth Width of the bar in pixels.
     */
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    /**
     * This chart doesn't support mark area and this method always returns <code>null</code>.
     *
     * @param create If passed true, a new {@link MarkArea} is created if not exists.
     * @return <code>Null</code>
     */
    @Override
    public final MarkArea getMarkArea(boolean create) {
        return null;
    }

    /**
     * This chart doesn't support mark area and this method does nothing.
     *
     * @param markArea Mark area to set.
     */
    @Override
    public final void setMarkArea(MarkArea markArea) {
    }
}
