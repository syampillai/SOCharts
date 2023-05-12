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
 * Interface to denote that a {@link ComponentPart} has {@link ItemStyle}.
 *
 * @author Syam
 */
public interface HasItemStyle {

    /**
     * Get the item-style for this part. (If <code>true</code> is passed as the parameter,
     * a new label will be created if not already exists).
     *
     * @param create Whether to create it or not.
     * @return ItemStyle.
     */
    ItemStyle getItemStyle(boolean create);

    /**
     * Set the item-style of this.
     *
     * @param itemStyle ItemStyle to set.
     */
    void setItemStyle(ItemStyle itemStyle);
}
