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
 * Represents the symbol used to draw a point on the chart. (Example usage is in {@link LineChart}).
 *
 * @author Syam
 */
public class PointSymbol implements ComponentProperty {

    private PointSymbolType type = PointSymbolType.CIRCLE;
    private boolean show = true;
    private String size;
    private boolean hoverAnimation = true;

    /**
     * Constructor.
     */
    public PointSymbol() {
    }

    /**
     * Show the symbol.
     */
    public void show() {
        show = true;
    }

    /**
     * Hide the symbol.
     */
    public void hide() {
        show = false;
    }

    /**
     * Set the point symbol that decides how the points will be drawn.
     *
     * @param pointSymbolType Point symbol.
     */
    public void setType(PointSymbolType pointSymbolType) {
        this.type = pointSymbolType;
    }

    /**
     * Set size of the point symbol.
     *
     * @param size Size in pixels.
     */
    public void setSize(int size) {
        if(size <= 0) {
            this.size = null;
        } else {
            this.size = "" + size;
        }
    }

    /**
     * Set size of the point symbol.
     *
     * @param width Width in pixels.
     * @param height Height in pixels.
     */
    public void setSize(int width, int height) {
        if(width > 0 && height > 0) {
            this.size = "[" + width + "," + height + "]";
        } else if(width > 0) {
            setSize(width);
        } else if(height > 0) {
            setSize(height);
        } else {
            this.size = null;
        }
    }

    /**
     * Animate on hover.
     *
     * @param hoverAnimation True/false.
     */
    public void setHoverAnimation(boolean hoverAnimation) {
        this.hoverAnimation = hoverAnimation;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        sb.append(",\"showSymbol\":").append(show);
        if(type != null) {
            ComponentPart.encode(sb,"symbol", type, true);
            type = null;
        }
        if(size != null) {
            sb.append(",\"symbolSize\":").append(size);
            size = null;
        }
        sb.append(",\"hoverAnimation\":").append(hoverAnimation);
    }
}
