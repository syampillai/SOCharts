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

import java.util.Arrays;
import java.util.Objects;

/**
 * Data channel opens up a channel to push data on to a {@link SOChart} once rendered. This channel can be used
 * to push or append more data to the charts.
 *
 * @author Syam
 */
public final class DataChannel {

    private final SOChart soChart;
    private final AbstractDataProvider<?>[] dataProviders;

    /**
     * Create a data channel for the given set of data providers.
     *
     * @param soChart Chart on which this channel needs to be activated.
     * @param dataProviders Data sources for which the channel needs to be created.
     */
    public DataChannel(SOChart soChart, AbstractDataProvider<?>... dataProviders) {
        assert soChart != null;
        assert dataProviders != null && dataProviders.length > 0;
        this.soChart = soChart;
        this.dataProviders = dataProviders;
    }

    /**
     * Append data to the chart. The number of data elements will grow due to this and the chart will be repainted
     * accordingly.
     *
     * @param data Data to append. The data types should be compatible with the data providers for which this channel
     *             was created.
     * @throws ChartException If invalid data is passed.
     */
    public void append(Object... data) throws ChartException {
        checkData(data);
        soChart.appendData(data(data));
    }

    /**
     * Push data to the chart. The number of data elements will remain the same after this operation because for the
     * new value added, the value from the other end will be removed and the chart will be repainted
     * accordingly.
     *
     * @param data Data to push. The data types should be compatible with the data providers for which this channel
     *             was created.
     * @throws ChartException If invalid data is passed.
     */
    public void push(Object... data) throws ChartException {
        checkData(data);
        soChart.pushData(data(data));
    }

    private String data(Object[] d) {
        StringBuilder sb = new StringBuilder("{\"d\":[");
        for(int i = 0; i < d.length; i++) {
            if(i > 0) {
                sb.append(',');
            }
            sb.append("{\"i\":").append(dataProviders[i].getSerial());
            ComponentPart.encode(sb, "v", d[i], true);
            sb.append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private void checkData(Object... data) throws ChartException {
        if(data == null || data.length == 0) {
            throw new ChartException("Empty data");
        }
        if(Arrays.stream(data).anyMatch(Objects::isNull)) {
            throw new ChartException("Null values in data");
        }
        if(data.length != dataProviders.length) {
            throw new ChartException("Incorrect number of data elements");
        }
        for(int i = 0; i < data.length; i++) {
            if(!dataProviders[i].getDataType().getType().isAssignableFrom(data[i].getClass())) {
                throw new ChartException("Invalid data type at index " + i);
            }
        }
    }
}
