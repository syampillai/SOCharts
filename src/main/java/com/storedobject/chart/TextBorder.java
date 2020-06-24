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
 * Represents text-border. Text can be drawn with borders and the width and color of the border can be set.
 *
 * @author Syam
 */
public class TextBorder implements ComponentProperty {

    private String prefix;
    private Color color;
    private int width = -1;
    private Shadow shadow;

    /**
     * Constructor.
     */
    public TextBorder() {
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        if(color != null) {
            ComponentPart.addComma(sb);
            sb.append('"').append(p("borderColor")).append("\":").append(color);
        }
        if(width >= 0) {
            ComponentPart.addComma(sb);
            sb.append('"').append(p("borderWidth")).append("\":").append(width);
        }
        if(shadow != null) {
            shadow.setPrefix(prefix);
            ComponentPart.encodeProperty(sb, shadow);
        }
        prefix = null;
    }

    String p(String any) {
        if(prefix == null) {
            return any;
        }
        return prefix + any.substring(0, 1).toUpperCase() + any.substring(1);
    }

    /**
     * Get the color of the border.
     *
     * @return Color of the border.
     */
    public final Color getColor() {
        return color;
    }

    /**
     * Set the color of the border.
     *
     * @param color Color to set.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get the width of the border (in pixels).
     *
     * @return Width of the border.
     */
    public final int getWidth() {
        return width;
    }

    /**
     * <p>Set the width of the border (in pixels).</p>
     * <p>Note: Corner radius is not applicable for text borders.</p>
     *
     * @param width Width of the border.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    void setPrefix(String prefix) {
        this.prefix = prefix;
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
}