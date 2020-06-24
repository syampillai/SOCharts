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

/**
 * Gauge chart.
 *
 * @author Syam
 */
public class GaugeChart extends AbstractDataChart implements HasPolarProperty {

    private final Needle[] needles;
    private int startAngle = Integer.MIN_VALUE, endAngle = Integer.MIN_VALUE, divisions = Integer.MIN_VALUE;
    private Number min = Integer.MIN_VALUE, max = Integer.MIN_VALUE;
    private PolarProperty polarProperty;

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
        needles[0].setName(valueName);
    }

    /**
     * Constructor with number of needles for the gauge.
     *
     * @param needles Number of needles for the gauge.
     */
    public GaugeChart(int needles) {
        super(ChartType.Gauge);
        if(needles <= 0) {
            needles = 1;
        }
        this.needles = new Needle[needles];
        for(needles = 0; needles < this.needles.length; needles++) {
            this.needles[needles] = new Needle();
        }
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
        return needles[0].getValue();
    }

    /**
     * Set value of a given needle. (Used when more than one needle is used).
     *
     * @param value Value to set.
     * @param needle Needle index.
     */
    public void setValue(Number value, int needle) {
        if(needle >= 0 && needle < needles.length) {
            needles[needle].setValue(value);
        }
    }

    /**
     * Get value of a given needle. (Used when more than one needle is used).
     *
     * @param needle Needle index.
     * @return Value.
     */
    public Number getValue(int needle) {
        return needle >= 0 && needle < needles.length ? needles[needle].getValue() : null;
    }

    /**
     * Get number of needles.
     *
     * @return Number of needles.
     */
    public int getNumberOfNeedles() {
        return needles.length;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.encodeProperty(sb, polarProperty);
        encode(sb, "startAngle", startAngle);
        encode(sb, "endAngle", endAngle);
        encode(sb, "min", min);
        encode(sb, "max", max);
        encode(sb, "splitNumber", divisions);
        if(skippingData) {
            return;
        }
        sb.append(",\"data\":[");
        for (Needle needle : needles) {
            ComponentPart.encodeProperty(sb, needle);
        }
        sb.append(']');
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
}
