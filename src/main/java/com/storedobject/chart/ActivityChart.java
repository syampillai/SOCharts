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
 * Activity chart is like a {@link GanttChart} but instead of a {@link Project} instance as its data, it uses
 * an {@link ActivityList} instance as its data.
 * <p>Note: Unlike other charts that are instances of {@link Chart}, Activity chart is composed of multiple charts and
 * other charting elements put together as a {@link ComponentGroup}. If you want to customize this, there are
 * several methods to get its components from this class and there are methods that can be overridden in the
 * {@link ActivityList} class too.</p>
 *
 * @author Syam
 */
public class ActivityChart extends AbstractGanttChart {

    /**
     * Constructor.
     *
     * @param activityList Activity list.
     */
    public ActivityChart(ActivityList activityList) {
        super(activityList);
    }

    /**
     * Get the activity zoom component.
     *
     * @return Activity zoom component.
     */
    public DataZoom getActivityAxisZoom() {
        return super.getYAxisZoom();
    }

    /**
     * Get the activity axis of this Gantt chart.
     *
     * @return Activity axis. (An instance of {@link YAxis}).
     */
    public YAxis getActivityAxis() {
        return super.getYAxis();
    }
}
