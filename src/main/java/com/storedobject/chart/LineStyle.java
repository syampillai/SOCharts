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
 * Line-style. Determines how a line will be drawn.
 *
 * @author Syam
 */
public class LineStyle extends AbstractStyle {

    /**
     * Line-type.
     *
     * @author Syam
     */
    public enum Type {
        /**
         * Solid.
         */
        SOLID,
        /**
         * Dashed.
         */
        DASHED,
        /**
         * Dotted.
         */
        DOTTED
    }
    private int width = Integer.MIN_VALUE;
    private Type type;

    /**
     * Constructor.
     */
    public LineStyle() {
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        encode(sb, "width", width);
        encode(sb, "type", type);
    }

    /**
     * Get the width of the line.
     *
     * @return Line width in pixels.
     */
    public final int getWidth() {
        return width;
    }

    /**
     * Set the width of the line.
     *
     * @param width Width of the line in pixels.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get the line-type.
     *
     * @return Line type.
     */
    public final Type getType() {
        return type;
    }

    /**
     * Set the line-type.
     *
     * @param type Line type.
     */
    public void setType(Type type) {
        this.type = type;
    }
}