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
 * Chart type. Please note that there are certain types of charts that are derivative of the types mentioned here.
 * Examples: {@link DonutChart}, {@link NightingaleRoseChart}.
 *
 * @author Syam
 */
public enum ChartType {

    /**
     * Line.
     */
    Line,
    /**
     * Bar.
     */
    Bar,
    /**
     * Pie.
     */
    Pie(new String[] { "itemName", "value" }, false),
    /**
     * Scatter.
     */
    Scatter,
    /**
     * Effect-scatter.
     */
    EffectScatter,
    /**
     * Funnel.
     */
    Funnel(new String[] { "itemName", "value" }, false),
    /**
     * Radar.
     */
    Radar(new String[] {}),
    /**
     * Gauge.
     */
    Gauge(new String[] {}, false),
    /**
     * Tree.
     */
    Tree(new String[] {}, false),
    /**
     * Tree-map.
     */
    Treemap(new String[] {}, false),
    /**
     * Sunburst.
     */
    Sunburst(new String[] {}, false),
    /**
     * Heatmap.
     */
    Heatmap(new String[] {}, true),
    /**
     * Graph.
     */
    Graph(new String[] {}, false),
    /**
     * Candlestick.
     */
    Candlestick,
    /**
     * Sankey.
     */
    Sankey(new String[] {}, false),

    /**
     * Graph plotted on XY coordinates.
     */
    XYGraph("graph"),

    /**
     * Boxplot.
     */
    Boxplot,
    ;

    private final String[] axes;
    private final boolean coordinateSystem;
    private final String type;

    /**
     * Constructor for default XY type.
     */
    ChartType() {
        this(new String[] { "x", "y"});
    }

    /**
     * Constructor for the default XY type but with a different type name.
     *
     * @param type Type name.
     */
    ChartType(String type) {
        this(new String[] { "x", "y"}, true, type);
    }

    /**
     * Constructor with axes defined.
     *
     * @param axes Name of the axes.
     */
    ChartType(String[] axes) {
        this(axes, true, null);
    }

    /**
     * Constructor with axes defined.
     *
     * @param axes Name of the axes.
     * @param coordinateSystem Whether a coordinate system is required for this type or not.
     */
    ChartType(String[] axes, boolean coordinateSystem) {
        this(axes, coordinateSystem, null);
    }

    /**
     * Constructor with axes defined.
     *
     * @param axes Name of the axes.
     * @param coordinateSystem Whether a coordinate system is required for this type or not.
     * @param type Type name. (If null, it will be set to the name of the enum value in the lower case.)
     */
    ChartType(String[] axes, boolean coordinateSystem, String type) {
        this.axes = axes;
        this.coordinateSystem = coordinateSystem;
        if(type == null) {
            String t = name();
            type = Character.toLowerCase(t.charAt(0)) + t.substring(1);
        }
        this.type = type;
    }

    /**
     * Get the name of the axes.
     *
     * @return Name of axes.
     */
    public String[] getAxes() {
        return axes;
    }

    /**
     * Check whether a coordinate system is required for this type or not.
     *
     * @return True or false.
     */
    public boolean requireCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Retrieves the type name of the chart.
     *
     * @return The type name of the chart as a String.
     */
    public String getType() {
        return type;
    }
}