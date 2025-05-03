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
 * As the name indicates, "text style" is for styling texts.
 *
 * @author Syam
 */
public class TextStyle implements ComponentProperty {

    private AbstractColor color, background;
    private Font font;
    private Padding padding;
    private Border border;
    private Alignment alignment;
    private TextBorder textBorder;
    private RichTextStyle richTextStyle;

    @Override
    public void encodeJSON(StringBuilder sb) {
        encode(sb, "color", color);
        if(font != null) {
            if(font.getStyle() != null) {
                encode(sb, "fontStyle", font.getStyle());
            }
            if(font.getWeight() != null) {
                encode(sb, "fontWeight", font.getWeight());
            }
            encode(sb, "fontFamily", font.getFamily());
            encode(sb, "fontSize", font.getSize());
        }
        encode(sb, "backgroundColor", background);
        ComponentPart.encode(sb, null, border);
        ComponentPart.encode(sb, null, padding);
        ComponentPart.encode(sb, null, alignment);
        if(textBorder != null) {
            textBorder.setPrefix("text");
        }
        ComponentPart.encode(sb, null, textBorder);
        if(richTextStyle != null && !richTextStyle.parts.isEmpty()) {
            ComponentPart.addComma(sb);
            sb.append("\"rich\":{");
            richTextStyle.parts.forEach((p, s) -> {
                sb.append('"').append(p).append("\":{");
                ComponentPart.encode(sb, null, s);
                sb.append("},");
            });
            ComponentPart.removeComma(sb);
            sb.append('}');
        }
    }

    private static void encode(StringBuilder sb, String name, Object value) {
        if(value == null) {
            return;
        }
        if(value instanceof Integer && (Integer)value <= 0) {
            return;
        }
        ComponentPart.addComma(sb);
        if(value instanceof AbstractColor) {
            sb.append('"').append(name).append("\":").append(value);
            return;
        }
        ComponentPart.encode(sb, name, value);
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
     * Get background color.
     *
     * @return Background color.
     */
    public final AbstractColor getBackground() {
        return background;
    }

    /**
     * Set background color.
     *
     * @param background Background color.
     */
    public void setBackground(AbstractColor background) {
        this.background = background;
    }

    /**
     * Get font.
     *
     * @return Font.
     */
    public final Font getFontStyle() {
        return font;
    }

    /**
     * Set font.
     *
     * @param font Font.
     */
    public void setFontStyle(Font font) {
        this.font = font;
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
        if(alignment == null && create) {
            alignment = new Alignment();
        }
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

    /**
     * Get the {@link RichTextStyle} instance associated with this text style.
     *
     * @param create Whether to create if not exists or not.
     * @return {@link RichTextStyle} if exists or created just now.
     */
    public RichTextStyle getRichTextStyle(boolean create) {
        if(create && richTextStyle == null) {
            richTextStyle = new RichTextStyle();
        }
        return richTextStyle;
    }

    /**
     * Set a {@link RichTextStyle} instance that was created elsewhere.
     *
     * @param richTextStyle {@link RichTextStyle} to set.
     */
    public void setRichTextStyle(RichTextStyle richTextStyle) {
        this.richTextStyle = richTextStyle;
    }

    /**
     * Save state. For internal use only.
     *
     * @param op Save to this.
     */
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

    /**
     * Restore state. For internal purposes only.
     *
     * @param op Restore from this.
     */
    void restore(OuterProperties op) {
        background = op.background;
        padding = op.padding;
        border = op.border;
        alignment = op.alignment;
    }

    /**
     * Store definition to save/restore state.
     *
     * @author Syam
     */
    static class OuterProperties {
        AbstractColor background;
        Padding padding;
        Border border;
        Alignment alignment;
    }
}