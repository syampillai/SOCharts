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
 * Representation of legend. A legend will be automatically displayed by the {@link SOChart}. However, that
 * can be turned off using {@link SOChart#disableDefaultLegend()} and customized legends may be added using
 * {@link SOChart#add(Component...)}.
 *
 * @author Syam
 */
public class Legend extends VisiblePart implements Component, HasPosition, HasPadding {

    private Position position;
    private Padding padding;
    private TextStyle textStyle;
    private boolean vertical = false;
    private Border border;

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(textStyle != null) {
            sb.append("\"textStyle\":{");
            ComponentPart.encodeProperty(sb, textStyle);
            sb.append('}');
        }
        if(vertical) {
            if(textStyle != null) {
                sb.append(',');
            }
            sb.append("\"orient\":\"vertical\"");
        }
        ComponentPart.encodeProperty(sb, border);
    }

    @Override
    public void validate() {
    }

    /**
     * Display it vertically.
     */
    public void showVertically() {
        vertical = true;
    }

    @Override
    public final Position getPosition(boolean create) {
        if(position == null && create) {
            position = new Position();
        }
        return position;
    }

    @Override
    public final void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Get the text style.
     *
     * @param create Whether to create if not exists or not.
     * @return Text style.
     */
    public final TextStyle getTextStyle(boolean create) {
        if(textStyle == null && create) {
            textStyle = new TextStyle();
        }
        return textStyle;
    }

    /**
     * Set the text style.
     *
     * @param textStyle Text style to set.
     */
    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    @Override
    public Padding getPadding(boolean create) {
        if(padding == null && create) {
            padding = new Padding();
        }
        return padding;
    }

    @Override
    public void setPadding(Padding padding) {
        this.padding = padding;
    }

    /**
     * Get the border.
     * @param create Whether to create if not exists or not.
     *
     * @return Border.
     */
    public final Border getBorder(boolean create) {
        if(border == null && create) {
            border = new Border();
        }
        return border;
    }

    /**
     * Set the border.
     *
     * @param border Border.
     */
    public void setBorder(Border border) {
        this.border = border;
    }
}
