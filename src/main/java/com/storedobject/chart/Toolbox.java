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

/**
 * Toolbox provides certain utilities (Example: "Download the chart display as an image"). Each utility is
 * accessed via a {@link ToolboxButton} part that can be added to the {@link Toolbox} using the method
 * {@link #addButton(ToolboxButton...)}. Some standard buttons are already available as static classes in this
 * class itself.
 *
 * @author Syam
 */
public class Toolbox extends AbstractDisplayablePart implements Component {

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
        sb.append("\"tooltip\":{\"show\":true}");
        if(vertical) {
            sb.append(',');
            ComponentPart.encode(sb, "orient", "vertical");
        }
        if(show) {
            sb.append(",\"feature\":{");
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

    /**
     * Get the position of this in the display area.
     *
     * @return Position.
     */
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * Set the position of this in the display area.
     *
     * @param position Position.
     */
    public void setPosition(Position position) {
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
    }
}
