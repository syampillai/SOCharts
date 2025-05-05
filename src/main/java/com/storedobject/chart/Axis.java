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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract representation of an axis.
 *
 * @author Syam
 */
public abstract class Axis extends VisiblePart implements Wrapped, Clickable {

    /**
     * Definition of pointer types.
     *
     * @author Syam
     */
    public enum PointerType {
        /**
         * Pointer as a line.
         */
        LINE,
        /**
         * Pointer as a cross-hair.
         */
        CROSS_HAIR,
        /**
         * No pointer.
         */
        NONE
    }
    private DataType dataType;
    Map<CoordinateSystem, AxisWrapper> wrappers = new HashMap<>();
    private String name;
    private int nameGap = 15, nameRotation = 0;
    private Location nameLocation;
    private TextStyle nameTextStyle;
    private boolean inverted = false;
    private Object min, max;
    private int divisions = 0;
    private boolean showZero = true;
    private Label label;
    private MinorTicks minorTicks;
    private Ticks ticks;
    private GridLines gridLines;
    private MinorGridLines minorGridLines;
    private GridAreas gridAreas;
    private Pointer pointer;
    private int renderingIndex;
    private AbstractDataProvider<?> data;
    private boolean clickable = false;
    SOChart soChart;

    /**
     * Constructor.
     *
     * @param dataType Axis data type.
     */
    public Axis(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Constructor.
     *
     * @param data Data type will be determined from the data provider.
     */
    public Axis(AbstractDataProvider<?> data) {
        this(data.getDataType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Axis axis = (Axis) o;
        return getId() == axis.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    String axisName() {
        Class<?> c = getClass();
        while(!c.getName().startsWith("com.storedobject.chart")) {
            c = c.getSuperclass();
        }
        String name = c.getName();
        name = name.substring(name.lastIndexOf('.') + 1);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    Object value(Object value) {
        if(value == null) {
            return null;
        }
        if(dataType != null) {
            if(dataType == DataType.CATEGORY) {
                return value;
            }
            if(dataType == DataType.TIME && value instanceof LocalDate) {
                value = ((LocalDate)value).atStartOfDay();
            } else if(dataType == DataType.DATE && value instanceof LocalDateTime) {
                value = ((LocalDateTime)value).toLocalDate();
            }
            return dataType.getType().isAssignableFrom(value.getClass()) ? value : null;
        }
        for(DataType dt: DataType.values()) {
            if(dt.getType().isAssignableFrom(value.getClass())) {
                dataType = dt;
                return value;
            }
        }
        return value;
    }

    @Override
    public void validate() throws ChartException{
        if(dataType == null) {
            String name = getName();
            if(name == null) {
                name = ComponentPart.className(getClass());
            }
            throw new ChartException("Unable to determine the data type for this axis - " + name);
        }
    }

    @Override
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    public boolean isClickable() {
        return clickable;
    }

    /**
     * Set the data for this axis.
     * <p>Note: Normally, this is not required to be set unless you are working on a custom chart that is not yet
     * supported.</p>
     * @param data Data to be used for this axis.
     */
    public void setData(AbstractDataProvider<?> data) {
        this.data = data;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(data != null) {
            sb.append(",\"data\":").append(data.getSerial());
        }
        if(inverted) {
            sb.append(",\"inverse\":true");
        }
        sb.append(",\"type\":").append(dataType);
        if(name != null) {
            ComponentPart.encode(sb, "name", name);
            if(nameLocation != null) {
                sb.append(",\"nameLocation\":").append(nameLocation);
            }
            sb.append(",\"nameGap\":").append(nameGap);
            sb.append(",\"nameRotate\":").append(nameRotation);
            ComponentPart.encode(sb, "nameTextStyle", nameTextStyle);
        }
        ComponentPart.encode(sb, "axisLabel", label);
        min = value(min);
        if(min != null) {
            ComponentPart.encode(sb, "min", min);
        }
        max = value(max);
        if(max != null) {
            ComponentPart.encode(sb, "max", max);
        }
        if(dataType != DataType.CATEGORY) {
            if(divisions > 0) {
                sb.append(",\"splitNumber\":").append(divisions);
            }
            if(min == null && max == null) {
                sb.append(",\"scale\":").append(!showZero);
            }
        }
        ComponentPart.encode(sb, "axisTick", ticks);
        ComponentPart.encode(sb, "minorTick", minorTicks);
        ComponentPart.encode(sb, "splitLine", gridLines);
        ComponentPart.encode(sb, "minorSplitLine", minorGridLines);
        ComponentPart.encode(sb, "splitArea", gridAreas);
        ComponentPart.encode(sb, "axisPointer", pointer);
    }

    @Override
    public void setRenderingIndex(int renderingIndex) {
        this.renderingIndex = renderingIndex;
    }

    @Override
    public int getRenderingIndex() {
        return renderingIndex;
    }

    /**
     * Get the name of the axis.
     *
     * @return Name of the axis.
     */
    public final String getName() {
        return name;
    }

    /**
     * Set the name of the axis.
     *
     * @param name Name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Invert the axis. If called, the drawing of the axis will be inverted (drawn in the opposite direction).
     */
    public void invert() {
        inverted = true;
    }

    /**
     * Get the gap between the axis and the name in pixels. (It can have negative values).
     *
     * @return Gap in pixels.
     */
    public int getNameGap() {
        return nameGap;
    }

    /**
     * Set the gap between the axis and the name in pixels. (It can have negative values).
     *
     * @param nameGap Gap in pixels.
     */
    public void setNameGap(int nameGap) {
        this.nameGap = nameGap;
    }

    /**
     * Get the rotation of the name in degrees.
     *
     * @return Rotation.
     */
    public int getNameRotation() {
        return nameRotation;
    }

    /**
     * Set the rotation of the name in degrees.
     *
     * @param nameRotation Rotation.
     */
    public void setNameRotation(int nameRotation) {
        this.nameRotation = nameRotation;
    }

    /**
     * Get the location of the name.
     *
     * @return Location of the name.
     */
    public Location getNameLocation() {
        return nameLocation;
    }

    /**
     * Set the location of the name.
     *
     * @param nameLocation Location to set.
     */
    public void setNameLocation(Location nameLocation) {
        this.nameLocation = Location.h(nameLocation);
    }

    /**
     * Get the text style for the name.
     *
     * @param create Whether to create if not exists or not.
     * @return Text style.
     */
    public final TextStyle getNameTextStyle(boolean create) {
        if(nameTextStyle == null && create) {
            nameTextStyle = new TextStyle();
        }
        return nameTextStyle;
    }

    /**
     * Set the text style or the name.
     *
     * @param nameTextStyle Text style to set.
     */
    public void setNameTextStyle(TextStyle nameTextStyle) {
        this.nameTextStyle = nameTextStyle;
    }

    /**
     * Set the minimum value for the axis.
     *
     * @param min Minimum value. (For category axis, it could be just an ordinal number of the categories). Note: The type
     *            of the value must be compatible with the data type of the axis. Otherwise, it will be ignored.
     */
    public void setMin(Number min) {
        this.min = value(min);
    }

    /**
     * By invoking this method, the minimum of the axis will be set as the minimum value of the data.
     */
    public void setMinAsMinData() {
        min = "dataMin";
    }

    /**
     * Set the maximum value for the axis.
     *
     * @param max Maximum value. (For category axis, it could be just an ordinal number of the categories). Note: The type
     *            of the value must be compatible with the data type of the axis. Otherwise, it will be ignored.
     */
    public void setMax(Object max) {
        this.max = value(max);
    }

    /**
     * By invoking this method, the maximum of the axis will be set as the maximum value of the data.
     */
    public void setMaxAsMaxData() {
        max = "dataMax";
    }

    /**
     * Set the number of divisions on the axis. This will be used as a hint only because for actual divisions will
     * be determined based on the readability too. Also, this is not applicable to category type axes.
     *
     * @param divisions Number of divisions.
     */
    public void setDivisions(int divisions) {
        this.divisions = divisions;
    }

    /**
     * Set whether zero positions to be shown or not. On certain charts, it may be required not to show zero
     * positions of the axis. Note: this setting is ignored if minimum and maximum values are already set. Also,
     * this is not applicable to category type axes.
     *
     * @param show True or false.
     */
    public void showZeroPosition(boolean show) {
        this.showZero = show;
    }

    /**
     * Get the label.
     *
     * @param create Whether to create if not exists or not.
     * @return Label.
     */
    public final Label getLabel(boolean create) {
        if(label == null && create) {
            label = new Label();
        }
        return label;
    }

    /**
     * Set the label for the axis.
     *
     * @param label Label.
     */
    public void setLabel(Label label) {
        this.label = label;
    }

    /**
     * Get the axis-line ticks.
     *
     * @param create Whether to create if not exists or not.
     * @return Tick.
     */
    public final Ticks getTicks(boolean create) {
        if(ticks == null && create) {
            ticks = new Ticks();
        }
        return ticks;
    }

    /**
     * Set the axis-line ticks.
     *
     * @param ticks Ticks.
     */
    public void setTicks(Ticks ticks) {
        this.ticks = ticks;
    }

    /**
     * Get the minor tick of the axis-line.
     *
     * @param create Whether to create if not exists or not.
     * @return Minor ticks.
     */
    public final MinorTicks getMinorTicks(boolean create) {
        if(minorTicks == null && create) {
            minorTicks = new MinorTicks();
        }
        return minorTicks;
    }

    /**
     * Set the minor of the axis-line.
     *
     * @param ticks Minor ticks.
     */
    public void setMinorTicks(MinorTicks ticks) {
        this.minorTicks = ticks;
    }

    /**
     * Get the grid-lines of the axis.
     *
     * @param create Whether to create if not exists or not.
     * @return Grid-lines.
     */
    public final GridLines getGridLines(boolean create) {
        if(gridLines == null && create) {
            gridLines = new GridLines();
        }
        return gridLines;
    }

    /**
     * Set the grid-lines for the axis.
     *
     * @param gridLines Grid-lines to set.
     */
    public void setGridLines(GridLines gridLines) {
        this.gridLines = gridLines;
    }

    /**
     * Get the minor grid-lines of the axis.
     *
     * @param create Whether to create if not exists or not.
     * @return Grid-lines.
     */
    public final MinorGridLines getMinorGridLines(boolean create) {
        if(minorGridLines == null && create) {
            minorGridLines = new MinorGridLines();
        }
        return minorGridLines;
    }

    /**
     * Set the minor grid-lines for the axis.
     *
     * @param gridLines Grid-lines to set.
     */
    public void setMinorGridLines(MinorGridLines gridLines) {
        this.minorGridLines = gridLines;
    }

    /**
     * Get the grid-areas for the axis.
     *
     * @param create Whether to create if not exists or not.
     * @return Grid-areas.
     */
    public final GridAreas getGridAreas(boolean create) {
        if(gridAreas == null && create) {
            gridAreas = new GridAreas();
        }
        return gridAreas;
    }

    /**
     * Set the grid-areas for the axis.
     *
     * @param gridAreas Grid-areas to set.
     */
    public void setGridAreas(GridAreas gridAreas) {
        this.gridAreas = gridAreas;
    }

    /**
     * Get the axis-pointer for the axis. (By default, no axis-pointer will be shown. However, if a call is
     * made to this method with <code>create = true</code>, an axis-pointer will be made visible. Then, you may use
     * the {@link Pointer#hide()} method to hide it if required.)
     *
     * @param create Whether to create if not exists or not.
     * @return Axis-pointer.
     */
    public final Pointer getPointer(boolean create) {
        if(pointer == null && create) {
            pointer = new Pointer();
        }
        return pointer;
    }

    /**
     * Set the axis-pointer for this axis.
     *
     * @param pointer Axis-pointer.
     */
    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }

    /**
     * Get the data type of this axis.
     *
     * @return Data type.
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Represents the label used by {@link Axis}.
     *
     * @author Syam
     */
    public static class Label extends com.storedobject.chart.Label {

        private Boolean showMaxLabel, showMinLabel;
        private int interval = Integer.MIN_VALUE;

        /**
         * Constructor.
         */
        public Label() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            if(showMinLabel != null) {
                sb.append(",\"showMinLabel\":").append(showMinLabel);
            }
            if(showMaxLabel != null) {
                sb.append(",\"showMaxLabel\":").append(showMaxLabel);
            }
            if(interval >= -1) {
                sb.append(",\"interval\":");
                if(interval == -1) {
                    sb.append("\"auto\"");
                } else {
                    sb.append(interval);
                }
            }
        }

        /**
         * Check if a label for the maximum value will be displayed or not.
         *
         * @return True or false. <code>Null</code> value means that it will be determined automatically to eliminate
         * labels-overlap.
         */
        public final Boolean getShowMaxLabel() {
            return showMaxLabel;
        }

        /**
         * Setting for displaying the label corresponding to the maximum value.
         *
         * @param showMaxLabel True or false. <code>Null</code> value means that it will be determined automatically
         *                     to eliminate labels-overlap.
         */
        public void setShowMaxLabel(Boolean showMaxLabel) {
            this.showMaxLabel = showMaxLabel;
        }

        /**
         * Check if a label for the minimum value will be displayed or not.
         *
         * @return True or false. <code>Null</code> value means that it will be determined automatically to eliminate
         * labels-overlap.
         */
        public final Boolean getShowMinLabel() {
            return showMinLabel;
        }

        /**
         * Setting for displaying the label corresponding to the minimum value.
         *
         * @param showMinLabel True or false. <code>Null</code> value means that it will be determined automatically
         *                     to eliminate labels-overlap.
         */
        public void setShowMinLabel(Boolean showMinLabel) {
            this.showMinLabel = showMinLabel;
        }

        /**
         * Get the interval between labels.
         *
         * @return Interval.
         */
        public final int getInterval() {
            return interval;
        }

        /**
         * Set the interval between labels.
         * <p>Important Note: This is applicable only for {@link DataType#CATEGORY}.</p>
         *
         * @param interval 0 means all labels, 1 means every alternate label, 2 means every 2nd label and so on.
         *                 A special value of -1 means labeling will be determined automatically to remove overlap.
         */
        public void setInterval(int interval) {
            this.interval = interval;
        }

        /**
         * Set the label formatter.
         * <pre>
         * Example (numeric values):
         * "{value} kg" => Produces labels like "20 kg"
         * Examples (date/time values):
         * "{yyyy}-{MM}-{dd}" => Produces labels like "1998-01-23"
         * "Day {d}" => Produces labels like "Day 1"
         * All date/time value formatting characters:
         * {yyyy} => years (2020, 2021 etc.)
         * {yy} => years (20, 21 etc.)
         * {Q} => quarter {1, 2, 3 etc.}
         * {MMMM} => dull name of the month (January, February etc.)
         * {MMM} => short name of the month (Jan, Feb etc.)
         * {MM} => 2 digits of the month (01, 02, 03 etc.)
         * {M} => 1/2 digits of the month (1, 2, 3 etc.)
         * {dd} => 2 digits day of the month (01, 02, 03 etc.)
         * {d} => 1/2 digits day of the month (8, 9, 10, 11 etc.)
         * {eeee} => day of week - full name (Monday, Tuesday etc.)
         * {ee} => day of week - short name (Mon, Tue etc.)
         * {e} => day of week (1 to 54)
         * {HH} => Hour (00-23)
         * {H} => Hour (0-23)
         * {hh} => Hour (00-12)
         * {h} => Hour (1-12)
         * {mm} => Minute {00-59}
         * {m} => Minute {0-59}
         * {ss} => Second {00-59}
         * {s} => Second {0-59}
         * {SSS} => Second {000-999}
         * {S} => Second {0-999}
         * </pre>
         *
         * @param formatter Label formatter to be set.
         */
        public void setFormatter(String formatter) {
            this.formatter = formatter;
            this.doNotEscapeFormat = false;
        }

        /**
         * Set a JavaScript function as the label formatter. Only the body of the JavaScript function needs to be
         * set. Two parameters, (value, index) are passed to the function - value: The value at that axis-tick, index:
         * The index is the axis-tick index (index is of not much use since it is not the index of the data). The
         * function should return the label to be displayed.
         * <p>Example: For displaying a rounded numeric value (rounded to 2 decimal places) in a numeric axis,
         * you may do something like:</p>
         * <pre>
         *     setFormatterFunction("return value.toFixed(2);");
         * </pre>
         *
         * @param function The body of the JavaScript function.
         */
        public void setFormatterFunction(String function) {
            this.formatter = ComponentPart.encodeFunction(function, "value", "index");
            this.doNotEscapeFormat = true;
        }
    }

    /**
     * Represents ticks on an axis line.
     *
     * @author Syam
     */
    public static abstract class AbstractTicks extends Line {

        private int width = 0;

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            if(width > 0) {
                ComponentPart.addComma(sb);
                sb.append("\"length\":").append(width);
            }
        }

        /**
         * Get the width of the tick in pixels.
         *
         * @return Width of the tick.
         */
        public final int getWidth() {
            return width;
        }

        /**
         * Set the width of the tick in pixels.
         *
         * @param width Width of the tick.
         */
        public void setWidth(int width) {
            this.width = width;
        }
    }

    /**
     * Represents the minor tick on the axis line.
     *
     * @author Syam
     */
    public static class MinorTicks extends AbstractTicks {

        private int divisions = 0;

        /**
         * Constructor.
         */
        public MinorTicks() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            if(divisions > 0) {
                ComponentPart.addComma(sb);
                sb.append("\"splitNumber\":").append(divisions);
            }
        }

        /**
         * Get a number of divisions.
         *
         * @return Number of divisions.
         */
        public final int getDivisions() {
            return divisions;
        }

        /**
         * Set the number of divisions.
         *
         * @param divisions Number of divisions.
         */
        public void setDivisions(int divisions) {
            this.divisions = divisions;
        }
    }

