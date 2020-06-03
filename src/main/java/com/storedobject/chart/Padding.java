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
 * Padding. Several {@link ComponentPart}s use this property to define padding around them.
 *
 * @author Syam
 */
public class Padding implements ComponentProperty {

    private int paddingTop = 5, paddingRight = 5, paddingBottom = 5, paddingLeft = 5;

    /**
     * Constructor.
     */
    public Padding() {
    }

    /**
     * Set top padding.
     *
     * @param paddingTop Padding.
     */
    public void setPaddingTop(int paddingTop) {
        this.paddingTop = Math.max(0, paddingTop);
    }

    /**
     * Set right padding.
     *
     * @param paddingRight Padding.
     */
    public void setPaddingRight(int paddingRight) {
        this.paddingRight = Math.max(0, paddingRight);
    }

    /**
     * Set bottom padding.
     *
     * @param paddingBottom Padding.
     */
    public void setGetPaddingBottom(int paddingBottom) {
        this.paddingBottom = Math.max(0, paddingBottom);
    }

    /**
     * Set left padding.
     *
     * @param paddingLeft Padding.
     */
    public void setGetPaddingLeft(int paddingLeft) {
        this.paddingLeft = Math.max(0, paddingLeft);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        sb.append("\"padding\":");
        if(paddingTop == paddingBottom && paddingLeft == paddingRight) {
            if(paddingTop == paddingLeft) {
                sb.append(paddingTop);
            } else {
                sb.append('[').append(paddingTop).append(',').append(paddingLeft).append(']');
            }
        } else {
            sb.append('[').append(paddingTop).append(',').append(paddingRight).append(',').
                    append(paddingBottom).append(',').append(paddingLeft).append(']');
        }
    }
}
