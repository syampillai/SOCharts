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
 * Abstract base class for creating specific subtypes of charts that requires more axes.
 *
 * @author Syam
 */
public abstract class AbstractChart extends Chart {

    private final boolean axesBased;

    /**
     * Create a chart of a given type and data.
     *
     * @param type type of the chart.
     * @param data Data to be used (multiples of them for charts that use multi-axis coordinate systems).
     */
    public AbstractChart(ChartType type, AbstractDataProvider<?>... data) {
        this(type, true, data);
    }

    /**
     * Create a chart of a given type and data.
     *
     * @param type type of the chart.
     * @param axesBased Whether this is an axes-based chart or not.
     * @param data Data to be used (multiples of them for charts that use multi-axis coordinate systems).
     */
    public AbstractChart(ChartType type, boolean axesBased, AbstractDataProvider<?>... data) {
        super(type);
        super.setType(type);
        this.axesBased = axesBased;
        if(axesBased) {
            AbstractDataProvider<?>[] d = new AbstractDataProvider[type.getAxes().length];
            if(data != null) {
                for(int i = 0; i < data.length; i++) {
                    if(i == d.length) {
                        break;
                    }
                    d[i] = data[i];
                }
            }
            super.setData(d);
        } else {
            super.setData(data);
        }
    }

    /**
     * Calling this method does not have any effect. On specific charts, the {@link ChartType} can not be changed.
     *
     * @param type Type to be set.
     */
    @Override
    public final void setType(ChartType type) {
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        if(axesBased && dataToEmbed() == null) {
            AbstractDataProvider<?>[] d = getData();
            for(int i = 0; i < d.length; i++) {
                if(d[i] == null) {
                    throw new ChartException("Data for " + axisName(i) + " not set for " + className());
                }
            }
        }
    }

    /**
     * This method if invoked will raise a {@link RuntimeException} if this is an axes-based chart. However, you can use
     * {@link #setData(AbstractDataProvider, int)} to set data at a particular index.
     *
     * @param data Data to be used.
     */
    @Override
    public final void setData(AbstractDataProvider<?>... data) {
        if(axesBased) {
            throw new RuntimeException();
        } else {
            super.setData(data);
        }
    }

    /**
     * Use this method to set data at a specific index in derived classes.
     *
     * @param data Data to be set.
     * @param index Index position in the data array.
     */
    protected final void setData(AbstractDataProvider<?> data, int index) {
        AbstractDataProvider<?>[] d = getData();
        if(index >= 0 && index < d.length) {
            d[index] = data;
        }
    }
}
