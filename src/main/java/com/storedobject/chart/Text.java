package com.storedobject.chart;

/**
 * Text that can be added to {@link SOChart}.
 *
 * @author Syam
 */
public class Text extends Shape {

    private final TextContent text = new TextContent();

    /**
     * Constructor.
     *
     * @param text Text value to set.
     */
    public Text(String text) {
        this.text.text = text;
        getStyle(true).extra = this.text;
        this.text.alignment = new Alignment();
        this.text.alignment.center();
    }

    @Override
    protected final String getType() {
        return "text";
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.deleteCharAt(sb.length() - 1);
    }

    @Override
    public void setStyle(Style style) {
        super.setStyle(style);
        getStyle(true).extra = text;
    }

    /**
     * Get current content.
     *
     * @return Current text content.
     */
    public String getText() {
        return text.text;
    }

    /**
     * Set the content.
     *
     * @param text Text value to set.
     */
    public void setText(String text) {
        this.text.text = text;
    }

    /**
     * Set the font.
     *
     * @param font Font to set,
     */
    public void setFont(Font font) {
        this.text.font = font;
    }

    /**
     * Get the current font.
     *
     * @return Font.
     */
    public Font getFont() {
        return text.font;
    }

    /**
     * Get text alignment.
     *
     * @param create Whether to create it or not, if it doesn't exists.
     * @return Alignment.
     */
    public Alignment getAlignment(boolean create) {
        if(text.alignment == null && create) {
            text.alignment = new Alignment();
        }
        return text.alignment;
    }

    /**
     * Set text alignment.
     *
     * @param alignment Alignment.
     */
    public void setAlignment(Alignment alignment) {
        this.text.alignment = alignment;
    }

    private static class TextContent implements ComponentProperty {

        String text;
        Font font;
        Alignment alignment;

        @Override
        public void encodeJSON(StringBuilder sb) {
            ComponentPart.encode(sb, "text", text);
            if(font != null) {
                ComponentPart.encode(sb, "font", font);
            }
            if(alignment != null) {
                sb.append(',');
                alignment.setPrefix("text");
                alignment.encodeJSON(sb);
            }
        }
    }
}
