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
 * Funnel chart.
 * (Future versions will provide more chart-specific methods).
 *
 * @author Syam
 */
public class FunnelChart extends SelfPositioningChart {

    private int tailSize = 0, mouthSize = -100, sliceGap = 0;
    private byte align = 0;
    private boolean inverted = false;
    private boolean sorted = true;

    /**
     * Constructor.
     */
    public FunnelChart() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param itemNames Item names of the slices.
     * @param values Values of the slices.
     */
    public FunnelChart(AbstractDataProvider<?> itemNames, DataProvider values) {
        super(ChartType.Funnel, itemNames, values);
    }

    /**
     * Set names of the slices.
     *
     * @param itemNames Item names of the slices.
     */
    public void setItemNames(AbstractDataProvider<?> itemNames) {
        setData(itemNames, 0);
    }

    /**
     * Set data for the slices.
     *
     * @param data Data.
     */
    public void setData(DataProvider data) {
        setData(data, 1);
    }

    /**
     * Set the tail size. (Default value of tail size is zero and thus, the tail will look like a triangle).
     *
     * @param size Size of the tail to set.
     */
    public void setTailSize(Size size) {
        this.tailSize = size.get();
    }

    /**
     * By default, slices will be sorted and shown.
     *
     * @return True if sorted, otherwise false.
     */
    public boolean isSorted() {
        return sorted;
    }

    /**
     * Set the "sort" property for the slices.
     *
     * @param sorted True for sorted, false for not sorted.
     */
    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    /**
     * See if the slices are shown inverted or not.
     *
     * @return True if inverted, otherwise false.
     */
    public boolean isInverted() {
        return inverted;
    }

    /**
     * Invert the view of the slices. (By default, the slices are sorted in descending order. By setting
     * this to <code>true</code>, slices will be shown in ascending order).
     *
     * @param inverted True for inverted, false for not inverted.
     */
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(mouthSize != -100) {
            sb.append(',');
            sb.append("\"maxSize\":").append(Size.code(mouthSize));
        }
        if(tailSize != 0) {
            sb.append(',');
            sb.append("\"minSize\":").append(Size.code(tailSize));
        }
        if(!sorted || inverted) {
            sb.append(",\"sort\":\"").append(!sorted ? "none" : "ascending").append('"');
        }
        if(sliceGap > 0) {
            sb.append(',');
            ComponentPart.encode(sb, "gap", sliceGap);
        }
        if(align != 0) {
            sb.append(",\"funnelAlign\":\"").append(align < 0 ? "left" : "right").append('"');
        }
    }

    /**
     * Get the gap between slices.
     *
     * @return Gap.
     */
    public int getSliceGap() {
        return sliceGap;
    }

    /**
     * Set the gap between slices.
     *
     * @param sliceGap Gap in pixels.
     */
    public void setSliceGap(int sliceGap) {
        this.sliceGap = sliceGap;
    }

    /**
     * Set the mouth size of the funnel. (By default, the mouth size is 100%).
     *
     * @param mouthSize Mouth size to set.
     */
    public void setMouthSize(Size mouthSize) {
        this.mouthSize = mouthSize.get();
    }

    /**
     * Fit the funnel to the left side. It will look like fitted on the left wall.
     */
    public void fitOnLeftSide() {
        align = -1;
    }

    /**
     * Fit the funnel to the right side. It will look like fitted on the right wall.
     */
    public void fitOnRightSide() {
        align = 1;
    }

    /**
     * Show the funnel at the center.
     */
    public void showAtCenter() {
        align = 0;
    }
}