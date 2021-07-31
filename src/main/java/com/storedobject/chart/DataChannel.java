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
 * Data channel opens up a channel to push data on to a {@link SOChart} once rendered. This channel can be used
 * to push or append more data to the charts. These push/append operations will not touch your sources, so you
 * need to maintain those yourself if you want to do complete redrawing of the chart later.
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
     * Append data to the chart. The number of data elements will grow by 1 due to this and the chart will be repainted
     * accordingly.
     *
     * @param data Data to append. The data types should be compatible with the data providers for which this channel
     *             was created.
     * @throws ChartException If invalid data is passed.
     */
    public void append(Object... data) throws ChartException {
        process("append", data);
    }

    /**
     * Append data to the chart. The number of data elements will grow by 1 due to this and the chart will be repainted
     * accordingly.
     *
     * @param data Data to push. No error checking will happen and the data is pushed as such. Null values are allowed
     *             and will be handled as per the behaviour of the corresponding charts.
     */
    public void appendEncoded(Object... data) {
        processEncoded("append", data);
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
        process("push", data);
    }

    private void process(String command, Object... data) throws ChartException {
        if(data == null || data.length == 0) {
            return;
        }
        try {
            StringBuilder sb;
            @SuppressWarnings("rawtypes") AbstractDataProvider dp;
            for(int i = 0; i < dataProviders.length; i++) {
                sb = new StringBuilder();
                dp = dataProviders[i];
                //noinspection unchecked
                dp.encode(sb, data[i]);
                data[i] = sb;
            }
        } catch(Throwable e) {
            throw new ChartException("Incompatible data");
        }
        processEncoded(command, data);
    }

    /**
     * Push encoded data to the chart. The number of data elements will remain the same after this operation because
     * for the new value added, the value from the other end will be removed and the chart will be repainted
     * accordingly.
     *
     * @param data Data to push. No error checking will happen and the data is pushed as such. Null values are allowed
     *             and will be handled as per the behaviour of the corresponding charts.
     */
    public void pushEncoded(Object... data) {
        processEncoded("push", data);
    }

    private void processEncoded(String command, Object... data) {
        if(data == null || data.length == 0) {
            return;
        }
        int i = 0;
        StringBuilder sb = new StringBuilder("{\"d\":{");
        boolean first = true;
        for(AbstractDataProvider<?> d: dataProviders) {
            if(i >= dataProviders.length) {
                break;
            }
            if(d.getSerial() > 0) {
                if(data[i] == null) {
                    data[i] = "-";
                }
                if(first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append("\"d").append(d.getSerial()).append("\":").append(data[i]);
            }
            ++i;
        }
        sb.append("}}");
        soChart.updateData(sb.toString(), command);
    }

    /**
     * Reset data. All data defined in this channel are removed.
     */
    public void reset() {
        StringBuilder sb = new StringBuilder("{\"d\":[");
        boolean first = true;
        for(AbstractDataProvider<?> d: dataProviders) {
            if(d.getSerial() > 0) {
                if(first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append(d.getSerial());
            }
        }
        sb.append("]}");
        soChart.updateData(sb.toString(), "reset");
    }
}
