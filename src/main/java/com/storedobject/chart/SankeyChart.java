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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Sankey chart.
 *
 * @author Syam
 */
public class SankeyChart extends SelfPositioningSpecialChart {

    private SankeyDataProvider data;
    private final SD sd;
    private boolean horizontal = true;

    /**
     * Create a tree chart of the set of provided data.
     *
     * @param data Data to be used.
     */
    public SankeyChart(SankeyDataProvider data) {
        super(ChartType.Sankey);
        this.data = data;
        super.setData(sd = new SD());
    }

    @Override
    public final void setData(AbstractDataProvider<?>... data) {
    }

    /**
     * Get the Sankey data associated with this chart.
     *
     * @return Sankey data.
     */
    public SankeyDataProvider getSankeyData() {
        return data;
    }

    /**
     * Set the Sankey data associated with this chart.
     *
     * @param data Sankey data.
     */
    public void setSankeyData(SankeyDataProvider data) {
        this.data = data;
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return sd;
    }

    /**
     * Set the orientation as vertical.
     */
    public void setVertical() {
        horizontal = false;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(!horizontal) {
            ComponentPart.encode(sb, "orient", "vertical");
        }
        ComponentPart.addComma(sb);
        sb.append("\"edges\":[");
        AtomicBoolean first = new AtomicBoolean(true);
        data.getEdges().forEach(e -> {
            if(first.get()) {
                first.set(false);
            } else {
                sb.append(',');
            }
            e.encodeJSON(sb);
        });
        sb.append(']');
    }

    private class SD implements AbstractDataProvider<Object>, InternalDataProvider {

        private int serial = -1;

        @Override
        public Stream<Object> stream() {
            return data.getNodes().map(o -> o);
        }

        @Override
        public void encode(StringBuilder sb, Object value) {
            ((SankeyDataProvider.Node)value).encodeJSON(sb);
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
