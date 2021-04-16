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
 * Area-style. Determines how an area or a series of areas will be drawn.
 *
 * @author Syam
 */
public class AreaStyle extends AbstractStyle {

    private AbstractColor[] colors;

    /**
     * Constructor.
     */
    public AreaStyle() {
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        AbstractColor c = getColor();
        if(colors != null && colors.length > 0) {
            if(c != null) {
                setColor(null);
            }
        } else {
            c = null;
        }
        super.encodeJSON(sb);
        if(colors != null && colors.length > 0) {
            ComponentPart.addComma(sb);
            for(int i = 0; i < colors.length; i++) {
                if(i == 0) {
                    sb.append("\"color\":[");
                } else {
                    sb.append(',');
                }
                sb.append(colors[i]);
            }
            sb.append(']');
        }
        if(c != null) {
            setColor(c);
        }
    }

    /**
     * Set multiple colors. Will be applied in sequence to each part of the area in the case of multi-parts areas.
     * The single color if any, set using {@link #setColor(AbstractColor)} will be ignored if this is set. This
     * should not be set (or will be ignored even if it is set) in the case of single block areas.
     *
     * @param colors Color
     */
    public void setColors(AbstractColor... colors) {
        this.colors = colors;
    }
}
