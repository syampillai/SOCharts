package com.storedobject.chart;

import java.util.HashMap;
import java.util.Map;

/**
 * When styling any text using {@link TextStyle}, parts of the text can be separately styled. This class represents
 * those parts with additional style details. Each part has a {@link TextStyle} of its own.
 * <p>This is mainly used when you set "formatter" strings such as
 * {@link com.storedobject.chart.Axis.Label#setFormatter(String)},
 * {@link com.storedobject.chart.Chart.Label#setFormatter(String)} etc. If you want to style parts of the
 * formatted value in a different way, you can define a "style part" in using this class instance and specified in
 * the formatter string like {part|...} where "part" is the part name. For example, in the case of
 * {@link com.storedobject.chart.Chart.Label}, if you have a formatter "{1} {chart} (in kg)" and if you want
 * to show the portion "{chart} (in kg)" in another style, you can modify the formatter like this:
 * "{1} {name|{chart} (in kg)}" where name is the "part name" you created using {@link RichTextStyle}.</p>
 *
 * @author Syam
 */
public class RichTextStyle {

    final Map<String, TextStyle> parts = new HashMap<>();

    /**
     * Get the text style for the given part.
     *
     * @param partName Name of the part.
     * @param create Whether to create if not exists or not.
     * @return Text style.
     */
    public TextStyle get(String partName, boolean create) {
        if(partName == null) {
            return null;
        }
        partName = sanitizeName(partName);
        TS ts = (TS) parts.get(partName);
        if(ts == null && create) {
            ts = new TS();
            parts.put(partName, ts);
        }
        return ts;
    }

    /**
     * Add a part. If the part name already exists, it will be overwritten.
     *
     * @param partName Part name.
     * @param textStyle Text style to add. It must have been created from this {@link RichTextStyle} or
     *                  from some other {@link RichTextStyle}. You can freely mix/share text styles across multiple
     *                  {@link RichTextStyle} instances.
     */
    public void add(String partName, TextStyle textStyle) {
        if(!(textStyle instanceof TS)) {
            throw new RuntimeException("No a rich text style!");
        }
        if(partName == null) {
            return;
        }
        partName = sanitizeName(partName);
        parts.put(partName, textStyle);
    }

    /**
     * Remove a part.
     *
     * @param partName Name of the part to remove.
     */
    public void remove(String partName) {
        if(partName != null) {
            parts.remove(sanitizeName(partName));
        }
    }

    private static String sanitizeName(String partName) {
        partName = partName.strip();
        StringBuilder sb = new StringBuilder();
        partName.chars().forEach(c -> {
            if(Character.isLetterOrDigit(c)) {
                sb.append((char) c);
            } else {
                sb.append('_');
            }
        });
        partName = sb.toString();
        if(partName.isEmpty()) {
            return  "part";
        }
        if(Character.isDigit(partName.charAt(0))) {
            partName = "part_" + partName;
        }
        return partName;
    }

    private static class TS extends TextStyle {

        @Override
        public RichTextStyle getRichTextStyle(boolean create) {
            return null;
        }

        @Override
        public void setRichTextStyle(RichTextStyle richTextStyle) {
        }
    }
}
