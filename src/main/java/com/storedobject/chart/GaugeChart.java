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

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Gauge chart.
 *
 * @author Syam
 */
public class GaugeChart extends SelfPositioningChart implements HasPolarProperty {

    private int startAngle = Integer.MIN_VALUE, endAngle = Integer.MIN_VALUE, divisions = Integer.MIN_VALUE;
    private Number min = Integer.MIN_VALUE, max = Integer.MIN_VALUE;
    private PolarProperty polarProperty;
    private final NeedleData data;
    private AxisLine axisLine;

    /**
     * Constructor with a single needle for the gauge.
     */
    public GaugeChart() {
        this(1);
    }

    /**
     * Constructor with a single needle for the gauge.
     *
     * @param valueName Name of the value for this gauge.
     */
    public GaugeChart(String valueName) {
        this(1);
        data.needles[0].setName(valueName);
    }

    /**
     * Constructor with number of needles for the gauge.
     *
     * @param needles Number of needles for the gauge.
     */
    public GaugeChart(int needles) {
        this(new Needle[needles <= 0 ? 1 : needles]);
    }

    private GaugeChart(Needle[] needles) {
        this(new NeedleData(needles));
        for(int needle = 0; needle < needles.length; needle++) {
            needles[needle] = new Needle();
        }
    }

    private GaugeChart(NeedleData needleData) {
        super(ChartType.Gauge,false, needleData);
        this.data = needleData;
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return data;
    }

    /**
     * Set the current value. If there are more than one needle, value for the first needle will be set.
     *
     * @param value Value to set.
     */
    public void setValue(Number value) {
        setValue(value, 0);
    }

    /**
     * Get the current value. If there are more than one needle, value for the first needle will be returned.
     *
     * @return Value.
     */
    public Number getValue() {
        return data.needles[0].getValue();
    }

    /**
     * Set value of a given needle. (Used when more than one needle is used).
     *
     * @param value Value to set.
     * @param needle Needle index.
     */
    public void setValue(Number value, int needle) {
        if(needle >= 0 && needle < data.needles.length) {
            data.needles[needle].setValue(value);
        }
    }

    /**
     * Get value of a given needle. (Used when more than one needle is used).
     *
     * @param needle Needle index.
     * @return Value.
     */
    public Number getValue(int needle) {
        return needle >= 0 && needle < data.needles.length ? data.needles[needle].getValue() : null;
    }

    /**
     * Get number of needles.
     *
     * @return Number of needles.
     */
    public int getNumberOfNeedles() {
        return data.needles.length;
    }

    @Override
    protected String getLabelName() {
        return "axisLabel";
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        Label label = getLabel(false);
        if(label != null) {
            label.chart = this;
        }
        String formatter = label == null ? null : label.getFormatter();
        String f = formatter == null ? null : label.getFormatterValue();
        if(formatter != null) {
            label.setFormatter(null);
        }
        super.encodeJSON(sb);
        ComponentPart.encode(sb, null, polarProperty);
        encode(sb, "startAngle", startAngle);
        encode(sb, "endAngle", endAngle);
        encode(sb, "min", min);
        encode(sb, "max", max);
        encode(sb, "splitNumber", divisions);
        sb.append(",\"detail\":{\"fontSize\":12,\"valueAnimation\":true,\"color\":\"auto\"");
        if(label != null) {
            if(f != null) {
                sb.append(",\"formatter\":").append(ComponentPart.escape(f));
            }
        }
        sb.append('}');
        if(formatter != null) {
            label.setFormatter(formatter);
        }
        ComponentPart.encode(sb, "axisLine", axisLine);
        sb.append(",\"pointer\":{\"itemStyle\":{\"color\":\"auto\"}}");
    }

    private void encode(StringBuilder sb, String name, Number value) {
        if(value.intValue() == Integer.MIN_VALUE) {
            return;
        }
        ComponentPart.addComma(sb);
        ComponentPart.encode(sb, name, value);
    }

    @Override
    public final PolarProperty getPolarProperty(boolean create) {
        if(polarProperty == null && create) {
            polarProperty = new PolarProperty();
        }
        return polarProperty;
    }

    @Override
    public final void setPolarProperty(PolarProperty polarProperty) {
        this.polarProperty = polarProperty;
    }

    /**
     * Set the starting angle of the gauge.
     *
     * @param startAngle Starting angle.
     */
    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    /**
     * Set the ending angle of the gauge.
     *
     * @param endAngle Ending angle.
     */
    public void setEndAngle(int endAngle) {
        this.endAngle = endAngle;
    }

