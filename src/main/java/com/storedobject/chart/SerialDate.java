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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

/**
 * Implementation of serially increasing/decreasing date values as data.
 *
 * @author Syam
 */
public class SerialDate implements AbstractDataProvider<LocalDate> {

    private final LocalDate start, end;
    private final int step;
    private final ChronoUnit stepUnit;

    /**
     * Constructor.
     *
     * @param start Starting value.
     * @param end Ending value.
     */
    public SerialDate(LocalDate start, LocalDate end) {
        this(start, end, 0);
    }

    /**
     * Constructor.
     *
     * @param start Starting value.
     * @param end Ending value.
     * @param step Step value.
     */
    public SerialDate(LocalDate start, LocalDate end, int step) {
        this(start, end, step, ChronoUnit.DAYS);
    }

    /**
     * Constructor.
     *
     * @param start Starting value.
     * @param end Ending value.
     * @param step Step value.
     * @param stepUnit Unit of the step.
     */
    public SerialDate(LocalDate start, LocalDate end, int step, ChronoUnit stepUnit) {
        this.stepUnit = stepUnit;
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
    public Stream<LocalDate> stream() {
        return Stream.iterate(start, d -> step > 0 ? !d.isAfter(end) : !d.isBefore(end), d -> start.plus(step, stepUnit));
    }

    @Override
    public DataType getDataType() {
        return DataType.DATE;
    }
}
