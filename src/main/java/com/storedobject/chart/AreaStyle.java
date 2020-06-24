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

import java.util.ArrayList;
import java.util.List;

/**
 * Area-style. Determines how an area or a series of areas will be drawn.
 *
 * @author Syam
 */
public class AreaStyle implements ComponentProperty {

    private final List<Color> colors = new ArrayList<>();
    private int opacity = -1;
    private Shadow shadow;

    /**
     * Constructor.
     */
    public AreaStyle() {
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        if(!colors.isEmpty()) {
            for(int i = 0; i < colors.size(); i++) {
                if(i == 0) {
                    sb.append("\"color\":[");
                } else {
                    sb.append(',');
                }
                sb.append(colors.get(i));
            }
            sb.append(']');
        }
        if(opacity >= 0) {
            ComponentPart.addComma(sb);
            sb.append("\"opacity\":").append(Math.min(100, opacity) / 100.0);
        }
        if(shadow != null) {
            ComponentPart.encodeProperty(sb, shadow);
        }
    }

    /**
     * Get the color-bands.
     *
     * @return Colors.
     */
    public final List<Color> getColors() {
        return colors;
    }

    /**
     * Set color-bands.
     *
     * @param colors Colors to set.
     */
    public void setColors(Color... colors) {
        this.colors.clear();
        if(colors != null) {
            for (Color c : colors) {
                if (c != null) {
                    this.colors.add(c);
                }
            }
        }
    }

    /**
     * Get the shadow.
     *
     * @param create Whether to create if not exists or not.
     * @return Shadow.
     */
    public final Shadow getShadow(boolean create) {
        if(shadow == null && create) {
            shadow = new Shadow();
        }
        return shadow;
    }

    /**
     * Set the shadow.
     *
     * @param shadow Shadow.
     */
    public void setShadow(Shadow shadow) {
        this.shadow = shadow;
    }

    /**
     * Get the opacity of the line (Value as percentage 0 to 100%).
     *
     * @return Opacity.
     */
    public int getOpacity() {
        return opacity;
    }

    /**
     * Set the opacity of the line (Value as percentage, 0 to 100%).
     *
     * @param opacity Opacity.
     */
    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }
}
