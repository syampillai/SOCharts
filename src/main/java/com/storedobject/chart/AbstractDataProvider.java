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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Abstract data provider interface. The type of data can be anything that can be used
 * for charting. In charting, we need to distinguish between "numeric", "date/time", "categories" and "logarithmic"
 * values types. (See {@link DataType}).
 *
 * @param <T> Data class type.
 * @author Syam
 */
public interface AbstractDataProvider<T> extends ComponentPart {

    /**
     * Data provided by this provider as a stream.
     * <p>Note: The {@link Stream} should be reproducible after a terminal operation.</p>
     *
     * @return Stream of data values.
     */
    Stream<T> stream();

    /**
     * Collect all data values into a list.
     *
     * @return Data values as a list.
     */
    default List<T> asList() {
        if(this instanceof List) {
            //noinspection unchecked
            return (List<T>) this;
        }
        List<T> list = new ArrayList<>();
        stream().forEach(list::add);
        return list;
    }

    /**
     * Get the value type of the data.
     *
     * @return value type.
     */
    DataType getDataType();

    @Override
    default String getName() {
        return "Data " + getSerial();
    }

    @Override
    default void encodeJSON(StringBuilder sb) {
        sb.append('[');
        AtomicBoolean first = new AtomicBoolean(true);
        stream().forEach(v -> {
            if(first.get()) {
                first.set(false);
            } else {
                sb.append(',');
            }
            encode(sb, v);
        });
        sb.append(']');
    }

    /**
     * Encode a value for this data.
     *
     * @param value Value to encode.
     */
    default void encode(StringBuilder sb, T value) {
        sb.append(ComponentPart.escape(value));
    }

    @Override
    default long getId() {
        return -1L;
    }

    @Override
    default void validate() throws ChartException {
    }

    /**
     * Get the minimum value from this data. This is used by certain components such as {@link VisualMap}
     * to automatically find out the minimum value of a value-based chart.
     * <p>The default implementation tries to use the {@link Comparator} returned by the {@link #getComparator()} method
     * to determine this value. If no {@link Comparator} is available, <code>null</code> will be returned.</p>
     *
     * @return Minimum value of the data.
     */
    default T getMin() {
        if(getDataType() == DataType.CATEGORY) {
            return stream().findFirst().orElse(null);
        }
        Comparator<T> comparator = getComparator();
        return comparator == null ? null : stream().min(getComparator()).orElse(null);
    }

    /**
     * Get the maximum value from this data. This is used by certain components such as {@link VisualMap}
     * to automatically find out the minimum value of a value-based chart.
     * <p>The default implementation tries to use the {@link Comparator} returned by the {@link #getComparator()} method
     * to determine this value. If no {@link Comparator} is available, <code>null</code> will be returned.</p>
     *
     * @return Minimum value of the data.
     */
    default T getMax() {
        if(getDataType() == DataType.CATEGORY) {
            AtomicReference<T> value = new AtomicReference<>(null);
            stream().forEach(value::set);
            return value.get();
        }
        Comparator<T> comparator = getComparator();
        return comparator == null ? null : stream().max(getComparator()).orElse(null);
    }

    /**
     * This comparator, if available, will be used to determine the min/max values of the data if required by the
     * {@link #getMin()} and {@link #getMax()} methods.
     *
     * @return Comparator. (Default is null).
     */
    default Comparator<T> getComparator() {
        //noinspection unchecked
        return getDataType() == DataType.NUMBER ? (Comparator<T>) new NumberComparator() : null;
    }

    /**
     * A crude {@link Number} comparator that can be used for data that is of {@link Number} type.
     */
    class NumberComparator implements Comparator<Number> {

        @Override
        public int compare(Number n1, Number n2) {
            if(n1 == null || n2 == null) {
                return n1 == null && n2 == null ? 0 : (n1 == null ? -1 : 1);
            }
            return new BigDecimal(n1.toString()).compareTo(new BigDecimal(n2.toString()));
        }
    }

    /**
     * Create another data set by applying a mapping function to this data set. (Each item and its index are passed to
     * the mapping function).
     *
     * @param convertedType Converted type.
     * @param mappingFunction Mapping function.
     * @param <D> Type of data in the target data set.
     * @return A new data set with values mapped from this data set.
     */
    default <D> AbstractDataProvider<D> create(DataType convertedType, BiFunction<T, Integer, D> mappingFunction) {
        return new AbstractDataProvider<>() {

            private int serial = -1;

            @Override
            public Stream<D> stream() {
                AtomicInteger index = new AtomicInteger(0);
                return AbstractDataProvider.this.stream()
                        .map(item -> mappingFunction.apply(item, index.getAndIncrement()));
            }

            @Override
            public DataType getDataType() {
                return convertedType;
            }

            @Override
            public void setSerial(int serial) {
                this.serial = serial;
            }

            @Override
            public int getSerial() {
                return serial;
            }
        };
    }

    /**
     * Create another data set by applying a mapping function to this data set. (Each item is passed to
     * the mapping function).
     *
     * @param convertedType Converted type.
     * @param mappingFunction Mapping function.
     * @param <D> Type of data in the target data set.
     * @return A new data set with values mapped from this data set.
     */
    default <D> AbstractDataProvider<D> create(DataType convertedType, Function<T, D> mappingFunction) {
        return create(convertedType, (item, index) -> mappingFunction.apply(item));
    }
}
