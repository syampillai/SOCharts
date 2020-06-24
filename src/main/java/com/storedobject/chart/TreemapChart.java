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
import java.util.Arrays;
import java.util.List;

/**
 * Treemap chart.
 *
 * @author Syam
 */
public class TreemapChart extends AbstractDataChart implements HasPosition {

    private final List<TreeDataProvider> data = new ArrayList<>();
    private Position position;

    /**
     * Create a tree chart of the set of data.
     *
     * @param data Data to be used.
     */
    public TreemapChart(TreeDataProvider... data) {
        super(ChartType.Treemap);
        addData(data);
    }

    /**
     * Get the the list of data associated with this chart.
     *
     * @return List of data providers.
     */
    public List<TreeDataProvider> getTreemapData() {
        return data;
    }

    /**
     * Add data to the chart.
     *
     * @param data List of data to add.
     */
    public void addData(TreeDataProvider... data) {
        if(data != null) {
            this.data.addAll(Arrays.asList(data));
        }
    }

    /**
     * Remove data from the chart.
     *
     * @param data List of data to remove.
     */
    public void removeData(TreeDataProvider... data) {
        if(data != null) {
            this.data.removeAll(Arrays.asList(data));
        }
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        if(data.isEmpty()) {
            throw new ChartException("No data provided for " + className());
        }
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(!skippingData) {
            sb.append(",\"data\":[");
            for(int i = 0; i < data.size(); i++) {
                if(i > 0) {
                    sb.append(',');
                }
                data.get(i).encodeJSON(sb);
            }
            sb.append(']');
        }
        ComponentPart.addComma(sb);
        ComponentPart.encode(sb, "leafDepth", 1);
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
}
