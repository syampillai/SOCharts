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
 * Abstract representation of an axis.
 *
 * @param <T> Data type of the axis.
 * @author Syam
 */
public abstract class Axis<T> extends AbstractDisplayablePart implements ComponentPart {

    CoordinateSystem coordinateSystem;
    private final Class<T> dataType;

    /**
     * Constructor.
     *
     * @param dataType Data type.
     */
    public Axis(Class<T> dataType) {
        this.dataType = dataType;
    }

    /**
     * Get the type of the data.
     *
     * @return value type.
     */
    public final String getType() {
        return AbstractDataProvider.getType(dataType);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void validate() throws Exception{
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append("\"type\":\"").append(getType()).append('"');
        if(coordinateSystem != null) {
            sb.append(",\"").append(SOChart.encoderLabel(coordinateSystem));
            sb.append("Index\":").append(coordinateSystem.getSerial());
        }
    }
}