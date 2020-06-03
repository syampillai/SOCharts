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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Abstract data provider interface. The type of data can be anything that can be used
 * for charting. In charting, we need to distinguish between "numeric", "date/time", "categories" and "logarithmic"
 * values types.
 *
 * @param <T> Data type.
 * @author Syam
 */
public interface AbstractDataProvider<T> extends ComponentPart {

    /**
     * Data provided by this provider as a stream.
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
    Class<T> getDataType();

    @Override
    default void encodeJSON(StringBuilder sb) {
        sb.append("\"d").append(getSerial()).append("\":");
        append(sb);
    }

    /**
     * Append the JSON encoding of this to the given string builder.
     *
     * @param sb Append the JSONified string to this.
     */
    default void append(StringBuilder sb) {
        append(sb, stream(),"[", "]", true, null);
    }

    /**
     * Append the JSON encoding of all values coming from a stream to the given string builder.
     *
     * @param sb Append the JSONified string to this.
     * @param stream Sream of data values.
     * @param prefix Prefix to add.
     * @param suffix Suffix to add.
     * @param appendAnyway Append prefix and suffix even if data is empty.
     * @param valueEncoder Encoder for the value read from the stream. If <code>null</code> is passed, stringified
     *                     version will be appended.
     * @param <O> Type of data value in the stream.
     */
    static <O> void append(StringBuilder sb, Stream<O> stream, String prefix, String suffix, boolean appendAnyway,
                           BiConsumer<StringBuilder, Object> valueEncoder) {
        AtomicBoolean first = new AtomicBoolean(true);
        stream.forEach(v -> {
            if(first.get()) {
                first.set(false);
                sb.append(prefix);
            } else {
                sb.append(',');
            }
            if(valueEncoder == null) {
                sb.append(ComponentPart.escape(v));
            } else {
                valueEncoder.accept(sb, v);
            }
        });
        if(first.get()) {
            if(appendAnyway) {
                sb.append(prefix).append(suffix);
            }
        } else {
            sb.append(suffix);
        }
    }

    /**
     * Get the type of the data as a String for encoding purposes.
     *
     * @return Data type suitable for encoding.
     */
    default String getType() {
        return getType(getDataType());
    }

    /**
     * Helper method to determine the data type for encoding.
     *
     * @param dataType Value type.
     * @return Data type that can be used for encoding.
     */
    static String getType(Class<?> dataType) {
        if(Number.class.isAssignableFrom(dataType)) {
            return "value";
        }
        return "category";
    }
}
