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
import java.util.stream.Stream;

/**
 * Representation of data as a {@link java.util.List}. The type of data can be anything that can be used
 * for charting. In charting, we need to distinguish between "numeric", "date/time", "categories" and "logarithmic"
 * values types (See {@link DataType}).
 *
 * @param <T> Data type.
 * @author Syam
 */
public class AbstractData<T> extends ArrayList<T> implements AbstractDataProvider<T> {

    private int serial = -1;
    private final DataType dataType;
    private String name;

    /**
     * Constructor.
     *
     * @param dataClass Data class.
     * @param data Initial data to add
     */
    @SafeVarargs
    public AbstractData(Class<T> dataClass, T... data) {
        this(dataType(dataClass), dataClass, data);
    }

    /**
     * Constructor.
     *
     * @param dataType Data type.
     * @param dataClass Data class.
     * @param data Initial data to add
     */
    @SafeVarargs
    public AbstractData(DataType dataType, Class<T> dataClass, T... data) {
        this.dataType = dataType;
        if(data != null) {
            addAll(Arrays.asList(data));
        }
    }

    private static DataType dataType(Class<?> dataClass) {
        for(DataType dt: DataType.values()) {
            if(dt.getType().isAssignableFrom(dataClass)) {
                return dt;
            }
        }
        return DataType.CATEGORY;
    }

    /**
     * Get the data type.
     *
     * @return Data type.
     */
    public final DataType getDataType() {
        return dataType;
    }

    @Override
    public final int getSerial() {
        return serial;
    }

    @Override
    public final void setSerial(int serial) {
        this.serial = serial;
    }

    /**
     * Get the name of this data set.
     *
     * @return Name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set a name for this data set.
     *
     * @param name Name to set.
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<T> asList() {
        return this;
    }

    @Override
    public Stream<T> stream() {
        return super.stream();
    }
}
