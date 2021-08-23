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
 * Represents "data zoom" component. Data zoom components allow the end-users
 * to zoom in and to zoom out charts using mouse and/or touch devices. This component ignores
 * the "width" and the "height" properties that are set via its {@link Position}.
 *
 * @author Syam
 */
public class DataZoom extends AbstractDataZoom implements HasPosition {

    private boolean show = true;
    private Position position;
    private AbstractColor background, fillerColor, borderColor;
    private HandleStyle handleStyle;
    private DataShadowStyle dataShadowStyle;

    /**
     * Constructor.
     *
     * @param coordinateSystem Coordinate system.
     * @param axis Axis list.
     */
    public DataZoom(CoordinateSystem coordinateSystem, Axis... axis) {
        super("slider", coordinateSystem, axis);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append(",\"show\":").append(show);
        sb.append(",\"handleSize\":\"100%\"");
        ComponentPart.encode(sb, null, position);
        if(background != null) {
            ComponentPart.encode(sb, "backgroundColor", background);
        }
        if(fillerColor != null) {
            ComponentPart.encode(sb, "fillerColor", fillerColor);
        }
        if(borderColor != null) {
            ComponentPart.encode(sb, "borderColor", borderColor);
        }
        ComponentPart.encode(sb, "handleStyle", handleStyle);
        if(dataShadowStyle != null && dataShadowStyle.valid()) {
            ComponentPart.encode(sb, "dataBackground", dataShadowStyle);
        }
    }

    /**
     * Show this.
     */
    public void show() {
        show = true;
    }

    /**
     * Hide this.
     */
    public void hide() {
        show = false;
    }

    @Override
    public final Position getPosition(boolean create) {
        if(this instanceof HasPolarProperty) {
            return null;
        }
        if(position == null && create) {
            position = new Position();
        }
        return position;
    }

    @Override
    public final void setPosition(Position position) {
        if(this instanceof HasPolarProperty) {
            return;
        }
        this.position = position;
    }

    /**
     * Get the background color.
     *
     * @return Background color.
     */
    public final AbstractColor getBackground() {
        return background;
    }

    /**
     * Set the background color.
     *
     * @param background Background color.
     */
    public void setBackground(AbstractColor background) {
        this.background = background;
    }

    /**
     * Get the filler color.
     *
     * @return Filler color.
     */
    public final AbstractColor getFillerColor() {
        return fillerColor;
    }

    /**
     * Set the filler color to fill the selected area.
     *
     * @param fillerColor Filler color.
     */
    public void setFillerColor(AbstractColor fillerColor) {
        this.fillerColor = fillerColor;
    }

    /**
     * Get the border color.
     *
     * @return Border color.
     */
    public final AbstractColor getBorderColor() {
        return borderColor;
    }

