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
public class Title extends AbstractDisplayablePart implements Component, HasPosition {

    private String text, subtext;
    private Position position;

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
        ComponentPart.encode(sb, "text", text);
        String t = getSubtext();
        if(t != null) {
            sb.append(',');
            ComponentPart.encode(sb, "subtext", t);
        }
        ComponentPart.encodeProperty(sb, getPosition(false));
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
}