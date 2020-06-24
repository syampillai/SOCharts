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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * Implementation of serially increasing/decreasing time values as data.
 *
 * @author Syam
 */
public class SerialTime implements AbstractDataProvider<LocalDateTime> {

    private final LocalDateTime start, end;
    private final int step;
    private final ChronoUnit stepUnit;

    /**
     * Constructor.
     *
     * @param start Starting value.
     * @param end Ending value.
     */
    public SerialTime(LocalDateTime start, LocalDateTime end) {
        this(start, end, 0, null);
    }

    /**
     * Constructor.
     *
     * @param start Starting value.
     * @param end Ending value.
     * @param stepUnit Unit of the step.
     */
    public SerialTime(LocalDateTime start, LocalDateTime end, ChronoUnit stepUnit) {
        this(start, end, 0, stepUnit);
    }

    /**
     * Constructor.
     *
     * @param start Starting value.
     * @param end Ending value.
     * @param step Step value.
     * @param stepUnit Unit of the step.
     */
    public SerialTime(LocalDateTime start, LocalDateTime end, int step, ChronoUnit stepUnit) {
        this.stepUnit = stepUnit == null ? ChronoUnit.MINUTES : stepUnit;
        this.step = step == 0 ? (start.isBefore(end) ? 1 : -1) : step;
        if(this.step > 0) {
            this.start = start.isBefore(end) ? start : end;
            this.end = end.isAfter(start) ? end : start;
        } else {
            this.start = start.isAfter(end) ? start : end;
            this.end = end.isBefore(start) ? end : start;
        }
    }

    @Override
    public Stream<LocalDateTime> stream() {
        return Stream.iterate(start, t -> step > 0 ? !t.isAfter(end) : !t.isBefore(end), t -> start.plus(step, stepUnit));
    }

    @Override
    public DataType getDataType() {
        return DataType.TIME;
    }
}