    /**
     * Set the border color.
     *
     * @param borderColor Border color.
     */
    public void setBorderColor(AbstractColor borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Get the handle style.
     *
     * @param create Whether to create it or not.
     * @return Handle style.
     */
    public final HandleStyle getHandleStyle(boolean create) {
        if(handleStyle == null && create) {
            handleStyle = new HandleStyle();
        }
        return handleStyle;
    }

    /**
     * Set the handle style.
     *
     * @param handleStyle Handle style.
     */
    public void setHandleStyle(HandleStyle handleStyle) {
        this.handleStyle = handleStyle;
    }

    /**
     * Get the style of the data shadow.
     *
     * @param create Whether to create it or not.
     * @return Data shadow style.
     */
    public DataShadowStyle getDataShadowStyle(boolean create) {
        if(dataShadowStyle == null && create) {
            dataShadowStyle = new DataShadowStyle();
        }
        return dataShadowStyle;
    }

    /**
     * Set the style of the data shadow.
     *
     * @param dataShadowStyle Data shadow style.
     */
    public void setDataShadowStyle(DataShadowStyle dataShadowStyle) {
        this.dataShadowStyle = dataShadowStyle;
    }

    /**
     * Used to style the handle.
     *
     * @author Syam
     */
    public static class HandleStyle implements ComponentProperty {

        private AbstractColor color, borderColor;
        private int borderWidth = -1;
        private LineStyle.Type borderType;
        private Shadow shadow;

        /**
         * Constructor.
         */
        public HandleStyle() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            if(color != null) {
                ComponentPart.encode(sb, "color", color);
            }
            if(borderColor != null) {
                ComponentPart.encode(sb, "borderColor", borderColor);
            }
            if(borderType != null) {
                ComponentPart.encode(sb, "borderType", borderType.toString().toLowerCase());
            }
            if(borderWidth >= 0) {
                ComponentPart.encode(sb, "borderWidth", borderWidth);
                borderWidth = -1;
            }
            ComponentPart.encode(sb, null, shadow);
        }

        /**
         * Get color.
         *
         * @return Color.
         */
        public final AbstractColor getColor() {
            return color;
        }

        /**
         * Set color.
         *
         * @param color Color.
         */
        public void setColor(AbstractColor color) {
            this.color = color;
        }

        /**
         * Get the border color.
         *
         * @return Border color.
         */
        public final AbstractColor getBorderColor() {
            return borderColor;
        }

        /**
         * Set the border color.
         *
         * @param color Border color.
         */
        public void setBorderColor(AbstractColor color) {
            this.borderColor = color;
        }

        /**
         * Get the border width.
         *
         * @return Border width.
         */
        public final int getBorderWidth() {
            return Math.max(borderWidth, 0);
        }

        /**
         * Set the border width.
         *
         * @param borderWidth Border width.
         */
        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
        }

        /**
         * Get the type of the border.
         *
         * @return Border type.
         */
        public LineStyle.Type getBorderType() {
            return borderType;
        }

        /**
         * Set the type of the border.
         *
         * @param borderType Border type.
         */
        public void setBorderType(LineStyle.Type borderType) {
            this.borderType = borderType;
        }

        /**
         * Get the shadow.
         *
         * @param create Whether to create if not exists or not.
         * @return Shadow.
         */
        public final Shadow getShadow(boolean create) {
            if(shadow == null && create) {
                shadow = new Shadow();
            }
            return shadow;
        }

        /**
         * Set the shadow.
         *
         * @param shadow Shadow.
         */
        public void setShadow(Shadow shadow) {
            this.shadow = shadow;
        }
    }

    /**
     * Used to style the data shadow.
     *
     * @author Syam
     */
    public static class DataShadowStyle implements ComponentProperty {

        private LineStyle lineStyle;
        private AreaStyle areaStyle;

        /**
         * Constructor.
         */
        public DataShadowStyle() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            ComponentPart.encode(sb, null, lineStyle);
            ComponentPart.encode(sb, null, areaStyle);
        }

        private boolean valid() {
            return lineStyle != null || areaStyle != null;
        }

        /**
         * Get the line style.
         *
         * @param create Whether to create it or not.
         * @return Line style.
         */
        public final LineStyle getLineStyle(boolean create) {
            if(lineStyle == null && create) {
                lineStyle = new LineStyle();
            }
            return lineStyle;
        }

        /**
         * Set the line style.
         *
         * @param lineStyle Line style.
         */
        public void setLineStyle(LineStyle lineStyle) {
            this.lineStyle = lineStyle;
        }

        /**
         * Get the area style.
         *
         * @param create Whether to create it or not.
         * @return Area style.
         */
        public final AreaStyle getAreaStyle(boolean create) {
            if(areaStyle == null && create) {
                areaStyle = new AreaStyle();
            }
            return areaStyle;
        }

        /**
         * Set the area style.
         *
         * @param areaStyle Area style.
         */
        public void setAreaStyle(AreaStyle areaStyle) {
            this.areaStyle = areaStyle;
        }
    }
}