    /**
     * Represents the tick on the axis line.
     *
     * @author Syam
     */
    public static class Ticks extends AbstractTicks {

        private boolean inside = false;
        private int interval = Integer.MIN_VALUE;
        private boolean alignWithLabels;

        /**
         * Constructor.
         */
        public Ticks() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            sb.append(",\"inside\":").append(inside);
            if(interval >= -1) {
                sb.append(",\"interval\":");
                if(interval == -1) {
                    sb.append("\"auto\"");
                } else {
                    sb.append(interval);
                }
            }
            sb.append(",\"alignWithLabel\":").append(alignWithLabels);
        }

        /**
         * Check if the tick is drawn inside or outside the axis.
         *
         * @return True if inside.
         */
        public boolean isInside() {
            return inside;
        }

        /**
         * Setting for drawing the tick inside the axis.
         *
         * @param inside True if inside.
         */
        public void setInside(boolean inside) {
            this.inside = inside;
        }

        /**
         * Get the interval between labels.
         *
         * @return Interval.
         */
        public final int getInterval() {
            return interval;
        }

        /**
         * Set the interval between labels. (If not set and if the "interval" is set for the {@link Label} of the
         * axis, that will be used).
         *
         * @param interval 0 means all labels, 1 means every alternate label, 2 means every 2nd label and so on.
         *                 A special value of -1 means labeling will be determined automatically to remove overlap.
         */
        public void setInterval(int interval) {
            this.interval = interval;
        }

