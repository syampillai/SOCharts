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

/**
 * Time data provider. (A list-based data provider).
 *
 * @author Syam
 */
public class TimeData extends AbstractData<LocalDateTime> implements TimeDataProvider {

    /**
     * Constructor.
     *
     * @param data Initial data to add
     */
    public TimeData(LocalDateTime... data) {
        super(LocalDateTime.class, data);
    }
}