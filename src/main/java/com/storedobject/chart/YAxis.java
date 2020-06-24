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
 * Representation of Y axis.
 *
 * @author Syam
 */
public class YAxis extends XYAxis {

    /**
     * Constructor.
     *
     * @param dataType Data type.
     */
    public YAxis(DataType dataType) {
        super(dataType);
    }

    @Override
    ComponentPart wrap(CoordinateSystem coordinateSystem) {
        AxisWrapper w = wrappers.get(coordinateSystem);
        return w == null ? new YAxisWrapper(this, coordinateSystem) : w;
    }

    @Override
    String positionString() {
        return "right";
    }

    static class YAxisWrapper extends AxisWrapper {

        YAxisWrapper(Axis axis, CoordinateSystem coordinateSystem) {
            super(axis, coordinateSystem);
        }
    }
}
