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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Toolbox provides certain utilities (Example: "Download the chart display as an image"). Each utility is
 * accessed via a {@link ToolboxButton} part that can be added to the {@link Toolbox} using the method
 * {@link #addButton(ToolboxButton...)}. Some standard buttons are already available as static classes in this
 * class itself.
 *
 * @author Syam
 */
public class Toolbox extends VisiblePart implements Component, HasPosition {

    private final List<ToolboxButton> buttons = new ArrayList<>();
    private Position position;
    private boolean vertical = false;

    /**
     * Constructor.
     */
    public Toolbox() {
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.addComma(sb);
        sb.append("\"tooltip\":{\"show\":true}");
        if(vertical) {
            ComponentPart.encode(sb, "orient", "vertical");
        }
        if(show) {
            ComponentPart.addComma(sb);
            sb.append("\"feature\":{");
            for (ToolboxButton button : buttons) {
                if(button instanceof Internal) {
                    sb.append('"').append(((Internal) button).getTag()).append("\":{");
                } else {
                    continue;
                }
                button.encodeJSON(sb);
                sb.append("},");
            }
            ComponentPart.removeComma(sb);
            sb.append('}');
        }
    }

    @Override
    public void validate() {
    }

    @Override
    public final Position getPosition(boolean create) {
        if(position == null && create) {
            position = new Position();
        }
        return position;
    }

    @Override
    public final void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Change the orientation of the toolbox display to vertical.
     */
    public void showVertically() {
        vertical = true;
    }

    /**
     * Add too buttons.
     *
     * @param buttons Buttons to add.
     */
    public void addButton(ToolboxButton... buttons) {
        if(buttons != null) {
            this.buttons.addAll(Arrays.asList(buttons));
        }
    }

    /**
     * Remove tool buttons.
     *
     * @param buttons Buttons to remove.
     */
    public void removeButton(ToolboxButton... buttons) {
        if(buttons != null) {
            this.buttons.removeAll(Arrays.asList(buttons));
        }
    }

    private interface Internal {
        String getTag();
    }

    /**
     * Download tool button. Clicking this will download the displayed charts as an image.
     *
     * @author Syam
     */
    public final static class Download extends ToolboxButton implements Internal {

        private int resolution = 1;

        /**
         * Constructor.
         */
        public Download() {
            setCaption("Download");
        }

        /**
         * Get the tag for this tool button. (For internal use only).
         *
         * @return Tag.
         */
        @Override
        public String getTag() {
            return "saveAsImage";
        }

        /**
         * Get image resolution.
         *
         * @return Resolution
         */
        public int getResolution() {
            return resolution;
        }

        /**
         * Set image resolution. Resolution is a number greater than zero. Higher the number, higher the resolution.
         *
         * @param resolution Image resolution.
         */
        public void setResolution(int resolution) {
            this.resolution = resolution;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            if(resolution > 1) {
                sb.append(',');
                ComponentPart.encode(sb, "pixelRatio", resolution);
            }
        }
    }

    /**
     * Restore tool button. Clicking this will restore the displayed charts to the state prior to user interaction.
     *
     * @author Syam
     */
    public final static class Restore extends ToolboxButton implements Internal {

        /**
         * Constructor.
         */
        public Restore() {
            setCaption("Restore");
        }

        /**
         * Get the tag for this tool button. (For internal use only).
         *
         * @return Tag.
         */
        @Override
        public String getTag() {
            return "restore";
        }
    }

    /**
     * Zoom tool button. This allows data zooming for {@link RectangularCoordinate} systems.
     *
     * @author Syam
     */
    public final static class Zoom extends ToolboxButton implements Internal {

        private String resetCaption = "Reset zoom";
        private XAxis[] xAxes = null;
        private YAxis[] yAxes = null;

        /**
         * Constructor.
         */
        public Zoom() {
            setCaption("Zoom in");
        }

        /**
         * Get the tag for this tool button. (For internal use only).
         *
         * @return Tag.
         */
        @Override
        public String getTag() {
            return "dataZoom";
        }

        @Override
        protected void encodeCaptionJSON(StringBuilder sb) {
            if(xAxes != null) {
                sb.append(",\"xAxisIndex\":");
                if(xAxes.length == 0) {
                    sb.append("false");
                } else {
                    sb.append('[')
                            .append(Stream.of(xAxes).map(a -> String.valueOf(a.getRenderingIndex()))
                                    .collect(Collectors.joining(",")))
                            .append(']');
                }
            }
            if(yAxes != null) {
                sb.append(",\"yAxisIndex\":");
                if(yAxes.length == 0) {
                    sb.append("false");
                } else {
                    sb.append('[')
                            .append(Stream.of(yAxes).map(a -> String.valueOf(a.getRenderingIndex()))
                                    .collect(Collectors.joining(",")))
                            .append(']');
                }
            }
            String c = getCaption(), rc = getResetCaption();
            if(c == null && rc == null) {
                return;
            }
            sb.append(",\"title\":{");
            if(c != null) {
                ComponentPart.encode(sb, "zoom", c);
            }
            if(rc != null) {
                if(c != null) {
                    sb.append(',');
                }
                ComponentPart.encode(sb, "back", rc);
            }
            sb.append('}');
        }
        /**
         * Get the caption for the "reset zoom" part (will be shown as a tooltip).
         *
         * @return Caption.
         */
        public String getResetCaption() {
            return resetCaption;
        }

        /**
         * Set the caption for the "reset zoom" part (will be shown as a tooltip).
         *
         * @param resetCaption Caption.
         */
        public void setResetCaption(String resetCaption) {
            this.resetCaption = resetCaption;
        }

        /**
         * Set the applicable {@link XAxis} list while zooming. By default, all the axes are applicable. However, you
         * may pass the ones you want. If you pass no parameter, that is, <code>setXAxes()</code>, no {@link XAxis} is
         * applicable.
         *
         * @param xAxes {@link XAxis} list.
         */
        public void setXAxes(XAxis... xAxes) {
            this.xAxes = xAxes;
        }

        /**
         * Set the applicable {@link YAxis} list while zooming. By default, all the axes are applicable. However, you
         * may pass the ones you want. If you pass no parameter, that is, <code>setYAxes()</code>, no {@link YAxis} is
         * applicable.
         *
         * @param yAxes {@link YAxis} list.
         */
        public void setYAxes(YAxis... yAxes) {
            this.yAxes = yAxes;
        }
    }

    public static class DataView extends ToolboxButton implements Internal {

        public DataView() {
            setCaption("Data View");
        }

        @Override
        public String getTag() {
            return "dataView";
        }
    }
}
