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
 * Sunburst chart (Beta version - not fully tested)
 *
 * @author Syam
 */
public class SunburstChart extends AbstractDataChart implements HasPosition {

    private TreeDataProvider data;
    private Position position;
    private ItemStyle itemStyle;

    /**
     * Create a tree chart. Data can be set later.
     */
    public SunburstChart() {
        this(null);
    }

    /**
     * Create a tree chart of the set of data.
     *
     * @param data Data to be used.
     */
    public SunburstChart(TreeDataProvider data) {
        super(ChartType.Sunburst);
    }

    /**
     * Get the the data associated with this chart.
     *
     * @return Data provider.
     */
    public TreeDataProvider getTreeData() {
        return data;
    }

    /**
     * Set data to the chart.
     *
     * @param data Data provider to set.
     */
    public void setTreeData(TreeDataProvider data) {
        this.data = data;
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        if(data == null) {
            throw new ChartException("No data provided for " + className());
        }
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(itemStyle != null) {
            ComponentPart.encode(sb, "itemStyle", itemStyle);
        }
        if(!skippingData) {
            sb.append(",\"data\":[");
            data.encodeJSON(sb);
            sb.append(']');
        }
    }

    @Override
    public final Position getPosition(boolean create) {
        if(position == null && create) {
            position = new Position();
        }
        return position;
    }

    @Override
    public final void setPosition(Position position) {
        this.position = position;
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
