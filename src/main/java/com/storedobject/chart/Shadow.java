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
 * Represents a shadow. Typically, other properties have this property as an additional property.
 * For example, {@link TextStyle} has this property to define its shadow.
 *
 * @author Syam
 */
public class Shadow implements ComponentProperty {

    private String prefix;
    private AbstractColor color;
    private int blur = Integer.MIN_VALUE, offsetX = Integer.MIN_VALUE, offsetY = Integer.MIN_VALUE;
    private int opacity = Integer.MIN_VALUE;

    /**
     * Constructor.
     */
    public Shadow() {
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        if(color != null) {
            ComponentPart.addComma(sb);
            sb.append('"').append(p("shadowColor")).append("\":").append(color);
        }
        if(blur > Integer.MIN_VALUE) {
            ComponentPart.addComma(sb);
            sb.append('"').append(p("shadowBlur")).append("\":").append(blur);
        }
        if(offsetX > Integer.MIN_VALUE) {
            ComponentPart.addComma(sb);
            sb.append('"').append(p("shadowOffsetX")).append("\":").append(offsetX);
        }
        if(offsetY > Integer.MIN_VALUE) {
            ComponentPart.addComma(sb);
            sb.append('"').append(p("shadowOffsetY")).append("\":").append(offsetY);
        }
        if(opacity >= 0) {
            ComponentPart.addComma(sb);
            sb.append("\"opacity\":").append(Math.min(100, opacity) / 100.0);
        }
        prefix = null;
    }

    private String p(String any) {
        if(prefix == null) {
            return any;
        }
        return prefix + any.substring(0, 1).toUpperCase() + any.substring(1);
    }

    /**
     * Get the blur for the shadow. (Example value: 10).
     *
     * @return Blur.
     */
    public final int getBlur() {
        return blur;
    }

    /**
     * Set the blur for the shadow. (Example value: 10).
     *
     * @param blur Blur.
     */
    public void setBlur(int blur) {
        this.blur = blur;
    }

    /**
     * Get the color.
     *
     * @return Color.
     */
    public final AbstractColor getColor() {
        return color;
    }

    /**
     * Set the color.
     *
     * @param color Color.
     */
    public void setColor(AbstractColor color) {
        this.color = color;
    }

    /**
     * Get offset X.
     *
     * @return X offset.
     */
    public final int getOffsetX() {
        return offsetX;
    }

    /**
     * Set offset X.
     *
     * @param offsetX X offset.
     */
    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    /**
     * Get offset Y.
     *
     * @return Y offset.
     */
    public final int getOffsetY() {
        return offsetY;
    }

    /**
     * Set offset Y.
     *
     * @param offsetY Y offset.
     */
    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Get the opacity of the shadow (Value as percentage 0 to 100%).
     *
     * @return Opacity.
     */
    public int getOpacity() {
        return opacity;
    }

    /**
     * Set the opacity of the shadow (Value as percentage, 0 to 100%).
     *
     * @param opacity Opacity.
     */
    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }
}
