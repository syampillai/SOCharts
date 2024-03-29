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
 * Gantt chart.
 * <p>An instance of {@link Project} is required as the data for the Gantt chart.</p>
 * <p>Note: Unlike other charts that are instances of {@link Chart}, Gantt chart is composed of multiple charts and
 * other charting elements put together as a {@link ComponentGroup}. If you want to customize this, there are
 * several methods to get its components from this class and there are methods that can be overridden in the
 * {@link Project} class too.</p>
 *
 * @author Syam
 */
public class GanttChart extends AbstractGanttChart {

    /**
     * Constructor.
     *
     * @param project Project.
     */
    public GanttChart(Project project) {
        super(project);
    }

    /**
     * Get the task zoom component.
     *
     * @return Task zoom component.
     */
    public DataZoom getTaskAxisZoom() {
        return super.getYAxisZoom();
    }

    /**
     * Get the task axis of this Gantt chart.
     *
     * @return Task axis. (An instance of {@link YAxis}).
     */
    public YAxis getTaskAxis() {
        return super.getYAxis();
    }
}
