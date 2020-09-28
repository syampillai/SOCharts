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

import java.util.stream.Stream;

/**
 * Representation of tree-type data.
 *
 * @author Syam
 */
public interface TreeDataProvider extends ComponentPart {

    /**
     * Get data value.
     *
     * @return Value.
     */
    Number getValue();

    /**
     * Get the child nodes.
     *
     * @return Child nodes as a stream.
     */
    Stream<? extends TreeDataProvider> getChildren();

    @Override
    default void encodeJSON(StringBuilder sb) {
        String name = getName();
        if(name == null) {
            name = "Name?";
        }
        sb.append("{\"name\":").append(ComponentPart.escape(name)).append(",\"value\":").append(getValue());
        Stream<? extends TreeDataProvider> children = getChildren();
        if(children != null) {
            AbstractDataProvider.append(sb, children, ",\"children\":[", "]", false,
                    (buffer, v) -> ((TreeDataProvider)v).encodeJSON(sb));
        }
        sb.append('}');
    }

    @Override
    default void validate() {
    }
}
