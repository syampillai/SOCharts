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
 * <p>
 * Represents some combined attributes/properties owned by a {@link Component} or {@link ComponentPart}.
 * </p>
 * <p>
 * An example of an implementation of this interface is {@link Position} that is used by {@link Component}s like
 * {@link RectangularCoordinate} to position them within the chart display area defined by the {@link SOChart}. The
 * typical use-case of {@link ComponentProperty} is as follows:
 * </p>
 * <pre>
 *     RectangularCoordinate rc = new RectangularCoordinate();
 *     rc.getPosition(true).setTop(Size.percentage(50));
 * </pre>
 * <p>
 * Please notice that the getPosition(create = true) call uses "true" as the parameter to create a position if
 * it doesn't already exist. This is because, in most cases, default values will be automatically applied if no
 * property values are present and it will save on the client communication.
 * </p>
 *
 * @author Syam
 */
public interface ComponentProperty {

    /**
     * Encode the JSON string with the properties of this.
     *
     * @param sb Encoded JSON string to be appended to this.
     */
    void encodeJSON(StringBuilder sb);
}
