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

import java.util.Objects;

/**
 * Polar property is visible in {@link PieChart}, {@link PolarCoordinate}, {@link RadarCoordinate} etc. where
 * an angular and a radial element exist.
 *
 * @author Syam
 */
public class PolarProperty implements ComponentProperty {

    private int innerRadius = 0, radius = -75;
    private int centerH = -50, centerV = -50;

    /**
     * Set size of the radius.
     * @param radius Size of the radius.
     */
    public void setRadius(Size radius) {
        this.radius = radius.get();
    }

    /**
     * Set the inner radius (generally to crate a hole at the center).
     *
     * @param innerRadius Inner radius to set.
     */
    public void setInnerRadius(Size innerRadius) {
        this.innerRadius = innerRadius.get();
    }

    /**
     * Set the center. Default is [50%, 50%].
     *
     * @param horizontal Horizontal position.
     * @param vertical Vertical position.
     */
    public void setCenter(Size horizontal, Size vertical) {
        this.centerH = horizontal.get();
        this.centerV = vertical.get();
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        Size ir = new Size(innerRadius);
        Size or = new Size(radius);
        if(!ir.isNull() || !or.isNull()) {
            sb.append("\"radius\":");
            if(!ir.isNull()) {
                sb.append('[').append(ir).append(',');
                if(or.isNull()) {
                    or.set(ir.plus(10));
                }
                sb.append(or);
                sb.append(']');
            } else {
                sb.append(or);
            }
            sb.append(',');
        }
        sb.append("\"center\":[").append(new Size(centerH)).append(',').
                append(new Size(centerV)).append(']');
    }
}
