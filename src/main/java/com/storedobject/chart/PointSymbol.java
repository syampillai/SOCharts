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
 * Represents the symbol used to draw a point on the chart. (Example usage is in {@link LineChart}).
 *
 * @author Syam
 */
public class PointSymbol implements ComponentProperty {

    private PointSymbolType type;
    private boolean show = true;
    String size;
    private boolean hoverAnimation = true;
    private String url = null;
    private Offset offset;

    /**
     * Default constructor for the {@code PointSymbol} class.
     * Creates a {@code PointSymbol} with the default symbol type {@code PointSymbolType.CIRCLE}.
     */
    public PointSymbol() {
        this(PointSymbolType.CIRCLE);
    }

    /**
     * Constructs a PointSymbol with the specified {@link PointSymbolType}.
     *
     * @param type The type of the point symbol to be used.
     */
    public PointSymbol(PointSymbolType type) {
        this.type = type;
    }

    /**
     * Sets the URL for the point symbol. The URL should start with either "image://" or "path://".
     * If it starts with "image://", then the URL should be a valid URL pointing to an image, and if it starts
     * with "path://", then it should be valid SVG code.
     * If the provided URL is null, empty, or does not start with valid prefixes, the URL is set to null.
     * Additionally, the point symbol type is ignored when a valid URL is provided.
     *
     * @param url The URL to be set for the point symbol. Valid URLs should start with either "image://" or "path://".
     */
    public void setURL(String url) {
        if(url == null || url.isEmpty()) {
            this.url = null;
            return;
        }
        if(url.startsWith("image://") || url.startsWith("path://")) {
            this.url = url;
        } else {
            this.url = null;
        }
    }

    /**
     * Makes the point symbol visible on the chart.
     */
    public void show() {
        show = true;
    }

    /**
     * Hides the point symbol by setting its visibility to false.
     */
    public void hide() {
        show = false;
    }

    /**
     * Sets the type of the point symbol.
     *
     * @param pointSymbolType The type of the point symbol to assign.
     */
    public void setType(PointSymbolType pointSymbolType) {
        this.type = pointSymbolType;
    }

    /**
     * Retrieves the current type of the point symbol.
     *
     * @return The current {@link PointSymbolType} of the point symbol.
     */
    public final PointSymbolType getType() {
        return type;
    }

    /**
     * Sets the size of the point symbol based on the given value. If the input
     * size is less than or equal to zero, the size will be set to null.
     * Otherwise, the size is set to the string representation of the given value.
     *
     * @param size The size of the point symbol to set. A positive value sets the size,
     *             while a non-positive value resets it to null.
     */
    public void setSize(int size) {
        if(size <= 0) {
            this.size = null;
        } else {
            this.size = "" + size;
        }
    }

    /**
     * Sets the size of the point symbol and returns the updated {@code PointSymbol} instance.
     * The size determines the visual dimensions of the point symbol.
     *
     * @param size The size of the point symbol to set. A positive value assigns the size,
     *             while a non-positive value resets the size to {@code null}.
     * @return The updated {@code PointSymbol} instance with the specified size applied.
     */
    public PointSymbol size(int size) {
        setSize(size);
        return this;
    }

    /**
     * Sets the size of the symbol using specified width and height values.
     *
     * @param width The width of the symbol. Should be a positive value.
     * @param height The height of the symbol. Should be a positive value.
     *               If both width and height are not positive, the size will be set to null.
     *               If only one of width or height is positive,
     *               the size will be adjusted accordingly by reusing other overloads of this method.
     */
    public void setSize(int width, int height) {
        if(width > 0 && height > 0) {
            this.size = "[" + width + "," + height + "]";
        } else if(width > 0) {
            setSize(width);
        } else if(height > 0) {
            setSize(height);
        } else {
            this.size = null;
        }
    }

    /**
     * Updates the size of the point symbol using the specified width and height values.
     * The size defines the visual dimensions of the point symbol and influences its
     * rendering within the chart.
     *
     * @param width The width of the point symbol. Should be a positive value.
     *              If width is non-positive, the size may be reset depending on height value.
     * @param height The height of the point symbol. Should be a positive value.
     *               If height is non-positive, the size may be reset depending on width value.
     * @return The updated {@code PointSymbol} instance with the specified size applied.
     */
    public PointSymbol size(int width, int height) {
        setSize(width, height);
        return this;
    }

    /**
     * Sets the hover animation property for the symbol. When enabled, the symbol will display
     * animations on hover, typically enhancing the visual interaction for users.
     *
     * @param hoverAnimation Specifies whether the hover animation should be enabled or disabled.
     *                        A value of {@code true} enables the animation, while {@code false} disables it.
     */
    public void setHoverAnimation(boolean hoverAnimation) {
        this.hoverAnimation = hoverAnimation;
    }

    /**
     * Sets the offset for the point symbol. The offset determines the horizontal (x-axis) and
     * vertical (y-axis) displacement of the symbol.
     *
     * @param offset The offset to be set for the point symbol. It specifies the x and y offsets
     *               in relation to the default position.
     */
    public void setOffset(Offset offset) {
        this.offset = offset;
    }

    /**
     * Sets the offset for the {@code PointSymbol} and returns the updated instance.
     *
     * @param offset The {@code Offset} to be applied to the {@code PointSymbol}.
     * @return The updated {@code PointSymbol} instance with the specified offset applied.
     */
    public PointSymbol offset(Offset offset) {
        setOffset(offset);
        return this;
    }

    /**
     * Retrieves the offset for the point symbol. If the offset does not already exist and the
     * {@code createIfNotExists} parameter is {@code true}, a new {@link Offset} instance will be created.
     *
     * @param createIfNotExists A boolean flag indicating whether to create a new {@link Offset}
     *                          instance if none exists. If {@code true}, a new offset will be created
     *                          when it does not exist; otherwise, {@code null} will be returned if it
     *                          does not already exist.
     * @return The {@link Offset} instance associated with the point symbol. May return {@code null}
     *         if no offset exists and {@code createIfNotExists} is {@code false}.
     */
    public Offset getOffset(boolean createIfNotExists) {
        if(offset == null && createIfNotExists) {
            offset = new Offset();
        }
        return offset;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        String t = url == null ? this.type.toString() : url;
        ComponentPart.encode(sb,"symbol", t);
        if(size != null) {
            sb.append(",\"symbolSize\":").append(size);
        }
        ComponentPart.encode(sb, "hoverAnimation", hoverAnimation);
        ComponentPart.encode(sb, "showSymbol", show);
        Offset o = getOffset(false);
        if(o != null) {
            o.encodeJSON("symbolOffset", sb);
        }
    }
}
