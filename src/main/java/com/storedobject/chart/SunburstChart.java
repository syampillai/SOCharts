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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Sunburst chart.
 *
 * @author Syam
 */
public class SunburstChart extends Chart implements HasPosition, HasPolarProperty {

    private final List<TreeDataProvider> data = new ArrayList<>();
    private Position position;
    private final TD td = new TD();
    private PolarProperty polarProperty;

    /**
     * Create a tree chart.
     *
     * @param data Data to be used.
     */
    public SunburstChart(TreeDataProvider... data) {
        super(ChartType.Sunburst);
        super.setData(td);
        addData(data);
    }

    @Override
    public void setData(AbstractDataProvider<?>... data) {
    }

    /**
     * Get the list of data associated with this chart.
     *
     * @return List of data providers.
     */
    public List<TreeDataProvider> getSunburstData() {
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
    protected AbstractDataProvider<?> dataToEmbed() {
        return td;
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

    @Override
    public final PolarProperty getPolarProperty(boolean create) {
        if(polarProperty == null && create) {
            polarProperty = new PolarProperty();
        }
        return polarProperty;
    }

    @Override
    public final void setPolarProperty(PolarProperty polarProperty) {
        this.polarProperty = polarProperty;
    }

    private class TD implements AbstractDataProvider<Object>, InternalDataProvider {

        private int serial = -1;

        @Override
        public Stream<Object> stream() {
            return data.stream().map(o -> o);
        }

        @Override
        public void encode(StringBuilder sb, Object value) {
            ((TreeDataProvider)value).encodeJSON(sb);
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
