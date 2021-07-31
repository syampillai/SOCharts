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
 * Representation of radius axis. This can be used in {@link RadarChart}s.
 *
 * @author Syam
 */
public class RadarAxis extends Axis {

    /**
     * Constructor.
     */
    public RadarAxis() {
        super(DataType.CATEGORY);
    }

    @Override
    ComponentPart wrap(CoordinateSystem coordinateSystem) {
        Axis.AxisWrapper w = wrappers.get(coordinateSystem);
        return w == null ? new RadarAxisWrapper(this, coordinateSystem) : w;
    }

    static class RadarAxisWrapper extends Axis.AxisWrapper {

        RadarAxisWrapper(Axis axis, CoordinateSystem coordinateSystem) {
            super(axis, coordinateSystem);
        }
    }
}
