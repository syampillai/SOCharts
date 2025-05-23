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

/**
 * Represents the symbol type used to draw a point on the chart. (Example usage is in {@link LineChart}).
 *
 * @author Syam
 */
public enum PointSymbolType {

    /**
     * Circle.
     */
    CIRCLE,
    /**
     * Empty circle.
     */
    EMPTY_CIRCLE("emptyCircle"),
    /**
     * Rectangle.
     */
    RECTANGLE("rect"),
    /**
     * Rounded rectangle.
     */
    ROUND_RECTANGLE("roundRect"),
    /**
     * Triangle.
     */
    TRIANGLE,
    /**
     * Diamond.
     */
    DIAMOND,
    /**
     * Pin.
     */
    PIN,
    /**
     * Arrow.
     */
    ARROW,
    /**
     * None.
     */
    NONE;

    private final String value;

    /**
     * Constructor.
     */
    PointSymbolType() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param value Value representation.
     */
    PointSymbolType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + (value == null ? super.toString().toLowerCase() : value) + "\"";
    }
}
