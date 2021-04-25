package com.storedobject.chart;

/**
 * Image that can be added to {@link SOChart}.
 *
 * @author Syam
 */
public class Image extends Shape {

    private final ImageContent image = new ImageContent();

    public Image(String imageURL) {
        this.image.imageURL = imageURL;
        getStyle(true).extra = this.image;
    }

    @Override
    protected String getType() {
        return "image";
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.deleteCharAt(sb.length() - 1);
    }

    @Override
    public void setStyle(Style style) {
        super.setStyle(style);
        getStyle(true).extra = image;
    }

    /**
     * Get current image URL.
     *
     * @return Current image URL.
     */
    public String getImageURL() {
        return image.imageURL;
    }

    /**
     * Set the image URL.
     *
     * @param imageURL Image URL to set.
     */
    public void setImageURL(String imageURL) {
        this.image.imageURL = imageURL;
    }

    /**
     * Get width in pixels.
     *
     * @return Width.
     */
    public final Number getWidth() {
        return this.image.width;
    }

    /**
     * Set width.
     *
     * @param width Width in pixels.
     */
    public void setWidth(Number width) {
        this.image.width = width;
    }

    /**
     * Get height in pixels.
     *
     * @return Height.
     */
    public final Number getHeight() {
        return this.image.height;
    }

    /**
     * Seth height.
     *
     * @param height Height in pixels.
     */
    public void setHeight(Number height) {
        this.image.height = height;
    }

    private static class ImageContent implements ComponentProperty {

        String imageURL;
        Number width, height;

        @Override
        public void encodeJSON(StringBuilder sb) {
            ComponentPart.encode(sb, "image", imageURL, true);
            if(width != null && width.doubleValue() > 0) {
                ComponentPart.encode(sb, "width", width, true);
            }
            if(height != null && height.doubleValue() > 0) {
                ComponentPart.encode(sb, "height", height, true);
            }
        }
    }
}
