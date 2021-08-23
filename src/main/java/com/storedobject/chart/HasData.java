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

import java.util.Set;

/**
 * Interface to denote that a {@link ComponentPart} owns some data.
 *
 * @author Syam
 */
public interface HasData {

    /**
     * Declare the data set owned by this {@link ComponentPart} by adding it to the {@link Set} provided.
     *
     * @param dataSet Set to which all the data owned by this {@link ComponentPart} needs to be added.
     */
    void declareData(Set<AbstractDataProvider<?>> dataSet);
}
