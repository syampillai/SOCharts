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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Type of data that can be used by charts.
 *
 * @author Syam
 */
public enum DataType {

    /**
     * Numeric values.
     */
    NUMBER("value", Number.class),
    /**
     * Category values.
     */
    CATEGORY("category", String.class),
    /**
     * Date values.
     */
    DATE("time", LocalDate.class),
    /**
     * Time values.
     */
    TIME("time", LocalDateTime.class),
    /**
     * Logarithmic values.
     */
    LOGARITHMIC("log", Number.class),
    /**
     * Object values - used by specialized charts.
     */
    OBJECT("value", Object.class);

    private final String name;
    private final Class<?> type;

    /**
     * Constructor (internally used).
     *
     * @param name Name.
     * @param type Type.
     */
    DataType(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Get the type.
     *
     * @return Type.
     */
    public final Class<?> getType() {
        return type;
    }

    @Override
    public String toString() {
        return "\"" + name + "\"";
    }

    /**
     * Encode the given value as per the data type for creating the JSON part.
     *
     * @param value Value.
     * @return Encoded value.
     */
    public String encode(Object value) {
        if(this == NUMBER) {
            return value.toString();
        }
        return "\"" + value + "\"";
    }

    /**
     * Guess the data type for a given value.
     *
     * @param value Value.
     * @return Data type.
     */
    public static DataType guessType(Object value) {
        if(value instanceof String) {
            return CATEGORY;
        }
        if(value instanceof LocalDateTime) {
            return TIME;
        }
        if(value instanceof LocalDate) {
            return DATE;
        }
        if(value instanceof Number) {
            return NUMBER;
        }
        return OBJECT;
    }
}