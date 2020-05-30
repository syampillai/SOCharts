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
 * Representation of "size" value. It can represent size in pixels {@link #pixels(int)} or size in percentage
 * {@link #percentage(int)}.
 *
 * @author Syam
 */
public class Size {

    private int size;

    Size() {
        this(Integer.MIN_VALUE);
    }

    Size(int size) {
        this.size = size;
    }

    int get() {
        return size;
    }

    void set(Size size) {
        this.size = size.size;
    }

    void left() {
        size = -101;
    }

    void center() {
        size = -102;
    }

    void right() {
        size = -103;
    }

    void top() {
        size = -111;
    }

    void middle() {
        size = -112;
    }

    void bottom() {
        size = -113;
    }

    String encode() {
        return encode(Integer.MIN_VALUE);
    }

    String encode(int defaultValue) {
        if(size == defaultValue || size == Integer.MIN_VALUE) {
            return null;
        }
        switch (size) {
            case -101:
                return "\"left\"";
            case -102:
                return "\"center\"";
            case -103:
                return "\"right\"";
            case -111:
                return "\"top\"";
            case -112:
                return "\"middle\"";
            case -113:
                return "\"bottom\"";
        }
        if(size < 0) {
            return "\"" + (-size) + "%\"";
        }
        return "" + size;
    }

    /**
     * Create a "size" value in pixels.
     *
     * @param pixels Number of pixels representing this size.
     * @return Size created.
     */
    public static Size pixels(int pixels) {
        return new Size(Math.max(0, pixels));
    }

    /**
     * Create a "size" value as a percentage.
     *
     * @param percentage Size as a percentage.
     * @return Size created.
     */
    public static Size percentage(int percentage) {
        return new Size(-Math.min(100, percentage));
    }

    /**
     * Create a "null" size. This is useful when we want to reset some already existing "size" value.
     *
     * @return Size create as "null" value.
     */
    public static Size none() {
        return new Size(Integer.MIN_VALUE);
    }
}
