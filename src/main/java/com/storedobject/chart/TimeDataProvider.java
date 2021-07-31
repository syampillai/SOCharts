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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Date;

/**
 * Data provider to provide time values.
 *
 * @author Syam
 */
public interface TimeDataProvider extends AbstractDataProvider<LocalDateTime> {

    @Override
    default DataType getDataType() {
        return DataType.TIME;
    }

    /**
     * Add a time value.
     *
     * @param time time value to add.
     * @return True if added. Otherwise, false.
     */
    boolean add(LocalDateTime time);

    /**
     * Add a date value.
     *
     * @param date Date value to add.
     * @return True if added. Otherwise, false.
     */
    default boolean add(LocalDate date) {
        if(date == null) {
            return false;
        }
        return add(date.atStartOfDay());
    }

    /**
     * Add a date value.
     *
     * @param date Date value to add.
     * @return True if added. Otherwise, false.
     */
    default boolean add(Date date) {
        if(date == null) {
            return false;
        }
        return add(date.getTime());
    }

    /**
     * Add a time value as time in milliseconds.
     *
     * @param timeInMillis Time in milliseconds.
     * @return True if added. Otherwise, false.
     */
    default boolean add(long timeInMillis) {
        long nanos = (timeInMillis - ((timeInMillis / 1000L) * 1000L)) * 1000L;
        return add(LocalDateTime.ofEpochSecond(timeInMillis / 1000L, (int) nanos, ZoneOffset.UTC));
    }

    @Override
    default Comparator<LocalDateTime> getComparator() {
        return (d1, d2) -> {
            if(d1 == null || d2 == null) {
                return d1 == null && d2 == null ? 0 : (d1 == null ? -1 : 1);
            }
            return d1.isBefore(d2) ? -1 : (d1.isAfter(d2) ? 1 : 0);
        };
    }
}
