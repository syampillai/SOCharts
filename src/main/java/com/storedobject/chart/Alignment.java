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
 * Representation of Alignment property (both horizontal and vertical).
 *
 * @author Syam
 */
public class Alignment implements ComponentProperty {

    private String prefix, justify, align;

    /**
     * Constructor.
     */
    public Alignment() {
    }

    /**
     * Justify left.
     */
    public void justifyLeft() {
        this.justify = "left";
    }

    /**
     * Justify right.
     */
    public void justifyRight() {
        this.justify = "right";
    }

    /**
     * Justify center.
     */
    public void justifyCenter() {
        this.justify = "center";
    }

    /**
     * Align top.
     */
    public void alignTop() {
        align = "top";
    }

    /**
     * Align center/middle.
     */
    public void alignCenter() {
        align = "middle";
    }

    /**
     * Align bottom.
     */
    public void alignBottom() {
        align = "bottom";
    }

    /**
     * Align and justify at the center.
     */
    public void center() {
        justifyCenter();
        alignCenter();
    }

    void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private String p(String any) {
        if(prefix == null) {
            return any;
        }
        return prefix + any.substring(0, 1).toUpperCase() + any.substring(1);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        if(justify != null) {
            ComponentPart.encode(sb, p("align"), justify);
        }
        if(align != null) {
            ComponentPart.encode(sb, p("verticalAlign"), align);
        }
        prefix = null;
    }
}
