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

    private AngleAxis<?> angleAxis;
    private RadiusAxis<?> radiusAxis;
    private PolarProperty polarProperty;

    /**
     * Constructor.
     */
    public PolarCoordinate() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param angleAxis Angle axis.
     * @param radiusAxis Radius axis.
     */
    public PolarCoordinate(AngleAxis<?> angleAxis, RadiusAxis<?> radiusAxis) {
        this.angleAxis = angleAxis;
        this.radiusAxis = radiusAxis;
    }

    /**
     * Get the angle axis.
     *
     * @return Angle axis.
     */
    public AngleAxis<?> getAngleAxis() {
        return angleAxis;
    }

    /**
     * Set the angle axis.
     *
     * @param angleAxis Angle axis to set.
     */
    public void setAngleAxis(AngleAxis<?> angleAxis) {
        this.angleAxis = angleAxis;
    }

    /**
     * Get the radius axis.
     *
     * @return Radius axis.
     */
    public RadiusAxis<?> getRadiusAxis() {
        return radiusAxis;
    }

    /**
     * Set the radius axis.
     *
     * @param radiusAxis Radius axis to set.
     */
    public void setRadiusAxis(RadiusAxis<?> radiusAxis) {
        this.radiusAxis = radiusAxis;
    }

    @Override
    public void validate() throws Exception {
        if(radiusAxis == null) {
            throw new Exception("Radius Axis is not set");
        }
        if(angleAxis == null) {
            throw new Exception("Angle Axis is not set");
        }
        if(radiusAxis.coordinateSystem != null && radiusAxis.coordinateSystem != this) {
            throw new Exception("Radius Axis is used by some other coordinate system");
        }
        radiusAxis.coordinateSystem = this;
        if(angleAxis.coordinateSystem != null && angleAxis.coordinateSystem != this) {
            throw new Exception("Polar Axis is used by some other coordinate system");
        }
        angleAxis.coordinateSystem = this;
        radiusAxis.validate();
        angleAxis.validate();
    }

    @Override
    public void addParts(SOChart soChart) {
        radiusAxis.coordinateSystem = this;
        angleAxis.coordinateSystem = this;
        soChart.addParts(radiusAxis, angleAxis);
        super.addParts(soChart);
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
}
