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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Radar coordinate is used by {@link RadarChart}. Each of its leg (radial axis) can be labelled (indicators)
 * by {@link CategoryData}.
 *
 * @author Syam
 */
public class RadarCoordinate extends CoordinateSystem implements HasPolarProperty {

    private PolarProperty polarProperty;
    private CategoryDataProvider axisIndicators;
    private int startingAngle = 90;
    private AbstractColor color;
    private RadarAxis axis;
    private boolean circular;

    /**
     * Constructor. Axis indicators can be set later. A default {@link RadarAxis} will be created.
     */
    public RadarCoordinate() {
        this(null, null);
    }

    /**
     * Construct with the given set of axis indicators. A default {@link RadarAxis} will be created.
     *
     * @param axisIndicators Axis indicators to set.
     */
    public RadarCoordinate(CategoryDataProvider axisIndicators) {
        this(null, axisIndicators);
    }

    /**
     * Constructor. Axis indicators can be set later.
     *
     * @param axis Radar axis.
     */
    public RadarCoordinate(RadarAxis axis) {
        this(axis, null);
    }

    /**
     * Construct with the given set of axis indicators.
     *
     * @param axis Radar axis.
     * @param axisIndicators Axis indicators to set.
     */
    public RadarCoordinate(RadarAxis axis, CategoryDataProvider axisIndicators) {
        this.axisIndicators = axisIndicators;
        setAxis(axis);
    }

    @Override
    public void addAxis(Axis... axes) {
    }

    public void setAxis(RadarAxis axis) {
        this.axis = axis;
    }

    public RadarAxis getAxis() {
        return axis;
    }

    /**
     * Set axis indicators.
     *
     * @param axisIndicators Axis indicators to set.
     */
    public void setAxisIndicators(CategoryDataProvider axisIndicators) {
        this.axisIndicators = axisIndicators;
    }

    /**
     * Get the current axis indicators.
     *
     * @return Axis indicators.
     */
    public CategoryDataProvider getAxisIndicators() {
        return axisIndicators;
    }

    @Override
    public void validate() throws ChartException {
        if(axisIndicators == null || axisIndicators.stream().findAny().isEmpty()) {
            throw new ChartException("No axis-indicators for " + className());
        }
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append("\"indicator\":[");
        final AtomicBoolean first = new AtomicBoolean(true);
        axisIndicators.stream().forEach(category -> {
            if(first.get()) {
                first.set(false);
            } else {
                sb.append(',');
            }
            sb.append("{\"name\":").append(ComponentPart.escape(category)).append('}');
        });
        sb.append("],\"startAngle\":").append(startingAngle);
        if(circular) {
            sb.append(",\"shape\":\"circle\"");
        }
        ComponentPart.encodeProperty(sb, color);
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

    /**
     * Get the starting angle.
     *
     * @return Angle in degrees.
     */
    public int getStartingAngle() {
        return startingAngle;
    }

    /**
     * Set the starting angle.
     *
     * @param startingAngle Angle in degrees.
     */
    public void setStartingAngle(int startingAngle) {
        this.startingAngle = startingAngle;
    }

    /**
     * Get the color of the indicator.
     *
     * @return Color.
     */
    public final AbstractColor getColor() {
        return color;
    }

    /**
     * Set color of the indicator.
     *
     * @param color Color.
     */
    public void setColor(AbstractColor color) {
        this.color = color;
    }

    /**
     * Set the shape as circular.
     *
     * @param circular True/false.
     */
    public void setCircular(boolean circular) {
        this.circular = circular;
    }

    /**
     * Is the shape circular?
     *
     * @return True/false.
     */
    public final boolean isCircular() {
        return circular;
    }
}
