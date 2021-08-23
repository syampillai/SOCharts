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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Mark area. Used to define style for one or more individual areas in many charts.
 * Examples: {@link LineChart}, {@link ScatterChart}, {@link PieChart} etc.
 *
 * @author Syam
 */
public class MarkArea implements ComponentProperty, HasData {

    private ItemStyle itemStyle;
    private final List<Block> blocks = new ArrayList<>();
    final AbstractDataProvider<?> data = new Data();

    @Override
    public void declareData(Set<AbstractDataProvider<?>> dataSet) {
        dataSet.add(data);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        ComponentPart.encode(sb, "itemStyle", itemStyle);
        ComponentPart.encode(sb, "data", data.getSerial());
    }

    /**
     * Get item style.
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
     * Add blocks to this mark area.
     *
     * @param blocks Blocks to add.
     */
    public void add(Block... blocks) {
        if(blocks != null) {
            Collections.addAll(this.blocks, blocks);
        }
    }

    /**
     * Remove blocks from this mark area.
     *
     * @param blocks Blocks to remove.
     */
    public void remove(Block... blocks) {
        if(blocks != null) {
            for(Block block: blocks) {
                this.blocks.remove(block);
            }
        }
    }

    /**
     * Clear all blocks.
     */
    public void clear() {
        blocks.clear();
    }

    /**
     * A block represents an area of the {@link MarkArea}. A block has 2 {@link Side}s viz. "the starting side",
     * "the ending side".
     * <p>A block may be defined along an axis and in that case, it covers an area along that axis with the starting
     * side and ending side based on the starting value and ending value along that axis.</p>
     * <p>A block may be defined using both the axes (mostly in the case of {@link RectangularCoordinate} and in that
     * case, it will have to be specified using 2 values for each axis. The block will be formed from the
     * intersection of the axis values.</p>
     * <p>There are a few special strings that can be used when specifying the values and when such values
     * are used, it is treated as special cases. The following special strings are currently supported:</p>
     * <pre>
     *     "min": Represents minimum value of the axis
     *     "max": Represents maximum value of the axis
     *     "average": The average value when the chart's values (applicable for numeric data)
     * </pre>
     *
     * @author Syam
     */
    public static class Block implements ComponentProperty {

        private String name;
        private ItemStyle itemStyle;
        private Axis axis1, axis2;
        private final Side start = new Side(), end = new Side();

        @Override
        public void encodeJSON(StringBuilder sb) {
            sb.append("[{");
            ComponentPart.encode(sb, "name", name);
            ComponentPart.encode(sb, "itemStyle", itemStyle);
            start.encodeJSON(sb);
            sb.append("},{");
            end.encodeJSON(sb);
            sb.append("}]");
        }

        /**
         * Get the name of the block.
         *
         * @return Name.
         */
        public final String getName() {
            return name;
        }

        /**
         * Set the name of the block.
         *
         * @param name Name.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Get the starting side.
         *
         * @return Side.
         */
        public final Side getStartingSide() {
            return start;
        }

        /**
         * Get the ending side.
         *
         * @return Side.
         */
        public final Side getEndingSide() {
            return end;
        }

        /**
         * Helper method to set sides. A value may be defined as a reserved string that has special meaning. The
         * currently supported reserved strings are: "min", "max", "average".
         *
         * @param axis Axis.
         * @param valueFrom Value from. Value should match with the {@link DataType} of the chart's axis or one of the
         *                  reserved strings.
         * @param valueTo Value to. Value should match with the {@link DataType} of the chart's axis or one of the
         *                  reserved strings.
         */
        public void setSides(Axis axis, Object valueFrom, Object valueTo) {
            sides(axis, null, valueFrom, null, valueTo, null);
        }

        /**
         * Helper method to set sides. A value may be defined as a reserved string that has special meaning. The
         * currently supported reserved strings are: "min", "max", "average".
         *
         * @param xAxis X axis.
         * @param yAxis Y axis.
         * @param xValueFrom X value from. Value should match with the {@link DataType} of the chart or one of the
         *                  reserved strings.
         * @param yValueFrom Y value from. Value should match with the {@link DataType} of the chart's axis or
         *                   one of the reserved strings.
         * @param xValueTo X value to. Value should match with the {@link DataType} of the chart's axis or one of the
         *                  reserved strings.
         * @param yValueTo Y value to. Value should match with the {@link DataType} of the chart's axis or one of the
         *                  reserved strings.
         */
        public void setSides(XAxis xAxis, YAxis yAxis, Object xValueFrom, Object yValueFrom, Object xValueTo,
                             Object yValueTo) {
            sides(xAxis, yAxis, xValueFrom,yValueFrom, xValueTo, yValueTo);
        }

        private void sides(Axis xAxis, Axis yAxis, Object xValueFrom, Object yValueFrom, Object xValueTo,
                             Object yValueTo) {
            axis1 = xAxis;
            start.value1 = xValueFrom;
            axis2 = yAxis;
            start.value2 = yValueFrom;
            end.value1 = xValueTo;
            end.value2 = yValueTo;
        }

        /**
         * Get item style for this block. (Each block can have its own item style).
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
         * Set item style. (Each block can have its own item style).
         *
         * @param itemStyle Style to set.
         */
        public void setItemStyle(ItemStyle itemStyle) {
            this.itemStyle = itemStyle;
        }

        /**
         * A block has 2 sides, the starting side and an ending side. This class represents a side.
         *
         * @author Syam
         */
        public class Side implements ComponentProperty {

            private Object value1, value2;

            @Override
            public void encodeJSON(StringBuilder sb) {
                if(axis1 != null) {
                    ComponentPart.encode(sb, axis1.axisName(), value1);
                }
                if(axis2 != null) {
                    ComponentPart.encode(sb, axis2.axisName(), value2);
                }
            }

            /**
             * Set the value for the side.
             *
             * @param value Value to set.
             */
            public void setValue(Object value) {
                setValue(value, null);
            }

            /**
             * Set the X and Y values for the side.
             *
             * @param valueX X value to set.
             * @param valueY Y value to set.
             */
            public void setValue(Object valueX, Object valueY) {
                this.value1 = valueX;
                this.value2 = valueY;
            }
        }
    }

    private class Data extends BasicDataProvider<Object> {

        @Override
        public Stream<Object> stream() {
            return blocks.stream().map(o -> o);
        }

        @Override
        public void encode(StringBuilder sb, Object value) {
            ((Block)value).encodeJSON(sb);
        }
    }
}
