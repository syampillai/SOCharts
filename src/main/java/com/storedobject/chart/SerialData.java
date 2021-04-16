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

import java.util.stream.Stream;

/**
 * Implementation of serially increasing/decreasing numbers as data.
 *
 * @author Syam
 */
public class SerialData implements AbstractDataProvider<Integer> {

    private final int start, end, step;
    private int serial;

    /**
     * Constructor.
     *
     * @param start Starting value.
     * @param end Ending value.
     */
    public SerialData(int start, int end) {
        this(start, end, 1);
    }

    /**
     * Constructor.
     *
     * @param start Starting value.
     * @param end Ending value.
     * @param step Step value.
     */
    public SerialData(int start, int end, int step) {
        this.step = step == 0 ? 1 : step;
        if(this.step > 0) {
            this.start = Math.min(start, end);
            this.end = Math.max(start, end);
        } else {
            this.start = Math.max(start, end);
            this.end = Math.min(start, end);
        }
    }

    @Override
    public Stream<Integer> stream() {
        return Stream.iterate(start, i -> (step > 1 ? i <= end : i >= end), i -> i + step);
    }

    @Override
    public final DataType getDataType() {
        return DataType.NUMBER;
    }

    @Override
    public final void setSerial(int serial) {
        this.serial = serial;
    }

    @Override
    public final int getSerial() {
        return serial;
    }
}
