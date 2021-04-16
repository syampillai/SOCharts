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
 * Item style. (Used to define style of an individual item in many charts).
 *
 * @author Syam
 */
public class ItemStyle extends AbstractStyle {

    private Border border;

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(border != null) {
            border.encodeJSON(sb);
        }
    }

    /**
     * Get the border.
     * @param create Whether to create if not exists or not.
     *
     * @return Border.
     */
    public final Border getBorder(boolean create) {
        if(border == null && create) {
            border = new Border();
        }
        return border;
    }

    /**
     * Set the border.
     *
     * @param border Border.
     */
    public void setBorder(Border border) {
        this.border = border;
    }
}