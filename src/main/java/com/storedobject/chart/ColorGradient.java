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

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of color gradient.
 *
 * @author Syam
 */
public class ColorGradient extends AbstractColor {

    private final List<Color> colors = new ArrayList<>();
    private int x1 = 0, y1 = 0, x2 = 0, y2 = 100;

    /**
     * Constructor.
     *
     * @param colors Colors to set.
     */
    public ColorGradient(Color... colors) {
        setColors(colors);
    }

    /**
     * Get the color-bands.
     *
     * @return Colors.
     */
    public final List<Color> getColors() {
        return colors;
    }

    /**
     * Set color-bands. When multiple colors are set, gradient parameters may be set additionally using
     * methods {@link #setGradient(int, int, int, int)} and {@link #setGradient(int, int, int)}.
     *
     * @param colors Colors to set.
     */
    public void setColors(Color... colors) {
        this.colors.clear();
        if(colors != null) {
            for (Color c : colors) {
                if (c != null) {
                    this.colors.add(c);
                }
            }
        }
    }

    /**
     * Set parameters for linear gradient. All parameter values are percentage values in the bounding box.
     * Color-stops will be at equal intervals based on the number of colors defined using {@link #setColors(Color...)}.
     *
     * @param x1 Must be a value from 0 to 100.
     * @param y1 Must be a value from 0 to 100.
     * @param x2 Must be a value from 0 to 100.
     * @param y2 Must be a value from 0 to 100.
     */
    public void setGradient(int x1, int y1, int x2, int y2) {
        this.x1 = mm(x1);
        this.x2 = mm(x2);
        this.y1 = mm(y1);
        this.y2 = mm(y2);
    }

    /**
     * Set parameters for radial gradient. All parameter values are percentage values in the bounding box (starts at
     * the center and moves out radially).
     * Color-stops will be at equal intervals based on the number of colors defined using {@link #setColors(Color...)}.
     *
     * @param x Must be a value from 0 to 100.
     * @param y Must be a value from 0 to 100.
     * @param r Must be a value from 0 to 100.
     */
    public void setGradient(int x, int y, int r) {
        this.x1 = mm(x);
        this.x2 = mm(r);
        this.y1 = mm(y);
        this.y2 = Integer.MAX_VALUE;
    }

    private static int mm(int v) {
        return Math.max(0, Math.min(100, v));
    }

    @Override
    public String toString() {
        if(colors.isEmpty()) {
            return Color.TRANSPARENT.toString();
        }
        if(colors.size() == 1) {
            return colors.get(0).toString();
        }
        StringBuilder sb = new StringBuilder("{\"type\":\"");
        sb.append(y2 == Integer.MAX_VALUE ? "radial" : "linear").append("\",\"x\":");
        sb.append(((double) x1) / 100.0).append(",\"y\":").append(((double) y1) / 100.0).append(",\"");
        if(y2 == Integer.MAX_VALUE) {
            sb.append("r\":").append(((double) x2) / 100.0);
        } else {
            sb.append("x2\":").append(((double) x2) / 100.0).append(",\"y2\":").append(((double) y2) / 100.0);
        }
        sb.append(",\"colorStops\":[");
        double slice = 100.0 / (colors.size() - 1);
        for(int i = 0; i < colors.size(); i++) {
            if(i != 0) {
                sb.append(',');
            }
            sb.append("{\"offset\":");
            if(i == 0) {
                sb.append('0');
            } else if(i == colors.size() - 1) {
                sb.append('1');
            } else {
                sb.append(i * slice / 100.0);
            }
            sb.append(",\"color\":").append(colors.get(i)).append('}');
        }
        sb.append("]}");
        return sb.toString();
    }
}
