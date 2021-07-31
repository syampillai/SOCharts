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
import java.util.stream.Stream;

/**
 * Radar chart. It is generally used with multi-dimensional data. Multiple sets of data can be added using the
 * {@link #addData(DataProvider...)} method. It should be plotted on a {@link RadarCoordinate} system.
 *
 * @author Syam
 */
public class RadarChart extends Chart {

    private final List<DataProvider> dataList = new ArrayList<>();
    private final RadarData data = new RadarData();

    /**
     * Create a chart with data.
     *
     * @param data Data to be used.
     */
    public RadarChart(DataProvider... data) {
        super(ChartType.Radar);
        super.setData(this.data);
        addData(data);
    }

    @Override
    public void setData(AbstractDataProvider<?>... data) {
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
            throw new ChartException("Radar chart should be plotted on a Radar Coordinate system: " + className());
        }
        if(dataList.isEmpty()) {
            throw new ChartException("No data set for " + className());
        }
        String name;
        for(int i = 0; i < dataList.size(); i++) {
            name = dataList.get(i).getName();
            if(name == null) {
                dataList.get(i).setName("Data " + i);
            }
        }
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return data;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
    }

    private class RadarData implements AbstractDataProvider<DataProvider>, InternalDataProvider {

        private int serial = -1;

        @Override
        public Stream<DataProvider> stream() {
            return dataList.stream();
        }

        @Override
        public void encode(StringBuilder sb, DataProvider value) {
            String name = value.getName();
            sb.append("{\"name\":\"").append(name == null ? "Data" : name).append("\",\"value\":");
            value.encodeJSON(sb);
            sb.append('}');
        }

        @Override
        public DataType getDataType() {
            return DataType.OBJECT;
        }

        @Override
        public void setSerial(int serial) {
            this.serial = serial;
        }

        @Override
        public int getSerial() {
            return serial;
        }
    }
}
