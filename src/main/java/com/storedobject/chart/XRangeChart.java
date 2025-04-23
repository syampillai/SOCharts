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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * X-range chart. X-range charts are used to visualize a range on the X-axis. It is plotted on a
 * {@link RectangularCoordinate} system a range of values on the X-axis with corresponding Y-axis values.
 * Each X range-value is represented using a horizontal bar. Typically, X-values are of {@link DataType#NUMBER},
 * {@link DataType#DATE} or {@link DataType#TIME} and Y-values are of {@link DataType#CATEGORY}. However, the
 * Y-values could be of other types too.
 * <p>Note: This chart automatically creates its coordinate system and the required axes.</p>
 *
 * @param <X> Type of data on the X-axis.
 * @param <Y> Type of data on the Y-axis.
 * @author Syam
 */
public class XRangeChart<X, Y> implements ComponentGroup {

    private final List<Data> data = new ArrayList<>();
    private XAxis xAxis;
    private YAxis yAxis;
    private RectangularCoordinate coordinate;
    private RChart chart;
    private CategoryDataProvider yData;
    private byte showProgress = -1;
    private boolean showProgressLabel = false;
    private DataZoom xZoom, yZoom;

    @Override
    public boolean matchSource(ChartEvent event) {
        return Clickable.matchSource(event, xAxis, yAxis, chart, chart, xZoom, yZoom);
    }

    /**
     * Add a data value.
     *
     * @param xLowerValue Lower value of the X-range.
     * @param xUpperValue Upper value of the X-range.
     * @param yValue Y-value.
     * @param label A label to be rendered on the bar. (Could be null or empty string if you don't want a label).
     * @param color Color of the bar.
     */
    public void addData(X xLowerValue, X xUpperValue, Y yValue, String label, Color color) {
        addData(xLowerValue, xUpperValue, yValue, label, color, 0);
    }

    /**
     * Add a data value.
     *
     * @param xLowerValue Lower value of the X-range.
     * @param xUpperValue Upper value of the X-range.
     * @param yValue Y-value.
     * @param label A label to be rendered on the bar. (Could be null or empty string if you don't want a label).
     * @param color Color of the bar.
     * @param extraInfo Extra information (Can be used to set the tooltip text for the bar).
     */
    public void addData(X xLowerValue, X xUpperValue, Y yValue, String label, Color color, String extraInfo) {
        addData(xLowerValue, xUpperValue, yValue, label, color, 0, extraInfo);
    }

    /**
     * Add a data value.
     *
     * @param xLowerValue Lower value of the X-range.
     * @param xUpperValue Upper value of the X-range.
     * @param yValue Y-value.
     * @param color Color of the bar.
     * @param progressPercentage Progress percentage. (Will be shown on the bar with a different color).
     */
    public void addData(X xLowerValue, X xUpperValue, Y yValue, Color color, double progressPercentage) {
        addData(xLowerValue, xUpperValue, yValue, null, color, progressPercentage);
    }

    /**
     * Add a data value.
     *
     * @param xLowerValue Lower value of the X-range.
     * @param xUpperValue Upper value of the X-range.
     * @param yValue Y-value.
     * @param color Color of the bar.
     * @param progressPercentage Progress percentage. (Will be shown on the bar with a different color).
     * @param extraInfo Extra information (Can be used to set the tooltip text for the bar).
     */
    public void addData(X xLowerValue, X xUpperValue, Y yValue, Color color, double progressPercentage,
                        String extraInfo) {
        addData(xLowerValue, xUpperValue, yValue, null, color, progressPercentage, extraInfo);
    }

    /**
     * Add a data value.
     *
     * @param xLowerValue Lower value of the X-range.
     * @param xUpperValue Upper value of the X-range.
     * @param yValue Y-value.
     * @param color Color of the bar.
     */
    public void addData(X xLowerValue, X xUpperValue, Y yValue, Color color) {
        addData(xLowerValue, xUpperValue, yValue, null, color, 0, null);
    }

    /**
     * Add a data value.
     *
     * @param xLowerValue Lower value of the X-range.
     * @param xUpperValue Upper value of the X-range.
     * @param yValue Y-value.
     * @param color Color of the bar.
     * @param extraInfo Extra information (Can be used to set the tooltip text for the bar).
     */
    public void addData(X xLowerValue, X xUpperValue, Y yValue, Color color, String extraInfo) {
        addData(xLowerValue, xUpperValue, yValue, null, color, 0, extraInfo);
    }

    /**
     * Add a data value.
     *
     * @param xLowerValue Lower value of the X-range.
     * @param xUpperValue Upper value of the X-range.
     * @param yValue Y-value.
     * @param label A label to be rendered on the bar. (Could be null or empty string if you don't want a label).
     * @param color Color of the bar.
     * @param progressPercentage Progress percentage. (Will be shown on the bar with a different color).
     */
    public void addData(X xLowerValue, X xUpperValue, Y yValue, String label, Color color, double progressPercentage) {
        addData(xLowerValue, xUpperValue, yValue, label, color, progressPercentage, null);
    }

    /**
     * Add a data value.
     *
     * @param xLowerValue Lower value of the X-range.
     * @param xUpperValue Upper value of the X-range.
     * @param yValue Y-value.
     * @param label A label to be rendered on the bar. (Could be null or empty string if you don't want a label).
     * @param color Color of the bar.
     * @param progressPercentage Progress percentage. (Will be shown on the bar with a different color).
     * @param extraInfo Extra information (Can be used to set the tooltip text for the bar).
     */
    public void addData(X xLowerValue, X xUpperValue, Y yValue, String label, Color color, double progressPercentage,
                        String extraInfo) {
        data.add(new Data(xLowerValue, xUpperValue, yValue, label, color, progressPercentage, extraInfo));
    }

    /**
     * Clear all data values.
     */
    public void clearData() {
        data.clear();
    }

    @Override
    public void addParts(SOChart soChart) {
        if(chart == null) {
            chart = new RChart();
        }
        soChart.addData(yData);
        soChart.add(chart, xZoom, yZoom);
    }

    @Override
    public void removeParts(SOChart soChart) {
        soChart.remove(chart, xZoom, yZoom);
    }

    /**
     * Get the data zoom component for the X-axis.
     *
     * @param create Create if not already exists.
     * @return Zoom data.
     */
    public DataZoom getXZoom(boolean create) {
        if(xZoom == null && create) {
            xZoom = new DataZoom(getCoordinateSystem(), getXAxis());
            xZoom.setFilterMode(2);
            xZoom.setShowDetail(false);
            xZoom.setZ(7);
        }
        return xZoom;
    }

    /**
     * Get the data zoom component for the Y-axis.
     *
     * @param create Create if not already exists.
     * @return Zoom data.
     */
    public DataZoom getYZoom(boolean create) {
        if(yZoom == null && create) {
            yZoom = new DataZoom(getCoordinateSystem(), getYAxis());
            yZoom.setFilterMode(2);
            yZoom.setShowDetail(false);
            yZoom.setZ(7);
        }
        return yZoom;
    }

    /**
     * Whether to render a progress bar or not.
     *
     * @param showProgress True/false.
     */
    public void showProgressBar(boolean showProgress) {
        this.showProgress = (byte) (showProgress ? 1 : 0);
    }

    /**
     * Whether to render a progress label or not.
     *
     * @param showProgressLabel True/false.
     */
    public void showProgressLabel(boolean showProgressLabel) {
        this.showProgressLabel = showProgressLabel;
    }

    /**
     * Get the X-axis.
     *
     * @return X-axis.
     */
    public XAxis getXAxis() {
        if(xAxis == null) {
            if(!data.isEmpty()) {
                xAxis = new XAxis(DataType.guessType(data.getFirst().xLowerValue));
            }
        }
        return xAxis;
    }

    /**
     * Get the Y-axis.
     *
     * @return Y-axis.
     */
    public YAxis getYAxis() {
        if(yAxis == null) {
            if(!data.isEmpty()) {
                yAxis = new YAxis(DataType.guessType(data.getFirst().yValue));
            }
        }
        return yAxis;
    }

    /**
     * Set data for the Y-Axis. This may be required to render the Y-axis labels in proper order. However, it is not
     * required unless the Y-axis is not of {@link DataType#CATEGORY}.
     *
     * @param yData Y-data.
     */
    public void setYData(CategoryDataProvider yData) {
        if(getYAxis().getDataType().equals(DataType.CATEGORY)) {
            this.yData = yData;
            yAxis.setData(yData);
        }
    }

    /**
     * Return the co-ordinate system used by this chart. (It will be a rectangular co-ordinate system).
     *
     * @return Rectangular co-ordinate system. (An instance of {@link RectangularCoordinate}).
     */
    public RectangularCoordinate getCoordinateSystem() {
        if(coordinate == null) {
            coordinate = new RectangularCoordinate(getXAxis(), getYAxis());
            Position p = coordinate.getPosition(true);
            //noinspection ConstantConditions
            p.setBottom(Size.pixels(60));
        }
        return coordinate;
    }

    /**
     * Format a value to show as part of the tooltip. (A null may be returned to ignore the value).
     *
     * @param value Value to format.
     * @return Formatted value.
     */
    public String formatValue(Object value) {
        if(value instanceof LocalDate d) {
            return DateTimeFormatter.ofPattern("MMM dd, yyyy").format(d);
        }
        if(value instanceof LocalDateTime d) {
            return DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss").format(d);
        }
        return value.toString();
    }

    /**
     * Format a Y-value to show as part of the tooltip. (A null may be returned to ignore the value).
     * By default, it invokes {@link #formatValue(Object)} and adds a colon followed by a space.
     *
     * @param value Value to format.
     * @return Formatted value.
     */
    public String formatYValue(Y value) {
        String s = formatValue(value);
        return s == null ? null : (s + ": ");
    }

    /**
     * Format an X-value to show as part of the tooltip. (A null may be returned to ignore the value).
     * By default, it invokes {@link #formatValue(Object)}.
     *
     * @param value Value to format.
     * @return Formatted value.
     */
    public String formatXValue(X value) {
        return formatValue(value);
    }

    /**
     * Format the X-values to show as part of the tooltip. (A null may be returned to ignore the value).
     * By default, it invokes {@link #formatXValue(Object)} for each of the values and concatenate it with a
     * dash (" - ") as the delimiter.
     *
     * @param xLowerValue Lower value of the X-range.
     * @param xUpperValue Upper value of the X-range.
     * @return Formatted value.
     */
    public String formatXValue(X xLowerValue, X xUpperValue) {
        String s = formatXValue(xLowerValue);
        if(s == null) {
            return null;
        }
        return s + " - " + formatXValue(xUpperValue);
    }

    /**
     * Format a data point to show it as the tooltip.
     *
     *  @param xLowerValue Lower value of the X-range.
     * @param xUpperValue Upper value of the X-range.
     * @param yValue Y-value.
     * @param label Label.
     * @param progress Progress percentage.
     * @param extraInfo Extra information.
     * @return Formatted value.
     */
    public String getTooltipText(X xLowerValue, X xUpperValue, Y yValue, @SuppressWarnings("unused") String label,
                                 @SuppressWarnings("unused") double progress, String extraInfo) {
        String sb = "";
        String s = formatYValue(yValue);
        if(s != null) {
            sb += s;
        }
        s = formatXValue(xLowerValue, xUpperValue);
        if(s != null) {
            sb += s;
        }
        if(extraInfo != null) {
            if(sb.isEmpty()) {
                return extraInfo;
            }
            sb += "\n" + extraInfo;
        }
        return sb;
    }

    private class Data {

        final X xLowerValue, xUpperValue;
        final Y yValue;
        final String label;
        final Color color;
        final double progress;
        final String extraInfo;

        private Data(X xLowerValue, X xUpperValue, Y yValue, String label, Color color, double progress,
                     String extraInfo) {
            this.xLowerValue = xLowerValue;
            this.xUpperValue = xUpperValue;
            this.yValue = yValue;
            this.label = label;
            this.color = color;
            this.progress = Math.min(Math.max(0, progress), 100);
            this.extraInfo = extraInfo;
        }

        String label() {
            return (label == null ? "" : label) + p();
        }

        private String p() {
            if(!showProgressLabel) {
                return "";
            }
            return " (" + AbstractProject.trim(progress) + "%)";
        }

        private String tooltip() {
            String s = getTooltipText(xLowerValue, xUpperValue, yValue, label, progress, extraInfo);
            while(s != null && s.endsWith("\n")) {
                s = s.substring(0, s.length() - 1);
            }
            return s == null ? "" : s.replace("\n", "<br>");
        }
    }

    private class RChart extends LineChart {

        private AbstractDataProvider<?> data;

        private RChart() {
            setCustomRenderer("HBar");
            plotOn(XRangeChart.this.getCoordinateSystem(), getXAxis(), getYAxis());
            Tooltip tooltip = new Tooltip();
            tooltip.append(new BasicDataProvider<String>() {

                @Override
                public Stream<String> stream() {
                    return XRangeChart.this.data.stream().map(Data::tooltip);
                }

                @Override
                public DataType getDataType() {
                    return DataType.CATEGORY;
                }
            });
            setTooltip(tooltip);
        }

        @Override
        protected AbstractDataProvider<?> dataToEmbed() {
            if(data == null) {
                boolean progress;
                if(showProgress == -1) {
                    progress = XRangeChart.this.data.stream().anyMatch(d -> d.progress > 0);
                } else {
                    progress = showProgress == 1;
                }
                data = new BasicDataProvider<String>() {

                    @Override
                    public Stream<String> stream() {
                        return XRangeChart.this.data.stream().map(d ->
                                "[" + yAxis.getDataType().encode(d.yValue) + ",\"" + d.label() + "\","
                                + xAxis.getDataType().encode(d.xLowerValue) + ","
                                + xAxis.getDataType().encode(d.xUpperValue) + ","
                                + (progress ? d.progress : 100) + "," + d.color + "," + d.color + ","
                                + (progress ? "\"black\"" : d.color) + "]");
                    }

                    @Override
                    public void encode(StringBuilder sb, String value) {
                        sb.append(value);
                    }
                };
            }
            return data;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            sb.append(",\"encode\":{\"x\":[2,3],\"y\":0}");
        }
    }
}
