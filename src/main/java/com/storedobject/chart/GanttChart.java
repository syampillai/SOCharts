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
 * several methods to get its components from this class or there are methods in the {@link Project} that can
 * be overridden and/or customized.</p>
 *
 * @author Syam
 */
public class GanttChart implements ComponentGroup {

    private RectangularCoordinate coordinate;
    private XAxis timeAxis;
    private YAxis taskAxis;
    private Today today;
    private final Project project;
    private final Text error;
    private Tasks tasks;
    private TaskAxisLabels taskAxisLabels;
    private TaskDependencies taskDependencies;
    private Tooltip tooltip;
    private TaskBands taskBands;
    private DataZoom timeZoom, taskZoom;

    /**
     * Constructor.
     *
     * @param project Project.
     */
    public GanttChart(Project project) {
        this.project = project;
        error = new Text("");
        error.setFont(new Font(Font.Family.sans_serif(), Font.Size.larger()));
        //noinspection ConstantConditions
        error.getPosition(true).center();
        error.setDraggable(true);
    }

    @Override
    public void addParts(SOChart soChart) {
        soChart.disableDefaultLegend();
        soChart.disableDefaultTooltip();
        soChart.remove(error);
        soChart.add(getCoordinateSystem());
        try {
            project.validateConstraints();
        } catch(ChartException e) {
            error.setText(e.getMessage());
            soChart.add(error);
            return;
        }
        if(project.isEmpty()) {
            error.setText("Empty project - " + project.getName());
            soChart.add(error);
            return;
        }
        if(today == null) {
            today = new Today();
        }
        if(taskBands == null) {
            taskBands = new TaskBands();
        }
        if(tasks == null) {
            tasks = new Tasks();
        }
        if(tooltip == null) {
            tooltip = new Tooltip();
            tooltip.append(project.taskTooltipLabels());
        }
        if(taskAxisLabels == null) {
            taskAxisLabels = new TaskAxisLabels();
        }
        if(taskDependencies == null) {
            taskDependencies = new TaskDependencies();
        }
        taskAxis.setMax(project.getTaskCount());
        soChart.add(taskBands, tasks, today, tooltip, taskAxisLabels, taskDependencies, getTimeAxisZoom(),
                getTaskAxisZoom());
    }

    @Override
    public void removeParts(SOChart soChart) {
        soChart.remove(coordinate, today, error, taskBands, tasks, today, tooltip, taskAxisLabels, taskDependencies,
                timeZoom, taskZoom);
    }

    /**
     * Get the task axis zoom component.
     *
     * @return Task-axis zoom component.
     */
    public DataZoom getTaskAxisZoom() {
        if(taskZoom == null) {
            taskZoom = new DataZoom(getCoordinateSystem(), getTaskAxis());
            taskZoom.setFilterMode(2);
            taskZoom.setShowDetail(false);
            taskZoom.setZ(7);
        }
        return taskZoom;
    }

    /**
     * Get the time axis zoom component.
     *
     * @return Time-axis zoom component.
     */
    public DataZoom getTimeAxisZoom() {
        if(timeZoom == null) {
            timeZoom = new DataZoom(getCoordinateSystem(), getTimeAxis());
            timeZoom.setFilterMode(2);
            timeZoom.setShowDetail(false);
            timeZoom.setZ(7);
        }
        return timeZoom;
    }

    /**
     * Return the co-ordinate system used by this Gantt chart. (It will be a rectangular co-ordinate system).
     *
     * @return Rectangular co-ordinate system. (An instance of {@link RectangularCoordinate}).
     */
    public RectangularCoordinate getCoordinateSystem() {
        if(coordinate == null) {
            coordinate = new RectangularCoordinate(getTimeAxis(), getTaskAxis());
            Position p = coordinate.getPosition(true);
            //noinspection ConstantConditions
            p.setTop(Size.pixels(40));
            p.setBottom(Size.pixels(60));
            p.setLeft(Size.pixels(225));
            p.setRight(Size.pixels(40));
        }
        return coordinate;
    }

    /**
     * Get the time axis.
     *
     * @return Time axis. (An instance of {@link XAxis}).
     */
    public XAxis getTimeAxis() {
        if(timeAxis == null) {
            timeAxis = new XAxis(project.getDurationType().isDateBased() ? DataType.DATE : DataType.TIME);
            timeAxis.getLabel(true).getAlignment(true).center();
            timeAxis.getTicks(true).hide();
            timeAxis.setZ(7);
        }
        return timeAxis;
    }

    /**
     * Get the task axis of this Gantt chart.
     *
     * @return Task axis. (An instance of {@link YAxis}).
     */
    public YAxis getTaskAxis() {
        if(taskAxis == null) {
            taskAxis = new YAxis(DataType.NUMBER);
            taskAxis.getLabel(true).hide();
            taskAxis.getTicks(true).hide();
            taskAxis.setMin(0);
        }
        return taskAxis;
    }

    private class TaskBands extends LineChart {

        private AbstractDataProvider<?> bands;

        private TaskBands() {
            setCustomRenderer("HBand");
            setZ(1);
            plotOn(getCoordinateSystem());
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
            if(bands == null) {
                bands = project.taskBands();
            }
            return bands;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            sb.append(",\"encode\":{\"x\":-1,\"y\":0}");
        }
    }

    private class Today extends LineChart {

        private AbstractDataProvider<?> data;

        private Today() {
            setCustomRenderer("VLine");
            setZ(6);
            plotOn(getCoordinateSystem());
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
            if(data == null) {
                data = project.dataForToday();
            }
            return data;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            sb.append(",\"encode\":{\"x\":0,\"y\":-1}");
        }
    }

    private class TaskAxisLabels extends LineChart {

        private AbstractDataProvider<?> data;

        private TaskAxisLabels() {
            setCustomRenderer("VAxisLabel");
            setZ(5);
            plotOn(getCoordinateSystem());
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
            if(data == null) {
                data = project.taskAxisLabels();
            }
            return data;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            sb.append(",\"encode\":{\"x\":-1,\"y\":0}");
        }
    }

    private class Tasks extends LineChart {

        private AbstractDataProvider<?> data;

        private Tasks() {
            setCustomRenderer("HBar");
            setZ(4);
            setName("Tasks");
            plotOn(getCoordinateSystem(), getTimeAxis(), getTaskAxis());
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
            if(data == null) {
                data = project.taskData();
            }
            return data;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            sb.append(",\"encode\":{\"x\":[2,3],\"y\":0}");
        }
    }

    private class TaskDependencies extends LineChart {

        private AbstractDataProvider<?> data;

        private TaskDependencies() {
            setCustomRenderer("Dependency");
            setZ(2);
            setName("Dependencies");
            plotOn(getCoordinateSystem(), getTimeAxis(), getTaskAxis());
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
            if(data == null) {
                data = project.taskDependencies();
            }
            return data;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            sb.append(",\"encode\":{\"x\":-1,\"y\":0}");
        }
    }
}
