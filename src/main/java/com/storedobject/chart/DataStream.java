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
 * A data provider implementation to provide data from a {@link Stream}.
 *
 * @author Syam
 */
public final class DataStream extends AbstractDataStream<Number> {

    /**
     * Constructor.
     *
     * @param dataStream Data stream that provides the data.
     */
    public DataStream(Stream<Number> dataStream) {
        super(DataType.NUMBER, dataStream);
    }
}
