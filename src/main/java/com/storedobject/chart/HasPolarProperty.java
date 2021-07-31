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
 * Interface to denote that a {@link ComponentPart} has {@link PolarProperty}.
 *
 * @author Syam
 */
public interface HasPolarProperty {

    /**
     * Get the polar property. (If <code>true</code> is passed as the parameter, a new polar
     * property will be created if not already exists).
     *
     * @param create Whether to create it or not.
     * @return Polar property.
     */
    PolarProperty getPolarProperty(boolean create);

    /**
     * Set the polar property.
     *
     * @param polarProperty Polar property to set. It could be <code>null</code>.
     */
    void setPolarProperty(PolarProperty polarProperty);


    /**
     * Set the hole radius (if a hole is required at the center).
     *
     * @param size Hole radius.
     */
    default void setHoleRadius(Size size) {
        PolarProperty pp = getPolarProperty(true);
        if(pp != null) {
            pp.setInnerRadius(size);
        }
    }

    /**
     * Set the radius.
     *
     * @param size Radius.
     */
    default void setRadius(Size size) {
        PolarProperty pp = getPolarProperty(true);
        if(pp != null) {
            pp.setRadius(size);
        }
    }
}