    /**
     * Set number of divisions on the dial.
     *
     * @param divisions Number of divisions.
     */
    public void setDivisions(int divisions) {
        this.divisions = divisions;
    }

    /**
     * Set minimum value on the dial.
     *
     * @param min Minimum value.
     */
    public void setMin(Number min) {
        this.min = min;
    }

    /**
     * Set maximum value on the dial.
     *
     * @param max Maximum value.
     */
    public void setMax(Number max) {
        this.max = max;
    }

    /**
     * Needle property of the gauge chart.
     *
     * @author Syam
     */
    public static class Needle implements ComponentProperty {

        private Number value = 0;
        private String name;

        private Needle() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            sb.append("{\"value\":").append(value).append(",\"name\":").
                    append(ComponentPart.escape(name)).append('}');
        }

        /**
         * Get current value.
         *
         * @return Current value.
         */
        public Number getValue() {
            return value;
        }

        /**
         * Set the value.
         *
         * @param value Value to set.
         */
        public void setValue(Number value) {
            this.value = value;
        }

        /**
         * Get the name of the needle (This will be displayed on the dial).
         *
         * @return Name.
         */
        public String getName() {
            return name;
        }

        /**
         * Set the name of the needle (This will be displayed on the dial).
         *
         * @param name Name to set.
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    private static class NeedleData implements AbstractDataProvider<Needle> {

        private final Needle[] needles;
        private int serial = -1;

        private NeedleData(Needle[] needles) {
            this.needles = needles;
        }

        @Override
        public Stream<Needle> stream() {
            return Stream.of(needles);
        }

        @Override
        public DataType getDataType() {
            return DataType.OBJECT;
        }

        @Override
        public void setSerial(int serial) {
            this.serial = serial;
        }

        @Override
        public int getSerial() {
            return serial;
        }

        @Override
        public void encode(StringBuilder sb, Needle value) {
            value.encodeJSON(sb);
        }
    }

    public static class AxisLine extends VisiblePart {

        private boolean roundCap;
        private final AxisLineStyle style = new AxisLineStyle();

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            ComponentPart.encode(sb, "roundCap", roundCap);
            ComponentPart.encode(sb, "lineStyle", style);
        }

        public final LineStyle getStyle() {
            return style;
        }
    }

    private static class AxisLineStyle extends LineStyle {

        private final Map<Integer, AbstractColor> zones = new TreeMap<>();

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            if(zones.isEmpty()) {
                return;
            }
            ComponentPart.addComma(sb);
            sb.append("\"color\":[");
            AtomicBoolean first = new AtomicBoolean(true);
            zones.keySet().forEach(k -> {
                if(first.get()) {
                    first.set(false);
                } else {
                    sb.append(',');
                }
                sb.append('[').append(k / 100.0).append(',').append(zones.get(k)).append(']');
            });
            sb.append(']');
        }

        void addZone(int zone, AbstractColor color) {
            zone = Math.max(0, zone);
            zone = Math.min(zone, 100);
            zones.put(zone, color);
        }
    }

    /**
     * Add a zone to the dial that should show in a different color. You can add multiple zones and each zone should
     * specify the percentage of the dial to which a given color can be used. So, if you want to show the first 20% in
     * "yellow", the next 50% in "green" and the last 30% in "red", you should call this method as follows:
     * <pre>
     *     addDialZone(20, new Color("yellow")); // First 20%
     *     addDialZone(70, new Color("green")); // Next 50% (that means, show up to 20 + 50 = 70%)
     *     addDialZone(100, new Color("red")); // Last 30% (that means, show up to 20 + 50 + 30 = 100%)
     * </pre>
     *
     * @param percentage Percentage of the dial to which the given color should be used.
     * @param color Color.
     */
    public void addDialZone(int percentage, AbstractColor color) {
        if(percentage <= 0) {
            return;
        }
        if(color == null) {
            color = Color.TRANSPARENT;
        }
        getAxisLine(true).style.addZone(percentage, color);
    }

    /**
     * Get the axis line for this gauge.
     *
     * @param create If passed true, it will be created if not exists.
     * @return Axis Line instance
     */
    public AxisLine getAxisLine(boolean create) {
        if(axisLine == null && create) {
            axisLine = new AxisLine();
        }
        return axisLine;
    }

    /**
     * Get the axis label for this gauge.
     *
     * @param create If passed true, it will be created if not exists.
     * @return Label instance
     */
    public Label getAxisLabel(boolean create) {
        return getLabel(create);
    }
}
