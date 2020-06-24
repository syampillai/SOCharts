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
import java.util.List;

/**
 * Radar chart. It is generally used with multi-dimensional data. Multiple sets of data can be added using the
 * {@link #addData(DataProvider...)} method.
 *
 * @author Syam
 */
public class RadarChart extends AbstractDataChart {

    private final List<DataProvider> dataList = new ArrayList<>();

    /**
     * Create a chart with data.
     *
     * @param data Data to be used.
     */
    public RadarChart(DataProvider... data) {
        super(ChartType.Radar);
        addData(data);
    }

    /**
     * Add data to the chart.
     *
     * @param data Data to be added.
     */
    public void addData(DataProvider... data) {
        if(data != null) {
            for (DataProvider d: data) {
                if(d != null) {
                    dataList.add(d);
                }
            }
        }
    }

    /**
     * Remove data from the chart.
     *
     * @param data Data to be removed.
     */
    public void removeData(DataProvider... data) {
        if(data != null) {
            for (DataProvider d: data) {
                if(d != null) {
                    dataList.remove(d);
                }
            }
        }
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        if(!(coordinateSystem instanceof RadarCoordinate)) {
            throw new ChartException("Radar chart can be plotted on a Radar Coordinate system only: " + className());
        }
        if(skippingData) {
            return;
        }
        if(dataList.isEmpty()) {
            throw new ChartException("No data set for " + className());
        }
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(skippingData) {
            return;
        }
        sb.append(",\"data\":[");
        String name;
        int i = 1;
        for(DataProvider data: dataList) {
            if(i > 1) {
                sb.append(',');
            }
            sb.append("{\"value\":");
            name = data.getName();
            if(name == null) {
                name = "Data " + i;
            }
            data.append(sb);
            sb.append(",\"name\":").append(ComponentPart.escape(name)).append('}');
            ++i;
        }
        sb.append(']');
    }
}
