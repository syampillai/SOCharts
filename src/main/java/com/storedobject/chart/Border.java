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

import java.util.Arrays;

/**
 * Represents a border. Several other properties or parts have border as an additional property.
 *
 * @author Syam
 */
public class Border extends TextBorder {

    private AbstractColor background;
    private final int[] radius = new int[4];

    /**
     * Constructor.
     */
    public Border() {
        Arrays.fill(radius, 0);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        if(any()) {
            ComponentPart.addComma(sb);
            sb.append('"').append(p("borderRadius")).append("\":");
            if(radius[0] == radius[1] && radius[1] == radius[2] && radius[2] == radius[3]) {
                sb.append(radius[0]);
            } else {
                for(int i = 0; i < radius.length; i++) {
                    if(i == 0) {
                        sb.append('[');
                    } else {
                        sb.append(',');
                    }
                    sb.append(radius[i]);
                }
                sb.append(']');
            }
        }
        if(background != null) {
            ComponentPart.addComma(sb);
            sb.append("\"backgroundColor\":").append(background);
        }
        super.encodeJSON(sb);
    }

    private boolean any() {
        for(int r: radius) {
            if(r > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get background color.
     *
     * @return Background color.
     */
    public final AbstractColor getBackground() {
        return background;
    }

    /**
     * Set background color.
     *
     * @param background Background color.
     */
    public void setBackground(AbstractColor background) {
        this.background = background;
    }

    /**
     * Set the radius of the border corners (all corners) (in degrees).
     *
     * @param radius Radius of the corner.
     */
    public void setRadius(int radius) {
        Arrays.fill(this.radius, Math.min(90, Math.max(0, radius)));
    }

    /**
     * Set the radius at the given corner. (Corner index: Top-left corner is 0, Top-right corner is 1,
     * Bottom-right corner is 2, Bottom-left corner is 3).
     *
     * @param index Corner index.
     * @param radius Radius to set in degrees.
     */
    public void setRadius(int index, int radius) {
        if(index >= 0) {
            this.radius[index % this.radius.length] = Math.min(90, Math.max(0, radius));
        }
    }
}
