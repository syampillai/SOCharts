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

/**
 * <p>
 * Chart. Since this is a concrete class, this may be directly used for creating a chart of a particular {@link ChartType}.
 * It has got the flexibility that the {@link ChartType} can be changed at any time using {@link #setType(ChartType)} method.
 * However, there are concrete derivatives of this class such as {@link PieChart}, {@link NightingaleRoseChart} etc.
 * where more chart-specific methods are available and data for the chart is checked more accurately for errors. If
 * the data set for the chart is of invalid type, system tries to do its best to adapt that data but the chart may not
 * appear if the data conversion fails.
 * </p>
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
public class Chart extends AbstractPart implements Component {

    List<Axis> axes;
    private ChartType type = ChartType.Line;
    private Label label;
    private String name;
    CoordinateSystem coordinateSystem;
    private AbstractDataProvider<?>[] data;
    private AbstractColor[] colors;
    private final Map<Class<? extends ComponentProperty>, ComponentProperty> propertyMap = new HashMap<>();
    private final Map<Class<? extends ComponentProperty>, String> propertyNameMap = new HashMap<>();

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

    private String type() {
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

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(colors != null) {
            sb.append("\"color\":[");
            for(int i = 0; i < colors.length; i++) {
                if(i > 0) {
                    sb.append(',');
                }
                sb.append(colors[i]);
            }
            sb.append("],");
        }
        AbstractDataProvider<?> dataToEmbed = dataToEmbed();
        if(dataToEmbed != null) {
            sb.append("\"data\":").append(dataToEmbed.getSerial()).append(',');
        }
        ComponentPart.encode(sb, "type", type());
        if(coordinateSystem != null) {
            if(coordinateSystem.axes != axes) {
                ComponentPart aw;
                if(axes != null && !axes.isEmpty()) {
                    for (Axis a : axes) {
                        aw = a.wrap(coordinateSystem);
                        if (aw.getSerial() > 0) {
                            sb.append(",\"").append(a.axisName()).append("Index\":").append(aw.getSerial());
                        }
                    }
                }
            } else {
                Set<Class<?>> axisClasses = new HashSet<>();
                coordinateSystem.axes.forEach(a -> axisClasses.add(a.getClass()));
                axisClasses.forEach(ac -> coordinateSystem.axes(ac).map(a -> a.wrap(coordinateSystem)).
                        min(Comparator.comparing(ComponentPart::getSerial)).ifPresent(w -> {
                            if(w.getSerial() > 0) {
                                sb.append(",\"").append(((Axis.AxisWrapper)w).axis.axisName()).append("Index\":").
                                        append(w.getSerial());
                            }
            }));
            }
        }
        if(coordinateSystem != null) {
            String name = coordinateSystem.systemName();
            if(name != null) {
                ComponentPart.addComma(sb);
                ComponentPart.encode(sb, "coordinateSystem", name);
            }
        }
        propertyMap.values().forEach(p -> {
            ComponentPart.addComma(sb);
            sb.append('\"').append(getPropertyName(p)).append("\":{");
            ComponentPart.encodeProperty(sb, p);
            sb.append("}");
        });
        if(label != null) {
            sb.append(",\"label\":{");
            label.chart = this;
            ComponentPart.encodeProperty(sb, label);
            sb.append('}');
        }
        if(dataToEmbed != null) {
            return;
        }
        sb.append(",\"encode\":{");
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
        if(coordinateSystem != null) {
            soChart.addParts(coordinateSystem);
            coordinateSystem.addParts(soChart);
        } else {
            soChart.addParts(data);
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
     * set of axes.
     *
     * @param coordinateSystem Coordinate system on which the chart will be plotted. (If it was plotted on
     *                         another coordinate system, it will be removed from it).
     * @param axes Axes to be used by the chart. This needs to be specified if the coordinate system has
     *             multiple axes of the required type.
     * @return Self reference.
     */
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
     * @return Self reference.
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
     * Set name for this chart.
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
     * Get the label for this chart values.
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
     * Set the label for this chart values.
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
     * Value-label that can be customized for a chart.
     *
     * @author Syam
     */
    public static class Label extends com.storedobject.chart.Label {

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
         * accordingly. The index indicates is the order in which the axes were added to the chart.</p>
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
            this.formatter = formatter;
        }

        private String format(String f) {
            for(int i = 0; i < chart.data.length; i++) {
                f = f.replace("{" + i + "}", "{@d" + chart.data[i].getSerial() + "}");
            }
            return f.replace("{chart}", "{a}");
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

        private boolean left = false, right = false, top = false, bottom = false;
        private Size x, y;

        /**
         * Set the (x, y) position. The origin is at top-left point of the bounding rectangle.
         *
         * @param x X position.
         * @param y Y position.
         */
        public void at(Size x, Size y) {
            this.x = x;
            this.y = y;
            left = right = top = bottom = false;
        }

        /**
         * Set the label position to the left side.
         *
         * @return Self reference.
         */
        public LabelPosition left() {
            x = y = null;
            left = true;
            right = false;
            return this;
        }

        /**
         * Set the label position to the right side.
         *
         * @return Self reference.
         */
        public LabelPosition right() {
            x = y = null;
            left = false;
            right = true;
            return this;
        }

        /**
         * Set the label position to the top side.
         *
         * @return Self reference.
         */
        public LabelPosition top() {
            x = y = null;
            top = true;
            bottom = false;
            return this;
        }

        /**
         * Set the label position to the bottom side.
         *
         * @return Self reference.
         */
        public LabelPosition bottom() {
            x = y = null;
            top = false;
            bottom = true;
            return this;
        }

        private boolean set(Size size) {
            switch(size.get()) {
                case -101:
                    left = true;
                    break;
                case -102:
                case -111:
                case -112:
                    top = true;
                    break;
                case -103:
                    right = true;
                    break;
                case -113:
                    bottom = true;
                    break;
                default:
                    return false;
            }
            return true;
        }

        @Override
        public String toString() {
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
                s = "Inside" + s;
            }
            if(s.isEmpty()) {
                return s;
            }
            return ",\"position\":\"" + s.substring(0, 1).toLowerCase() + s.substring(1) + "\"";
        }
    }
}
