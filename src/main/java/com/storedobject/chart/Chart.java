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

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>
 * Chart. Since this is a concrete class, this may be directly used for creating a chart of a particular
 * {@link ChartType}. It has got the flexibility that the {@link ChartType} can be changed at any time using
 * {@link #setType(ChartType)} method.
 * However, there are concrete derivatives of this class such as {@link PieChart}, {@link NightingaleRoseChart} etc.
 * where more chart-specific methods are available and data for the chart is checked more accurately for errors. If
 * the data set for the chart is of invalid type, a system tries to do its best to adapt that data, but the chart may not
 * appear if the data conversion fails.
 * </p>
 * <p>Custom Charts: Any chart may be converted to a custom chart by setting up an item renderer to render the
 * data point. The item renderer can be specified via {@link #setCustomRenderer(String)}. However, please note that
 * only a few custom renderers are currently available.</p>
 * <p>
 * Positioning of charts within the display area of {@link SOChart}: Most charts need a {@link CoordinateSystem} to
 * plot on and all {@link CoordinateSystem}s support positioning (Please see
 * {@link CoordinateSystem#setPosition(Position)}). Those which do not require a {@link CoordinateSystem} are called
 * {@link SelfPositioningChart} and supports its own positioning mechanism (Please see
 * {@link SelfPositioningChart#setPosition(Position)}).
 * </p>
 *
 * @author Syam
 */
public class Chart extends AbstractPart implements Component, HasData, HasAnimation, HasEmphasis {

    List<Axis> axes;
    private ChartType type = ChartType.Line;
    private String custom;
    private Label label;
    private ItemStyle itemStyle;
    private String name;
    CoordinateSystem coordinateSystem;
    private AbstractDataProvider<?>[] data;
    private AbstractColor[] colors;
    private final Map<Class<? extends ComponentProperty>, ComponentProperty> propertyMap = new HashMap<>();
    private final Map<Class<? extends ComponentProperty>, String> propertyNameMap = new HashMap<>();
    private MarkArea markArea;
    private Animation animation;
    private Emphasis emphasis;
    private SOChart soChart;

    /**
     * Create a {@link ChartType#Line} chart.
     */
    public Chart() {
        this(ChartType.Line);
    }

    /**
     * Create a {@link ChartType#Line} chart with the given data.
     *
     * @param data Data to be used (multiples of them for charts that use multi-axis coordinate systems).
     */
    public Chart(AbstractDataProvider<?>... data) {
        this(null, data);
    }

    /**
     * Create a chart of a given type and data.
     *
     * @param type type of the chart.
     * @param data Data to be used (multiples of them for charts that use multi-axis coordinate systems).
     */
    public Chart(ChartType type, AbstractDataProvider<?>... data) {
        setType(type);
        this.data = data;
    }

    /**
     * Set data for the chart.
     *
     * @param data Data to be used (multiples of them for charts that use multi-axis coordinate systems).
     */
    public void setData(AbstractDataProvider<?>... data) {
        this.data = data;
    }

    /**
     * Get the current set of data.
     *
     * @return Data.
     */
    public final AbstractDataProvider<?>[] getData() {
        return data;
    }

    /**
     * <p>Set a custom renderer for this chart. Each renderer can be used on a certain type of charts only, may require
     * special data requirements, and you may have to encode the rendering values. The following are the currently supported
     * renderers:</p>
     * <pre>
     * (1) "HBand": Horizontal bands. Applicable to XY charts.
     * Data Point: [y-value, start, end, color], encode { x: 1, y: 0 }
     * (2) "VLine": Single vertical line at a given value. Applicable to XY charts.
     * Single Data Point: [line at (x-value), text label, color], encode { x: 0, y: -1 }
     * (3) "HBar": Horizontal bars. Applicable yo XY charts.
     * Data Point: [y-value, label, start, end, %age completed, bar color]
     * (4) "VAxisLabel": Label bars for the vertical axis.
     * Data Point: [y-value, group label, connected, label, sub-label, bar color], encode { x: -1, y: 0 }
     * </pre>
     *
     * @param renderer Name of the renderer.
     */
    public void setCustomRenderer(String renderer) {
        this.custom = renderer;
    }

    /**
     * Get the type-value of the chart. (Example, type-value of the {@link LineChart} is "line"). You must override this
     * and return the correct chart-type if you are creating your own chart-type that is not yet supported by this
     * add-on.
     *
     * @return Chart-type value.
     */
    protected String typeValue() {
        if(custom != null) {
            return "custom";
        }
        String t = type.toString();
        return Character.toLowerCase(t.charAt(0)) + t.substring(1);
    }

    /**
     * Get the data to embed. This is useful only for those special charts that embed data in the chart itself rather
     * than pointing to the dataset. Example: {@link TreeChart}, {@link GaugeChart} etc.
     * If null is returned from this method, no data will be embedded.
     *
     * @return Data to embed.
     */
    protected AbstractDataProvider<?> dataToEmbed() {
        return null;
    }

    /**
     * Get the data that represents the values of this chart. This is useful only for those special charts that embed
     * data in the chart itself rather than pointing to the dataset. Example: {@link TreeChart}, {@link GaugeChart} etc.
     * The default implementation returns the result of {@link #dataToEmbed()} if it is not null. Otherwise, it tries
     * to determine it from the axis data if possible.
     *
     * @return Data that represents value.
     */
    protected AbstractDataProvider<?> dataValue() {
        AbstractDataProvider<?> d = dataToEmbed();
        if(d == null) {
            if(data.length > 1) {
                d = data[1];
            } else if(data.length > 0) {
                d = data[0];
            }
        }
        return d;
    }

    /**
     * Get the index to get the real data value of this chart. (In special charts, the actual data value at a data
     * point may be at an index different from 0).
     *
     * @return Data value index.
     */
    protected int dataValueIndex() {
        return -1;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(colors != null) {
            ComponentPart.addComma(sb);
            sb.append("\"color\":[");
            for(int i = 0; i < colors.length; i++) {
                if(i > 0) {
                    sb.append(',');
                }
                sb.append(colors[i]);
            }
            sb.append(']');
        }
        AbstractDataProvider<?> dataToEmbed = dataToEmbed();
        if(dataToEmbed != null) {
            ComponentPart.encode(sb, "data", dataToEmbed.getSerial());
        }
        ComponentPart.encode(sb, "type", typeValue());
        if(custom != null) {
            ComponentPart.encode(sb, "renderItem", custom);
        }
        if(coordinateSystem != null) {
            if(coordinateSystem.axes != axes) {
                ComponentPart aw;
                if(axes != null && !axes.isEmpty()) {
                    for (Axis a : axes) {
                        aw = a.wrap(coordinateSystem);
                        ComponentPart.encode(sb, a.axisName() + "Index", aw.getRenderingIndex());
                    }
                }
            } else {
                Set<Class<?>> axisClasses = new HashSet<>();
                coordinateSystem.axes.forEach(a -> axisClasses.add(a.getClass()));
                axisClasses.forEach(ac -> coordinateSystem.axes(ac).map(a -> a.wrap(coordinateSystem)).
                        min(Comparator.comparing(ComponentPart::getSerial)).ifPresent(w -> {
                            Axis.AxisWrapper aw = (Axis.AxisWrapper) w;
                            ComponentPart.encode(sb, aw.axis.axisName() + "Index", aw.getRenderingIndex());
                        }));
            }
        }
        if(coordinateSystem != null) {
            ComponentPart.encode(sb, "coordinateSystem", coordinateSystem.systemName());
        }
        propertyMap.values().forEach(p -> ComponentPart.encode(sb, getPropertyName(p), p));
        if(label != null) {
            label.chart = this;
            ComponentPart.encode(sb, getLabelName(), label);
        }
        ComponentPart.encode(sb, "itemStyle", itemStyle);
        if(dataToEmbed == null) {
            ComponentPart.addComma(sb);
            sb.append("\"encode\":{");
            String[] axes = null;
            if(coordinateSystem != null) {
                axes = coordinateSystem.axesData();
            }
            if(axes == null) {
                axes = type.getAxes();
            }
            for(int i = 0; i < axes.length; i++) {
                if(i > 0) {
                    sb.append(',');
                }
                sb.append('"').append(axes[i]).append("\":\"d").append(data[i].getSerial()).append("\"");
            }
            sb.append('}');
        }
        ComponentPart.encode(sb, "markArea", markArea);
    }

    /**
     * Get the label name for encoding label if exists.
     *
     * @return Label name. Default is "label".
     */
    protected String getLabelName() {
        return "label";
    }

    @Override
    public void validate() throws ChartException {
        String[] axes = type.getAxes();
        if(data == null) {
            throw new ChartException("Data not set for " + className());
        }
        if(data.length < axes.length) {
            throw new ChartException("Data for " + name(axes[data.length]) + " not set for " + className());
        }
        if(type.requireCoordinateSystem()) {
            if (coordinateSystem == null) {
                throw new ChartException("Coordinate system not set for " + className());
            }
            if(this.axes == null || this.axes.isEmpty()) {
                this.axes = coordinateSystem.axes;
            }
            if(coordinateSystem.axes != this.axes) {
                for(Axis a: this.axes) {
                    if(!coordinateSystem.axes.contains(a)) {
                        String name = a.getName();
                        if(name == null) {
                            name = ComponentPart.className(a.getClass());
                        }
                        throw new ChartException("Axis " + name
                                + " doesn't belong to the coordinate system of this chart - " + getName());
                    }
                }
            }
        }
        if(coordinateSystem != null && coordinateSystem.axes != this.axes) {
            for(Axis a: this.axes) {
                if(this.axes.stream().filter(ax -> ax.getClass() == a.getClass()).count() > 1) {
                    String name = a.getName();
                    if(name == null) {
                        name = ComponentPart.className(a.getClass());
                    }
                    throw new ChartException("Multiple axes of the same type found (" + name +
                            ") for this chart - " + getName());
                }
            }
        }
    }

    /**
     * Get the coordinate system of this chart.
     * <p>Note: Some charts like {@link PieChart}, {@link RadarChart} etc. do not have a coordinate system.</p>
     *
     * @return Coordinate system.
     */
    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    private String getPropertyName(ComponentProperty property) {
        String name = propertyNameMap.get(property.getClass());
        if(name == null) {
            name = property.getClass().getName();
            name = name.substring(name.lastIndexOf('.') + 1);
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        return name;
    }

    @SuppressWarnings("unused")
    <P extends ComponentProperty> void setPropertyName(Class<P> propertyClass, String name) {
        propertyNameMap.put(propertyClass, name);
    }

    <P extends ComponentProperty> P getProperty(Class<P> propertyClass, boolean create) {
        @SuppressWarnings("unchecked") P property = (P) propertyMap.get(propertyClass);
        if(property == null && create) {
            try {
                property = propertyClass.getDeclaredConstructor().newInstance();
            } catch(Throwable ignored) {
                return null;
            }
            propertyMap.put(propertyClass, property);
        }
        return property;
    }

    void setProperty(ComponentProperty property) {
        propertyMap.put(property.getClass(), property);
    }

    String axisName(int axis) {
        return name(type.getAxes()[axis]);
    }

    static String name(String name) {
        if(name.substring(1).equals(name.substring(1).toLowerCase())) {
            return name;
        }
        StringBuilder n = new StringBuilder();
        n.append(Character.toUpperCase(name.charAt(0)));
        name.chars().skip(1).forEach(c -> {
            if(Character.isUpperCase((char)c)) {
                n.append(' ');
            }
            n.append((char)c);
        });
        return n.toString();
    }

    @Override
    public void addParts(SOChart soChart) {
        this.soChart = soChart;
        if(coordinateSystem != null) {
            soChart.addParts(coordinateSystem);
            coordinateSystem.addParts(soChart);
        } else {
            soChart.addParts(data);
        }
        Tooltip tooltip = getTooltip(false);
        if(tooltip != null) {
            tooltip.addParts(soChart);
        }
        if(markArea != null) {
            soChart.addParts(markArea.data);
        }
        if(label != null && label.labels != null) {
            soChart.addData(label.labels);
        }
    }

    @Override
    public void declareData(Set<AbstractDataProvider<?>> dataSet) {
        dataSet.addAll(Arrays.asList(data));
        AbstractDataProvider<?> d = dataToEmbed();
        if(d != null) {
            dataSet.add(d);
        }
        if(markArea != null) {
            dataSet.add(markArea.data);
        }
        Tooltip tooltip = getTooltip(false);
        if(tooltip != null) {
            tooltip.declareData(dataSet);
        }
    }

    /**
     * Get the type of this chart.
     *
     * @return Type.
     */
    public ChartType getType() {
        return type;
    }

    /**
     * Set the type of this chart.
     *
     * @param type Type to be set.
     */
    public void setType(ChartType type) {
        this.type = type == null ? ChartType.Line : type;
    }

    /**
     * Plot the chart on a given coordinate system. (Certain chart types such as {@link ChartType#Pie},
     * do not have a coordinate system and thus, this call is not required. Also, instead of using this
     * method, you can use the {@link CoordinateSystem#add(Chart...)} method if you want to plot on the default
     * set of axes.)
     *
     * @param coordinateSystem Coordinate system on which the chart will be plotted. (If it was plotted on
     *                         another coordinate system, it will be removed from it).
     * @param axes Axes to be used by the chart. This needs to be specified if the coordinate system has
     *             multiple axes of the required type.
     * @return Self-reference.
     */
    @SuppressWarnings("UnusedReturnValue")
    public Chart plotOn(CoordinateSystem coordinateSystem, Axis... axes) {
        if(coordinateSystem == null) {
            this.axes = null;
        } else if(type.requireCoordinateSystem()) {
            coordinateSystem.add(this);
        }
        if(this.coordinateSystem != null && axes != null && axes.length > 0) {
            this.axes = new ArrayList<>();
            for(Axis a: axes) {
                if(a != null) {
                    this.axes.add(a);
                }
            }
            this.coordinateSystem.addAxis(axes);
        }
        return this;
    }

    /**
     * Plot the chart on a given set of axes.
     *
     * @param axes Axes to be used by the chart.
     * @return Self-reference.
     */
    public Chart plotOn(Axis... axes) {
        if(axes != null && axes.length > 0) {
            this.axes = new ArrayList<>();
            for(Axis a: axes) {
                if(a != null) {
                    this.axes.add(a);
                }
            }
        }
        return this;
    }

    /**
     * Set the name of the chart. (This will be used in displaying the legend if the legend is enabled in the
     * {@link SOChart} or added separately).
     *
     * @return Name.
     */
    @Override
    public String getName() {
        return name == null || name.isEmpty() ? ("Chart " + (getSerial() + 1)) : name;
    }

    /**
     * Set the name for this chart.
     *
     * @param name Name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set colors for the charts. Certain charts require more than one color, e.g., {@link PieChart}. (Colors are
     * used sequentially and then, circularly).
     *
     * @param colors List of one or more colors.
     */
    public void setColors(AbstractColor... colors) {
        this.colors = colors.length == 0 ? null : colors;
    }

    /**
     * Get the tooltip. (If <code>true</code> is passed as the parameter, a new tooltip
     * will be created if not already exists).
     *
     * @param create Whether to create it or not.
     * @return Tooltip.
     */
    public Tooltip getTooltip(boolean create) {
        return getProperty(Tooltip.class, create);
    }

    /**
     * Set the tooltip.
     *
     * @param tooltip Tooltip.
     */
    public void setTooltip(Tooltip tooltip) {
        setProperty(tooltip);
    }

    /**
     * Get the label for this chart value.
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
     * Set the label for this chart value.
     *
     * @param label Label.
     */
    public void setLabel(Label label) {
        this.label = label;
    }

    /**
     * Get the minimum value for this chart. This is used to determine the minimum value to be used by some other
     * components (such as {@link VisualMap}) when rendering occurs. The default implementation tries various ways to
     * determine it automatically.
     *
     * @return The minimum value if it is possible to determine. Otherwise, null.
     */
    public Object getMin() {
        AbstractDataProvider<?> d = data4MinMax();
        return d == null ? null : d.getMin();
    }

    /**
     * Get the minimum value for this chart. This is used to determine the minimum value to be used by some other
     * components (such as {@link VisualMap}) when rendering occurs. The default implementation tries various ways to
     * determine it automatically.
     *
     * @return The minimum value if it is possible to determine. Otherwise, null.
     */
    public Object getMax() {
        AbstractDataProvider<?> d = data4MinMax();
        return d == null ? null : d.getMax();
    }

    private AbstractDataProvider<?> data4MinMax() {
        AbstractDataProvider<?> d = dataToEmbed();
        if(d != null) {
            return d;
        }
        for(AbstractDataProvider<?> dp: data) {
            if(!(dp instanceof CategoryDataProvider)) {
                return dp;
            }
        }
        return data.length == 0 ? null : (data.length == 1 ? data[0] : data[1]);
    }

    /**
     * Get item style. (Not applicable to certain types of charts).
     *
     * @param create If passed true, a new style is created if not exists.
     * @return Item style.
     */
    public final ItemStyle getItemStyle(boolean create) {
        if(itemStyle == null && create) {
            itemStyle = new ItemStyle();
        }
        return itemStyle;
    }

    /**
     * Set item style.
     *
     * @param itemStyle Style to set.
     */
    public void setItemStyle(ItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }

    /**
     * Get the mark area for the chart. (Not applicable to certain types of charts).
     *
     * @param create If passed true, a new {@link MarkArea} is created if not exists.
     * @return Mark area.
     */
    public MarkArea getMarkArea(boolean create) {
        if(markArea == null && create) {
            markArea = new MarkArea();
        }
        return markArea;
    }

    /**
     * Set a customized {@link MarkArea} for this chart.
     *
     * @param markArea Mark the area to set.
     */
    public void setMarkArea(MarkArea markArea) {
        this.markArea = markArea;
    }

    @Override
    public Animation getAnimation(boolean create) {
        if(create && animation == null) {
            animation = new Animation();
        }
        return animation;
    }

    @Override
    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    /**
     * Get the Emphasis effect of the chart.
     *
     * @param create If passed true, a new {@link Emphasis} effect is created if not exists.
     * @return Emphasis.
     */
    @Override
    public Emphasis getEmphasis(boolean create) {
        if(emphasis == null && create) {
            emphasis = new Emphasis();
        }
        return emphasis;
    }

    /**
     * Set an Emphasis effect to this chart.
     *
     * @param emphasis Emphasis effect to set.
     */
    @Override
    public void setEmphasis(com.storedobject.chart.Emphasis emphasis) {
        if(emphasis instanceof Emphasis e) {
            this.emphasis = e;
        }
    }

    /**
     * Value-label that can be customized for a chart.
     *
     * @author Syam
     */
    public static class Label extends com.storedobject.chart.Label {

        private AbstractDataProvider<?> labels;

        /**
         * Chart associated with this label.
         */
        Chart chart;
        private final LabelPosition position = new LabelPosition();

        /**
         * Constructor.
         */
        public Label() {
            formatParser = this::format;
        }

        @Override
        protected String getGapName() {
            return "distance";
        }

        /**
         * Set the label formatter.
         * <p>Format template may contain patterns like {n} where "n" is the index of the axis. So {0} represents
         * the x-axis value and {1} represents the y-axis value for an {@link XYChart}. If 2 y-axes are there,
         * it will be {1} and {2} respectively. There can be many axes and the index value will increase
         * accordingly. The index indicates the order in which the axes were added to the chart.</p>
         * <p>You could also use {D:data name} to specify any data set so that the labels will be taken from the data
         * set specified. This mechanism is useful when you have a confusing set of axes and could not precisely
         * determine the index number of the axis. Also, please see if you can use
         * {@link #setLabelProvider(AbstractDataProvider)} instead of this.</p>
         * <pre>
         * Examples:
         * "{0}, {1}" => Produces labels like "Rice 20" where "Rice" is the x-value and 20 is the y-value.
         * "{1} kg" => Produces labels like "20 kg" where 20 is the y-value.
         * "{1} kg/year of {chart}" => Produces labels like "4000 kg/year of Rice" where 4000 is the y-value
         * and "Rice" is the name of the chart.
         * </pre>
         *
         * @param formatter Label formatter to be set.
         */
        public void setFormatter(String formatter) {
            this.labels = null;
            this.formatter = formatter;
        }

        private String format(String f) {
            for(int i = 0; i < chart.data.length; i++) {
                f = f.replace("{" + i + "}", "{@d" + chart.data[i].getSerial() + "}");
            }
            SOChart soChart = chart.soChart;
            if(soChart != null) {
                for(AbstractDataProvider<?> data: soChart.dataSet()) {
                    f = f.replace("{D:" + data.getName() + "}", "{@d" + data.getSerial() + "}");
                }
            }
            return f.replace("{chart}", "{a}");
        }

        /**
         * Instead of specifying the format for the label via {@link #setFormatter(String)}, it is possible to
         * set the labels directly from an {@link AbstractDataProvider} using this method. Such an
         * {@link AbstractDataProvider} may be created from the one or more of the {@link AbstractDataProvider}s
         * involved via one of the create methods: {@link AbstractDataProvider#create(DataType, Function)} or
         * {@link AbstractDataProvider#create(DataType, BiFunction)}
         *
         * @param labels Labels to be set from the elements of this data provider. (Typically a {@link CategoryData} is
         *               used).
         */
        public void setLabelProvider(AbstractDataProvider<?> labels) {
            this.formatter = ""; // Set to a non-null value.
            this.labels = labels;
        }

        @Override
        String getFormatterValue() {
            if(labels != null) {
                formatter = "{D:" + labels.getName() + "}";
            }
            return super.getFormatterValue();
        }

        /**
         * Get the position indicator of this label.
         *
         * @return Label position.
         */
        public LabelPosition getPosition() {
            return position;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            sb.append(position.encode(isInside()));
        }
    }

    /**
     * Position indicator for labels used in charts.
     *
     * @author Syam
     */
    public static class LabelPosition {

        private boolean left = false, right = false, top = false, bottom = false, center = false;
        private Size x, y;

        /**
         * Set the (x, y) position. The origin is at the top-left point of the bounding rectangle.
         *
         * @param x X position.
         * @param y Y position.
         */
        public void at(Size x, Size y) {
            this.x = x;
            this.y = y;
            left = right = top = bottom = center = false;
        }

        /**
         * Set the label position to the left side.
         *
         * @return Self-reference.
         */
        public LabelPosition left() {
            x = y = null;
            left = true;
            right = false;
            center = false;
            return this;
        }

        /**
         * Set the label position to the right side.
         *
         * @return Self-reference.
         */
        public LabelPosition right() {
            x = y = null;
            left = false;
            right = true;
            center = false;
            return this;
        }

        /**
         * Set the label position to the top side.
         *
         * @return Self-reference.
         */
        public LabelPosition top() {
            x = y = null;
            top = true;
            bottom = false;
            center = false;
            return this;
        }

        /**
         * Set the label position to the bottom side.
         *
         * @return Self-reference.
         */
        public LabelPosition bottom() {
            x = y = null;
            top = false;
            bottom = true;
            center = false;
            return this;
        }

        /**
         * Set the label position to the center. (Applicable to certain types of charts such as {@link PieChart}).
         *
         * @return Self-reference.
         */
        public LabelPosition center() {
            x = y = null;
            top = false;
            bottom = false;
            center = true;
            return this;
        }

        private boolean set(Size size) {
            switch(size.get()) {
                case -101 -> left = true;
                case -102, -111, -112 -> top = true;
                case -103 -> right = true;
                case -113 -> bottom = true;
                default -> {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            if(center) {
                return "center";
            }
            if(x != null && y != null) {
                if(set(x)) {
                    x = null;
                }
                if(set(y)) {
                    y = null;
                }
                if(x != null && y != null) {
                    return "[" + x.encode() + "," + y.encode() + "]";
                }
            }
            StringBuilder s = new StringBuilder();
            if(top) {
                s.append("Top");
            }
            if(bottom) {
                s.append("Bottom");
            }
            if(left) {
                s.append("Left");
            }
            if(right) {
                s.append("Right");
            }
            return s.toString();
        }

        private String encode(boolean inside) {
            String s = toString();
            if(inside) {
                s = "inside" + s;
            }
            if(s.isEmpty()) {
                return s;
            }
            return ",\"position\":\"" + s.substring(0, 1).toLowerCase() + s.substring(1) + "\"";
        }
    }

    /**
     * Class to represent the Emphasis effect.
     *
     * @author Syam
     */
    public static class Emphasis extends com.storedobject.chart.Emphasis {

        /**
         * Definition of the "fading out" of other elements when emphasizing.
         *
         * @author Syam
         */
        public enum FADE_OUT {

            /**
             * Do not fade out
             */
            NONE("none"),
            /**
             * All except the focused item.
             */
            ALL_OTHERS("self"),
            /**
             * All other charts.
             */
            OTHERS("series");

            private final String code;

            FADE_OUT(String code) {
                this.code = code;
            }

            @Override
            public String toString() {
                return code;
            }
        }

        /**
         * Definition of how the "fade out" is spread across other elements.
         *
         * @author Syam
         */
        public enum FADE_OUT_SCOPE {

            /**
             * Local (within the current coordinate system).
             */
            LOCAL("coordinateSystem"),
            /**
             * On the current chart only.
             */
            CHART("series"),
            /**
             * Global (in all charts).
             */
            GLOBAL("global");

            private final String code;

            FADE_OUT_SCOPE(String code) {
                this.code = code;
            }

            @Override
            public String toString() {
                return code;
            }
        }
        private FADE_OUT fadeOut;
        private FADE_OUT_SCOPE fadeOutScope;

        /**
         * Specify how other elements will be faded out when emphasizing an element.
         *
         * @param fadeOut Fade out.
         */
        public void setFadeOut(FADE_OUT fadeOut) {
            this.fadeOut = fadeOut;
        }

        /**
         * Set the scope of the fade out.
         *
         * @param fadeOutScope Fade out scope.
         */
        public void setFadeOutScope(FADE_OUT_SCOPE fadeOutScope) {
            this.fadeOutScope = fadeOutScope;
        }

        @Override
        protected void encodeProperty(StringBuilder sb) {
            super.encodeProperty(sb);
            ComponentPart.encode(sb, "focus", fadeOut);
            ComponentPart.encode(sb, "blurScope", fadeOutScope);
        }
    }
}
