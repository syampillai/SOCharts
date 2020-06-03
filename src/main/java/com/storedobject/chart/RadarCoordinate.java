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
 * Radar coordinate is used by {@link RadarChart}. Its each leg (axis) can be labelled (indicated)
 * by {@link CategoryData}.
 *
 * @author Syam
 */
public class RadarCoordinate extends CoordinateSystem implements HasPolarProperty {

    private PolarProperty polarProperty;
    private CategoryData axisIndicators;

    /**
     * Constructor. Axis indicators can be set later.
     */
    public RadarCoordinate() {
        this(null);
    }

    /**
     * Construct with the given set of axis indicators.
     *
     * @param axisIndicators Axis indicators to set.
     */
    public RadarCoordinate(CategoryData axisIndicators) {
        this.axisIndicators = axisIndicators;
    }

    /**
     * Set axis indicators.
     *
     * @param axisIndicators Axis indicators to set.
     */
    public void setAxisIndicators(CategoryData axisIndicators) {
        this.axisIndicators = axisIndicators;
    }

    /**
     * Get the current axis indicators.
     *
     * @return Axis indicators.
     */
    public CategoryData getAxisIndicators() {
        return axisIndicators;
    }

    @Override
    public void validate() throws Exception {
        if(axisIndicators == null || axisIndicators.isEmpty()) {
            throw new Exception("No axis-indicators for " + className());
        }
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append("\"indicator\":[");
        boolean first = true;
        for(String category: axisIndicators) {
            if(first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append("{\"name\":").append(ComponentPart.escape(category)).append('}');
        }
        sb.append(']');
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
