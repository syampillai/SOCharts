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
 * Representation of polar coordinate system with an angle axis and a radius axis.
 *
 * @author Syam
 */
public class PolarCoordinate extends CoordinateSystem implements HasPolarProperty {

    private PolarProperty polarProperty;

    /**
     * Constructor.
     */
    public PolarCoordinate() {
    }

    /**
     * Constructor.
     *
     * @param radiusAxis Radius axis.
     * @param angleAxis Angle axis.
     */
    public PolarCoordinate(RadiusAxis radiusAxis, AngleAxis angleAxis) {
        addAxis(radiusAxis, angleAxis);
    }

    @Override
    public void validate() throws ChartException {
        if(noAxis(RadiusAxis.class)) {
            throw new ChartException("Radius Axis is not set");
        }
        if(noAxis(AngleAxis.class)) {
            throw new ChartException("Angle Axis is not set");
        }
    }

    @Override
    public final PolarProperty getPolarProperty(boolean create) {
        if(polarProperty == null && create) {
            polarProperty = new PolarProperty();
        }
        return polarProperty;
    }

    @Override
    public final void setPolarProperty(PolarProperty polarProperty) {
        this.polarProperty = polarProperty;
    }

    @Override
    String systemName() {
        return "polar";
    }

    @Override
    String[] axesData() {
        return new String[] { "radius", "angle" };
    }
}
