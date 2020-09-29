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
 * Represents font-weight.
 *
 * @author Syam
 */
public class FontWeight {

    /**
     * Normal font-weight.
     */
    public static final FontWeight NORMAL = new FontWeight(0);
    /**
     * Bold font-weight.
     */
    public static final FontWeight BOLD = new FontWeight(-1);
    /**
     * Bolder font-weight.
     */
    public static final FontWeight BOLDER = new FontWeight(-2);
    /**
     * Lighter font-weight.
     */
    public static final FontWeight LIGHTER = new FontWeight(-3);

    private final int weight;

    /**
     * Constructor.
     *
     * @param weight Weight (typically 1, 2, 3 etc.)
     */
    public FontWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        if(weight <= 0) {
            switch (weight) {
                case -3:
                    return "\"lighter\"";
                case -2:
                    return "\"bolder\"";
                case -1:
                    return "\"bold\"";
                default:
                    return "\"normal\"";
            }
        }
        return "" + (weight * 100);
    }
}
