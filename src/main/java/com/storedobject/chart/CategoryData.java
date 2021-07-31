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
 * A list-based data provider for category data.
 * Category data is typically non-numeric kind of (string) data that can be used in many charts like
 * in the X-axis of a {@link BarChart}. It is also used when data/item labels are required, for example:
 * {@link PieChart}.
 *
 * @author Syam
 */
public class CategoryData extends AbstractData<String> implements CategoryDataProvider {

    /**
     * Constructor.
     *
     * @param data Initial data to add
     */
    public CategoryData(String... data) {
        super(String.class, data);
    }

    @Override
    public String getMin() {
        return isEmpty() ? null : get(0);
    }

    @Override
    public String getMax() {
        return isEmpty() ? null : get(size() - 1);
    }
}
