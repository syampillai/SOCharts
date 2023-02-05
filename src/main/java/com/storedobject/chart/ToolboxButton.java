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

import com.storedobject.helper.ID;

/**
 * This is the base class for creating "toolbox buttons" that can be added to {@link Toolbox}. A standard set of
 * "toolbox buttons" are available as static classes within {@link Toolbox}. However, support for
 * "custom toolbox buttons" is not yet implemented.
 *
 * @author Syam
 */
public abstract class ToolboxButton implements ComponentPart {

    private final long id = ID.newID();
    private boolean show = true;
    private String caption;

    @Override
    public final long getId() {
        return id;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        sb.append("\"show\":").append(show);
        encodeCaptionJSON(sb);
    }

    /**
     * Encode caption part here. The default implementation does what is necessary to handle the return value of
     * {@link #getCaption()}.
     *
     * @param sb Append the encoded stuff to this.
     */
    protected void encodeCaptionJSON(StringBuilder sb) {
        String s = getCaption();
        if(s != null) {
            sb.append(',');
            ComponentPart.encode(sb, "title", s);
        }
    }

    @Override
    public void validate() throws ChartException {
    }

    @Override
    public final int getSerial() {
        return -1;
    }

    @Override
    public final void setSerial(int serial) {
    }

    /**
     * Show this part.
     */
    public void show() {
        show = true;
    }

    /**
     * Hide this part.
     */
    public void hide() {
        show = false;
    }

    /**
     * Get the caption for this (will be shown as a tooltip).
     *
     * @return Caption.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Set the caption for this (will be shown as a tooltip).
     *
     * @param caption Caption.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
}
