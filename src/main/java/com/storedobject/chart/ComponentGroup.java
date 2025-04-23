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

/**
 * Representation of a group of {@link Component}s put together. {@link ComponentGroup}s can be added to {@link SOChart}
 * using {@link SOChart#add(ComponentGroup...)}. This class is typically used to assemble complex custom charts that
 * require more than one {@link Component}s and {@link Chart} instances as their internal parts.
 * (For example: {@link GanttChart}).
 *
 * @author Syam
 */
public interface ComponentGroup extends Clickable {

    /**
     * Add parts of this group to {@link SOChart}. This will be invoked if the group was already added
     * to the {@link SOChart}, and it is about to be rendered.
     *
     * @param soChart Chart to which parts to be added.
     */
    void addParts(SOChart soChart);

    /**
     * Remove parts of this group from {@link SOChart}. This will be invoked when the group is removed from the
     * {@link SOChart}. This method should make sure that all the parts are removed properly including their
     * dependencies.
     *
     * @param soChart Chart from which parts to be removed.
     */
    void removeParts(SOChart soChart);

    /**
     * This method is invoked before the {@link #addParts(SOChart)} to verify that the {@link ComponentGroup} is valid.
     *
     * @throws ChartException Throws when in an invalid state.
     */
    default void validate() throws ChartException {
    }
}
