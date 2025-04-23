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
 * Abstract Gantt chart is the base class for {@link GanttChart} and {@link ActivityChart}.
 *
 * @author Syam
 */
public abstract class AbstractGanttChart implements ComponentGroup, Clickable {

    private RectangularCoordinate coordinate;
    private XAxis timeAxis;
    private YAxis taskAxis;
    private Today today;
    private final AbstractProject project;
    private final Text error;
    private Tasks tasks;
    private TaskAxisLabels taskAxisLabels;
    private TaskDependencies taskDependencies;
    private TaskBands taskBands;
    private DataZoom timeZoom, taskZoom;
    private boolean clickable = false;

    /**
     * Constructor.
     *
     * @param project Project.
     */
    public AbstractGanttChart(AbstractProject project) {
        this.project = project;
        error = new Text("");
        error.setFont(new Font(Font.Family.sans_serif(), Font.Size.larger()));
        //noinspection ConstantConditions
        error.getPosition(true).center();
        error.setDraggable(true);
    }

    @Override
    public boolean matchSource(ChartEvent event) {
        return Clickable.matchSource(event, taskBands, tasks, today, taskAxisLabels, taskDependencies, timeZoom,
                taskZoom, coordinate, timeAxis, taskAxis, error);
    }

    @Override
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
        Clickable.setClickable(clickable, taskBands, tasks, today, taskAxisLabels, taskDependencies, timeZoom,
                taskZoom, coordinate, timeAxis, taskAxis, error);
    }

    @Override
    public boolean isClickable() {
        return clickable;
    }

    @Override
    public void addParts(SOChart soChart) {
        soChart.disableDefaultLegend();
        soChart.remove(error);
        soChart.add(getC());
        try {
            project.validateConstraints();
        } catch (ChartException e) {
            error.setText(e.getMessage());
            soChart.add(error);
            return;
        }
        if (project.isEmpty()) {
            error.setText("Empty project - " + project.getName());
            soChart.add(error);
            return;
        }
        if (today == null) {
            AbstractDataProvider<?> data = project.dataForToday();
            if (data != null) {
                today = new Today(data);
            }
        }
        if (taskBands == null) {
            taskBands = new TaskBands();
        }
        if (tasks == null) {
            tasks = new Tasks();
        }
        if (taskAxisLabels == null) {
            taskAxisLabels = new TaskAxisLabels();
        }
        if (taskDependencies == null) {
            AbstractDataProvider<?> depData = project.dependencies();
            if (depData != null) {
                taskDependencies = new TaskDependencies(depData);
            }
        }
        taskAxis.setMax(project.getRowCount());
        soChart.add(taskBands, tasks, today, taskAxisLabels, taskDependencies, getTimeAxisZoom(),
                getYAxisZoom());
    }

    @Override
    public void removeParts(SOChart soChart) {
        soChart.remove(coordinate, today, error, taskBands, tasks, today, taskAxisLabels, taskDependencies,
                timeZoom, taskZoom);
    }

    /**
     * Get the Y-axis zoom component.
     *
     * @return Y-axis zoom component.
     */
    DataZoom getYAxisZoom() {
        if (taskZoom == null) {
            taskZoom = new DataZoom(getC(), getYAxis());
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
        if (timeZoom == null) {
            timeZoom = new DataZoom(getC(), getTimeAxis());
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
        if (coordinate == null) {
            coordinate = new RectangularCoordinate(getTimeAxis(), getYAxis());
            Position p = coordinate.getPosition(true);
            //noinspection ConstantConditions
            p.setTop(Size.pixels(40));
            p.setBottom(Size.pixels(60));
            p.setLeft(Size.pixels(225));
            p.setRight(Size.pixels(40));
        }
        return coordinate;
    }

    private CoordinateSystem getC() {
        return getCoordinateSystem();
    }

    /**
     * Get the time axis.
     *
     * @return Time axis. (An instance of {@link XAxis}).
     */
    public XAxis getTimeAxis() {
        if (timeAxis == null) {
            timeAxis = new XAxis(project.getDurationType().isDateBased() ? DataType.DATE : DataType.TIME);
            timeAxis.getLabel(true).getAlignment(true).center();
            timeAxis.getTicks(true).hide();
            timeAxis.setZ(7);
        }
        return timeAxis;
    }

    /**
     * Get the Y-axis of this Gantt chart.
     *
     * @return Y-axis. (An instance of {@link YAxis}).
     */
    YAxis getYAxis() {
        if (taskAxis == null) {
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
            plotOn(getC());
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
            if (bands == null) {
                bands = project.bands();
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

        private final AbstractDataProvider<?> data;

        private Today(AbstractDataProvider<?> data) {
            this.data = data;
            setCustomRenderer("VLine");
            setZ(6);
            plotOn(getC());
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
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
            plotOn(getC());
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
            if (data == null) {
                data = project.axisLabels();
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
            plotOn(getC(), getTimeAxis(), getYAxis());
            Tooltip tooltip = new Tooltip();
            tooltip.append(project.tooltipLabels());
            setTooltip(tooltip);
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
            if (data == null) {
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

        private final AbstractDataProvider<?> data;

        private TaskDependencies(AbstractDataProvider<?> data) {
            setCustomRenderer("Dependency");
            this.data = data;
            setZ(2);
            setName("Dependencies");
            plotOn(getC(), getTimeAxis(), getYAxis());
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
            return data;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            sb.append(",\"encode\":{\"x\":-1,\"y\":0}");
        }
    }
}