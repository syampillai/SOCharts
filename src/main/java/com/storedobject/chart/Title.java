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
 * Title for the chart. If you are adding more than one title, please make sure that you set appropriate
 * positions to eliminate overlap.
 *
 * @author Syam
 */
public class Title extends VisiblePart implements Component, HasPosition {

    private String text, subtext;
    private Position position;
    private TextStyle textStyle, subtextStyle;
    private int gap = -1;

    /**
     * Create a title with a given text.
     *
     * @param text Text to display as title.
     */
    public Title(String text) {
        this.text = text;
    }

    /**
     * Get the text of the title.
     *
     * @return Title text.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text of the title.
     *
     * @param text Text of the title.
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        TextStyle.OuterProperties op = new TextStyle.OuterProperties();
        ComponentPart.encode(sb, "text", text);
        String t = getSubtext();
        if(t != null) {
            sb.append(',');
            ComponentPart.encode(sb, "subtext", t);
            if(subtextStyle != null) {
                subtextStyle.save(op);
                ComponentPart.encode(sb, "subtextStyle", subtextStyle);
                subtextStyle.restore(op);
            }
        }
        ComponentPart.encode(sb, null, position);
        if(textStyle != null) {
            textStyle.save(op);
            ComponentPart.encode(sb, "textStyle", textStyle);
            textStyle.restore(op);
            if(op.background != null) {
                sb.append(",\"backgroundColor\":").append(op.background);
            }
            ComponentPart.encode(sb, null, op.padding);
            ComponentPart.encode(sb, null, op.border);
            if(op.alignment != null) {
                op.alignment.setPrefix("text");
                ComponentPart.encode(sb, null, op.alignment);
            }
        }
        if(gap > 0 && t != null) {
            ComponentPart.addComma(sb);
            sb.append("\"itemGap\":").append(gap);
        }
    }

    @Override
    public void validate() {
    }

    /**
     * Get the sub-text that will be shown as the sub-title in the second line.
     *
     * @return Text of the sub-title. It could be <code>null</code>.
     */
    public String getSubtext() {
        return subtext;
    }

    /**
     * Set the sub-text that will be shown as the sub-title in the second line.
     *
     * @param subtext Text of the sub-title. It could be <code>null</code>.
     */
    public void setSubtext(String subtext) {
        this.subtext = subtext;
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

    /**
     * Get the sub-text style.
     *
     * @param create Whether to create if not exists or not.
     * @return Text style.
     */
    public final TextStyle getSubtextStyle(boolean create) {
        if(subtextStyle == null && create) {
            subtextStyle = new TextStyle();
        }
        return subtextStyle;
    }

    /**
     * Set the sub-text style.
     *
     * @param textStyle Text style to set.
     */
    public void setSubtextStyle(TextStyle textStyle) {
        this.subtextStyle = textStyle;
    }

    /**
     * Get the gap between title and sub-title.
     *
     * @return Gap in pixels.
     */
    public final int getGap() {
        return gap;
    }

    /**
     * Set the gap between title and sub-title.
     *
     * @param gap Gap in pixels.
     */
    public void setGap(int gap) {
        this.gap = gap;
    }
}