        /**
         * Check if ticks are aligned with labels or not. (Applicable only to category type axes).
         *
         * @return True or false.
         */
        public final boolean isAlignWithLabels() {
            return alignWithLabels;
        }

        /**
         * Set alignment of the ticks with labels. (Applicable only to category type axes).
         *
         * @param alignWithLabels True or false.
         */
        public void setAlignWithLabel(boolean alignWithLabels) {
            this.alignWithLabels = alignWithLabels;
        }
    }

    /**
     * A base class for various types of lines used in axis and coordinate systems.
     *
     * @author Syam
     */
    public static class Line extends VisiblePart {

        private LineStyle style;

        /**
         * Constructor.
         */
        public Line() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            if(style != null) {
                ComponentPart.encode(sb, "lineStyle", style);
            }
        }

        /**
         * Get the style.
         *
         * @param create Whether to create if not exists or not.
         * @return Style.
         */
        public LineStyle getStyle(boolean create) {
            if(style == null && create) {
                style = new LineStyle();
            }
            return style;
        }

        /**
         * Set the style.
         *
         * @param style Style to set.
         */
        public void setStyle(LineStyle style) {
            this.style = style;
        }
    }

    /**
     * Represents the grid-lines drawn by the axis.
     *
     * @author Syam
     */
    public static class GridLines extends Line {

        private int interval = Integer.MIN_VALUE;

        /**
         * Constructor.
         */
        public GridLines() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            if(interval >= -1) {
                ComponentPart.addComma(sb);
                sb.append("\"interval\":");
                if(interval == -1) {
                    sb.append("\"auto\"");
                } else {
                    sb.append(interval);
                }
            }
        }

        /**
         * Get the interval for the grid lines. (If not defined, the interval property of the axis-label will be used).
         *
         * @return Interval.
         */
        public final int getInterval() {
            return interval;
        }

        /**
         * Set the interval for the grid lines. (If not defined, the interval property of the axis-label will be used).
         *
         * @param interval 0 means all, 1 means every alternate line, 2 means every 2nd line and so on.
         *                 A special value of -1 means it will be determined automatically.
         */
        public void setInterval(int interval) {
            this.interval = interval;
        }
    }

    /**
     * Represents the minor grid-lines drawn by the axis.
     *
     * @author Syam
     */
    public static class MinorGridLines extends Line {

        /**
         * Constructor.
         */
        public MinorGridLines() {
        }
    }

    /**
     * Represents the grid-areas drawn by the axis.
     *
     * @author Syam
     */
    public static class GridAreas extends Area {

        private int interval = Integer.MIN_VALUE;

        /**
         * Constructor.
         */
        public GridAreas() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            if(interval >= -1) {
                ComponentPart.addComma(sb);
                sb.append("\"interval\":");
                if(interval == -1) {
                    sb.append("\"auto\"");
                } else {
                    sb.append(interval);
                }
            }
        }

        /**
         * Get the interval for the grid lines. (If not defined, the interval property of the axis-label will be used).
         *
         * @return Interval.
         */
        public final int getInterval() {
            return interval;
        }

        /**
         * Set the interval for the grid lines. (If not defined, the interval property of the axis-label will be used).
         *
         * @param interval 0 means all, 1 means every alternate line, 2 means every 2nd line and so on.
         *                 A special value of -1 means it will be determined automatically.
         */
        public void setInterval(int interval) {
            this.interval = interval;
        }
    }

    /**
     * Represents the axis-pointer shown by the axis. (By default, no axis-pointer will be shown. However,
     * if you create one via {@link Axis#getPointer(boolean)}, it will be displayed).
     *
     * @author Syam
     */
    public static class Pointer extends VisiblePart {

        private PointerType type;
        private Boolean snap;
        private PointerLabel label;
        private Shadow shadow;
        private LineStyle lineStyle;
        private PointerHandle handle;

        /**
         * Constructor.
         */
        public Pointer() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            if(type != null) {
                sb.append(",\"type\":\"").append(type.toString().toLowerCase()).append('"');
            }
            if(snap != null) {
                sb.append(",\"snap\":").append(snap);
            }
            ComponentPart.encode(sb, "label", label);
            ComponentPart.encode(sb, "shadowStyle", shadow);
            ComponentPart.encode(sb, "handle",handle);
        }

        /**
         * Get the type of the pointer.
         *
         * @return Pointer-type.
         */
        public PointerType getType() {
            return type;
        }

        /**
         * Set the type of the pointer.
         *
         * @param type Pointer-type.
         */
        public void setType(PointerType type) {
            this.type = type;
        }

        /**
         * Get the snap attribute. This will determine whether to snap to point automatically or not.
         * The default value is auto-determined. This feature usually makes sense in value axis and time axis,
         * where tiny points can be sought automatically.
         *
         * @return True/false or <code>null</code> if not set (means default).
         */
        public Boolean getSnap() {
            return snap;
        }

        /**
         * Set the snap attribute. (See {@link #getSnap()} to understand more about this attribute).
         *
         * @param snap True/false or <code>null</code>.
         */
        public void setSnap(Boolean snap) {
            this.snap = snap;
        }

        /**
         * Get the label of the pointer.
         *
         * @param create Whether to create if not exists or not.
         * @return Pointer label.
         */
        public final PointerLabel getLabel(boolean create) {
            if(label == null && create) {
                label = new PointerLabel();
            }
            return label;
        }

        /**
         * Set the label for the pointer.
         *
         * @param label Pointer label.
         */
        public void setLabel(PointerLabel label) {
            this.label = label;
        }

        /**
         * Get the line-style of the pointer.
         *
         * @param create Whether to create if not exists or not.
         * @return Line-style.
         */
        public LineStyle getLineStyle(boolean create) {
            if(lineStyle == null && create) {
                lineStyle = new LineStyle();
            }
            return lineStyle;
        }

        /**
         * Set the line-style of the pointer.
         *
         * @param lineStyle Line-style.
         */
        public void setLineStyle(LineStyle lineStyle) {
            this.lineStyle = lineStyle;
        }

        /**
         * Get the shadow-style of the pointer.
         *
         * @param create Whether to create if not exists or not.
         * @return Shadow.
         */
        public final Shadow getShadow(boolean create) {
            if(shadow == null && create) {
                shadow = new Shadow();
            }
            return shadow;
        }

        /**
         * Set the shadow-style of the pointer.
         *
         * @param shadow Shadow.
         */
        public void setShadow(Shadow shadow) {
            this.shadow = shadow;
        }

        /**
         * Get the pointer-handle.
         *
         * @param create Whether to create if not exists or not.
         * @return Pointer-handle.
         */
        public final PointerHandle getHandle(boolean create) {
            if(handle == null && create) {
                handle = new PointerHandle();
            }
            return handle;
        }

        /**
         * Set the pointer-handle.
         *
         * @param handle Pointer-handle.
         */
        public void setHandle(PointerHandle handle) {
            this.handle = handle;
        }
    }

    /**
     * Represents the label displayed by the axis-pointer.
     *
     * @author Syam
     */
    public static class PointerLabel extends AbstractLabel {

        private int precision = -1;

        /**
         * Constructor.
         */
        public PointerLabel() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            sb.append(",\"precision\":");
            if(precision >= 0) {
                sb.append(precision);
            } else {
                sb.append("\"auto\"");
            }
        }

        /**
         * Get the precision for showing the value (Applicable to numeric values).
         *
         * @return Precision (Number of digits). A value of -1 means it will be determined automatically.
         */
        public final int getPrecision() {
            return precision;
        }

        /**
         * Set the precision for showing the value (Applicable to numeric values).
         *
         * @param precision Precision (Number of digits). A value of -1 means it will be determined automatically.
         */
        public void setPrecision(int precision) {
            this.precision = precision;
        }
    }

    /**
     * Represent the handle that can be used with an axis-pointer. (This is useful mainly for touch-devices).
     *
     * @author Syam
     */
    public static class PointerHandle extends VisiblePart {

        private int width = -1, height = -1, gap = -1;
        private Shadow shadow;
        private AbstractColor color;

        /**
         * Constructor.
         */
        public PointerHandle() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            if(width > 0 || height > 0) {
                int w = width, h = height;
                if(w <= 0) {
                    w = 45;
                }
                if(h <= 0) {
                    h = 45;
                }
                sb.append(",\"size\":");
                if(w == h) {
                    sb.append(w);
                } else {
                    sb.append('[').append(w).append(',').append(h).append(']');
                }
                if(gap >= 0) {
                    sb.append(",\"margin\":").append(gap);
                }
            }
            if(color != null) {
                sb.append(",\"color\":").append(color);
            }
            ComponentPart.encode(sb, null, shadow);
        }

        /**
         * Get the width in pixels.
         *
         * @return Width.
         */
        public int getWidth() {
            return width;
        }

        /**
         * Set the width in pixels.
         *
         * @param width Width.
         */
        public void setWidth(int width) {
            this.width = width;
        }

        /**
         * Get the height in pixels.
         *
         * @return Height.
         */
        public int getHeight() {
            return height;
        }

        /**
         * Set the height in pixels.
         *
         * @param height Height.
         */
        public void setHeight(int height) {
            this.height = height;
        }

        /**
         * Set the size in pixes (setting to both width and height).
         *
         * @param size Size in pixels.
         */
        public void setSize(int size) {
            width = size;
            height = size;
        }

        /**
         * Set the gap between the axis and the handle.
         *
         * @return Gap in pixels.
         */
        public int getGap() {
            return gap;
        }

        /**
         * Set the gap between the axis and handle.
         *
         * @param gap Gap in pixels.
         */
        public void setGap(int gap) {
            this.gap = gap;
        }

        /**
         * Get the color of the handle.
         *
         * @return Color
         */
        public AbstractColor getColor() {
            return color;
        }

        /**
         * Set the color of the handle.
         *
         * @param color Color.
         */
        public void setColor(AbstractColor color) {
            this.color = color;
        }

        /**
         * Get the shadow-style of the pointer-handle.
         *
         * @param create Whether to create if not exists or not.
         * @return Shadow.
         */
        public final Shadow getShadow(boolean create) {
            if(shadow == null && create) {
                shadow = new Shadow();
            }
            return shadow;
        }

        /**
         * Set the shadow-style of the pointer-handle.
         *
         * @param shadow Shadow.
         */
        public void setShadow(Shadow shadow) {
            this.shadow = shadow;
        }
    }

    abstract ComponentPart wrap(CoordinateSystem coordinateSystem);

    /**
     * Used internally to wrap an axis within a coordinate system. This wrapping is required because the same axis may
     * be used by more than one coordinate system.
     *
     * @author Syam
     */
    static class AxisWrapper extends AbstractPart {

        private int renderingIndex = -1;
        final Axis axis;
        private final CoordinateSystem coordinateSystem;

        /**
         * Constructor.
         * @param axis Axis.
         * @param coordinateSystem Coordinate system.
         */
        AxisWrapper(Axis axis, CoordinateSystem coordinateSystem) {
            this.axis = axis;
            this.coordinateSystem = coordinateSystem;
            this.axis.wrappers.put(this.coordinateSystem, this);
        }

        /**
         * Set the rendering index of this axis.
         *
         * @param index Rendering index.
         */
        @Override
        public void setRenderingIndex(int index) {
            this.renderingIndex = index;
        }

        /**
         * Return the rendering index of this axis.
         *
         * @return Rendering index.
         */
        @Override
        public int getRenderingIndex() {
            return renderingIndex;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            for(SOChart.ComponentEncoder ce: SOChart.encoders) {
                if(coordinateSystem.getClass() != ce.partType) {
                    continue;
                }
                ComponentPart.addComma(sb);
                sb.append('"').append(ce.label).append("Index\":").append(coordinateSystem.getSerial());
                break;
            }
            axis.encodeJSON(sb);
        }

        @Override
        public void validate() throws ChartException {
            axis.validate();
        }
    }
}