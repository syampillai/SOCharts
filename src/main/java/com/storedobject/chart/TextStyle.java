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
 * As the name indicates, "text style" is for styling texts.
 *
 * @author Syam
 */
public class TextStyle implements ComponentProperty {

    private Color color, background;
    private FontStyle fontStyle;
    private FontWeight fontWeight;
    private String fontFamily;
    private int fontSize = -1;
    private Padding padding;
    private Border border;
    private Alignment alignment;
    private TextBorder textBorder;

    @Override
    public void encodeJSON(StringBuilder sb) {
        encode(sb, "color", color);
        encode(sb, "fontStyle", fontStyle);
        encode(sb, "fontWeight", fontWeight);
        encode(sb, "fontFamily", fontFamily);
        encode(sb, "fontSize", fontSize);
        encode(sb, "backgroundColor", background);
        ComponentPart.encodeProperty(sb, border);
        ComponentPart.encodeProperty(sb, padding);
        ComponentPart.encodeProperty(sb, alignment);
        if(textBorder != null) {
            textBorder.setPrefix("text");
        }
        ComponentPart.encodeProperty(sb, textBorder);
    }

    private static void encode(StringBuilder sb, String name, Object value) {
        if(value == null) {
            return;
        }
        if(value instanceof Integer && (Integer)value <= 0) {
            return;
        }
        ComponentPart.addComma(sb);
        ComponentPart.encode(sb, name, value);
    }

    /**
     * Get color.
     *
     * @return Color.
     */
    public final Color getColor() {
        return color;
    }

    /**
     * Set color.
     *
     * @param color Color.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get background color.
     *
     * @return Background color.
     */
    public final Color getBackground() {
        return background;
    }

    /**
     * Set background color.
     *
     * @param background Background color.
     */
    public void setBackground(Color background) {
        this.background = background;
    }

    /**
     * Get font-style.
     *
     * @return Font-style.
     */
    public final FontStyle getFontStyle() {
        return fontStyle;
    }

    /**
     * Set font-style.
     *
     * @param fontStyle Font-style.
     */
    public void setFontStyle(FontStyle fontStyle) {
        this.fontStyle = fontStyle;
    }

    /**
     * Get font-size.
     *
     * @return Font-size.
     */
    public final int getFontSize() {
        return fontSize;
    }

    /**
     * Set font-size.
     *
     * @param fontSize Font-size.
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Get font-weight.
     *
     * @return Font-weight.
     */
    public final FontWeight getFontWeight() {
        return fontWeight;
    }

    /**
     * Set font-weight.
     *
     * @param fontWeight Font-weight.
     */
    public void setFontWeight(FontWeight fontWeight) {
        this.fontWeight = fontWeight;
    }

    /**
     * Get font-family.
     *
     * @return Font-family.
     */
    public final String getFontFamily() {
        return fontFamily;
    }

    /**
     * Set font-family. (Example: "sans-serif", "serif", "monospace" etc.).
     *
     * @param fontFamily Font-family.
     */
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    /**
     * Get the padding.
     * @param create Whether to create if not exists or not.
     *
     * @return Padding.
     */
    public final Padding getPadding(boolean create) {
        if(padding == null && create) {
            padding = new Padding();
        }
        return padding;
    }

    /**
     * Set the padding.
     *
     * @param padding Padding.
     */
    public void setPadding(Padding padding) {
        this.padding = padding;
    }

    /**
     * Get the border.
     * @param create Whether to create if not exists or not.
     *
     * @return Border.
     */
    public final Border getBorder(boolean create) {
        if(border == null && create) {
            border = new Border();
        }
        return border;
    }

    /**
     * Set the border.
     *
     * @param border Border.
     */
    public void setBorder(Border border) {
        this.border = border;
    }

    /**
     * Get the text border.
     *
     * @param create Whether to create if not exists or not.
     * @return Text border.
     */
    public final TextBorder getTextBorder(boolean create) {
        if(textBorder == null && create) {
            textBorder = new TextBorder();
        }
        return textBorder;
    }

    /**
     * Set the text border.
     *
     * @param textBorder Text border.
     */
    public final void setTextBorder(TextBorder textBorder) {
        this.textBorder = textBorder;
    }

    /**
     * Get the alignment.
     *
     * @param create Whether to create if not exists or not.
     * @return Alignment.
     */
    public Alignment getAlignment(boolean create) {
        return alignment;
    }

    /**
     * Set the alignment.
     *
     * @param alignment Alignment.
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    void save(OuterProperties op) {
        op.background = background;
        background = null;
        op.padding = padding;
        padding = null;
        op.border = border;
        border = null;
        op.alignment = alignment;
        alignment = null;
    }

    void restore(OuterProperties op) {
        background = op.background;
        padding = op.padding;
        border = op.border;
        alignment = op.alignment;
    }

    static class OuterProperties {
        Color background;
        Padding padding;
        Border border;
        Alignment alignment;
    }
}