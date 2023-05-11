/*
 *  Copyright Syam Pillai
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
 * Data representation for {@link CandlestickChart}.
 *
 * @author Syam
 */
public class CandlestickData extends AbstractData<CandlestickData.Candlestick> {

    /**
     * Candlestick chart.
     *
     * @param data Data points to add.
     */
    public CandlestickData(Candlestick... data) {
        super(Candlestick.class, data);
    }

    /**
     * A structure to represent a data point in the candlestick chart.
     *
     * @author Syam
     *
     * @param opening Opening value
     * @param closing Closing value
     * @param low Lowe value
     * @param high High value
     */
    public record Candlestick(Number opening, Number closing, Number low, Number high) {

        @Override
        public String toString() {
            return "[" + opening + "," + closing + "," + low + "," + high + "]";
        }
    }
}
