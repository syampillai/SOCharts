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

/**
 * A class that defines basic style details.
 *
 * @author Syam
 */
public abstract class AbstractStyle implements ComponentProperty {

    private AbstractColor color;
    private int opacity = -1;
    private Shadow shadow;

    @Override
    public void encodeJSON(StringBuilder sb) {
        if(color != null) {
            color.encodeJSON(sb);
        }
        encode(sb, "opacity", Math.min(100, opacity) / 100.0);
        ComponentPart.encode(sb, null, shadow);
    }

    static void encode(StringBuilder sb, String name, Object value) {
        if(value == null) {
            return;
        }
        if(value instanceof Number && ((Number)value).intValue() <= 0) {
            return;
        }
        ComponentPart.addComma(sb);
        if(value instanceof LineStyle.Type) {
            value = value.toString().toLowerCase();
        }
        ComponentPart.encode(sb, name, value);
    }

    /**
     * Get color.
     *
     * @return Color.
     */
    public final AbstractColor getColor() {
        return color;
    }

    /**
     * Set color.
     *
     * @param color Color.
     */
    public void setColor(AbstractColor color) {
        this.color = color;
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
