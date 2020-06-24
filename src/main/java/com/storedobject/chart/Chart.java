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
    private String name;
    CoordinateSystem coordinateSystem;
    private AbstractDataProvider<?>[] data;

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
    public AbstractDataProvider<?>[] getData() {
        return data;
    }

    private String type() {
        String t = type.toString();
        return Character.toLowerCase(t.charAt(0)) + t.substring(1);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
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
        if(this instanceof AbstractDataChart) {
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
                        throw new ChartException("Axis " + name + " doesn't belong to the coordinate system of this chart - "
                                + getName());
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
     * Set the name of the chart. (This will be used in displaying the legend if legend is enabled in the
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
}
