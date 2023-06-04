package com.storedobject.chart;

/**
 * Defines Font.
 *
 * @author Syam
 */
public class Font {

    /**
     * Represents font styles.
     *
     * @author Syam
     */
    public enum Style {
        /**
         * Normal.
         */
        NORMAL,
        /**
         * Italic.
         */
        ITALIC,
        /**
         * Oblique.
         */
        OBLIQUE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    /**
     * Represents font variants.
     *
     * @author Syam
     */
    public enum Variant {
        /**
         * Normal.
         */
        NORMAL,
        /**
         * Small-caps.
         */
        SMALL_CAPS;

        @Override
        public String toString() {
            return super.toString().toLowerCase().replace('_', '-');
        }
    }

    private Style style;
    private Weight weight;
    private Variant variant;
    private Size size, lineHeight;
    private Family family;
    private Stretch stretch;

    public Font(Family family, Size size) {
        this(family, null, size);
    }

    public Font(Family family, Style style, Size size) {
        setFamily(family);
        this.style = style;
        setSize(size);
    }

    /**
     * Get font-style.
     *
     * @return Font-style.
     */
    public final Style getStyle() {
        return style;
    }

    /**
     * Set font-style.
     *
     * @param style Font-style.
     */
    public void setStyle(Style style) {
        this.style = style;
    }

    /**
     * Get font-size.
     *
     * @return Font-size.
     */
    public final Size getSize() {
        return size;
    }

    /**
     * Set font-size.
     *
     * @param size Font-size.
     */
    public final void setSize(Size size) {
        this.size = size == null ? Size.medium() : size;
    }

    /**
     * Get line-height of this font.
     *
     * @return Font-size.
     */
    public final Size getLineHeight() {
        return lineHeight;
    }

    /**
     * Set a line-height for this font.
     *
     * @param size Font-size.
     */
    public void setLineHeight(Size size) {
        this.lineHeight = size;
    }

    /**
     * Get font-weight.
     *
     * @return Font-weight.
     */
    public final Weight getWeight() {
        return weight;
    }

    /**
     * Set font-weight.
     *
     * @param weight Font-weight.
     */
    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    /**
     * Get font-variant.
     *
     * @return Variant.
     */
    public final Variant getVariant() {
        return variant;
    }

    /**
     * Set the font-variant.
     *
     * @param variant Variant.
     */
    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    /**
     * Get font-family.
     *
     * @return Font-family.
     */
    public final Family getFamily() {
        return family;
    }

    /**
     * Set font-family. (Example: "sans-serif", "serif", "monospace" etc.).
     *
     * @param family Font-family.
     */
    public final void setFamily(Family family) {
        this.family = family == null ? Family.sans_serif() : family;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\"");
        if(style != null) {
            sb.append(style);
        }
        if(variant != null) {
            sb.append(' ').append(variant);
        }
        if(weight != null) {
            sb.append(' ').append(weight);
        }
        sb.append(' ').append(size);
        if(lineHeight != null) {
            sb.append('/').append(lineHeight);
        }
        sb.append(' ').append(family).append('"');
        return sb.charAt(0) == ' ' ? sb.substring(1): sb.toString();
    }

    /**
     * Represents font-size.
     *
     * @author Syam
     */
    public static class Size {

        // 0: xx-small, 1:x-small, 2:small, 3:medium, 4:large, 5:x-large, 6:xx-large, 7:xxx-large, 8: smaller, 9: larger
        private final int size;
        // Based on - 0: name, 1: percentage, 2: pixels, 3: point, 4: em, 5: rem, 6: just number
        private final int type;

        private Size(int size, int type) {
            this.size = size;
            this.type = type;
        }

        private Size(double size, int type) {
            this((int)(size * 1000), type);
        }

        public static Size percentage(double percentage) {
            return new Size(percentage, 1);
        }

        public static Size pixels(double pixels) {
            return new Size(pixels, 2);
        }

        public static Size points(double points) {
            return new Size(points, 3);
        }

        public static Size em(double em) {
            return new Size(em, 4);
        }

        public static Size rem(double rem) {
            return new Size(rem, 5);
        }

        public static Size number(int number) {
            return new Size(number, 6);
        }

        public static Size medium() {
            return new Size(3, 0);
        }

        public static Size large() {
            return new Size(4, 0);
        }

        public static Size larger() {
            return new Size(9, 0);
        }

        public static Size x_large() {
            return new Size(5, 0);
        }

        public static Size xx_large() {
            return new Size(6, 0);
        }

        public static Size xxx_large() {
            return new Size(7, 0);
        }

        public static Size small() {
            return new Size(2, 0);
        }

        public static Size smaller() {
            return new Size(8, 0);
        }

        public static Size x_small() {
            return new Size(1, 0);
        }

        public static Size xx_small() {
            return new Size(0, 0);
        }

        @Override
        public String toString() {
            if(type == 0) {
                switch(size) {
                    case 0 -> {
                        return "xx-small";
                    }
                    case 1 -> {
                        return "x-small";
                    }
                    case 2 -> {
                        return "small";
                    }
                    case 3 -> {
                        return "medium";
                    }
                    case 4 -> {
                        return "large";
                    }
                    case 5 -> {
                        return "x-large";
                    }
                    case 6 -> {
                        return "xx-large";
                    }
                    case 7 -> {
                        return "xxx-large";
                    }
                    case 8 -> {
                        return "smaller";
                    }
                    case 9 -> {
                        return "larger";
                    }
                }
            }
            String suffix = "";
            switch(type) {
                case 1 -> suffix = "%";
                case 2 -> suffix = "px";
                case 3 -> suffix = "pt";
                case 4 -> suffix = "em";
                case 5 -> suffix = "rem";
                case 6 -> {
                    return String.valueOf(size);
                }
            }
            String s = String.valueOf(size / 1000);
            int r = size % 1000;
            if(r > 0) {
                if(r < 10) {
                    s += ".00" + r;
                } else if(r < 100) {
                    s += ".0" + r;
                } else {
                    s += "." + r;
                }
            }
            return s + suffix;
        }
    }

    /**
     * Represents font-weight.
     *
     * @author Syam
     */
    public static class Weight {

        /**
         * Normal font-weight.
         */
        public static final Weight NORMAL = new Weight(0);
        /**
         * Bold font-weight.
         */
        public static final Weight BOLD = new Weight(-1);
        /**
         * Bolder font-weight.
         */
        public static final Weight BOLDER = new Weight(-2);
        /**
         * Lighter font-weight.
         */
        public static final Weight LIGHTER = new Weight(-3);

        private final int weight;

        /**
         * Constructor.
         *
         * @param weight Weight (Typically 1, 2, 3 etc. or 100, 200, 300 etc.)
         */
        public Weight(int weight) {
            if(weight < 100 && weight > 0) {
                weight *= 100;
            }
            this.weight = weight;
        }

        @Override
        public String toString() {
            if(weight <= 0) {
                return switch(weight) {
                    case -3 -> "lighter";
                    case -2 -> "bolder";
                    case -1 -> "bold";
                    default -> "normal";
                };
            }
            return String.valueOf(weight);
        }
    }

    /**
     * Represents font-family.
     *
     * @author Syam
     */
    public static class Family {

        final String family;
        final int type;
        Family fallback;

        private Family(String family, int type) {
            this.family = family;
            this.type = type;
        }

        private Family(int type) {
            this(null, type);
        }

        /**
         * Create a font-family with the name provided.
         *
         * @return Family created.
         */
        public static Family create(String familyName) {
            return new Family(familyName, 0);
        }

        /**
         * Create a font-family of type "serif".
         *
         * @return Family created.
         */
        public static Family serif() {
            return new Family(1);
        }

        /**
         * Create a font-family of type "sans-serif".
         *
         * @return Family created.
         */
        public static Family sans_serif() {
            return new Family(2);
        }

        /**
         * Create a font-family of type "monospace".
         *
         * @return Family created.
         */
        public static Family monospace() {
            return new Family(3);
        }

        /**
         * Create a font-family of type "cursive".
         *
         * @return Family created.
         */
        public static Family cursive() {
            return new Family(4);
        }

        /**
         * Create a font-family of type "fantasy".
         *
         * @return Family created.
         */
        public static Family fantasy() {
            return new Family(5);
        }

        /**
         * Create a font-family of type "system-ui".
         *
         * @return Family created.
         */
        public static Family system_ui() {
            return new Family(6);
        }

        /**
         * Create a font-family of type "ui-serif".
         *
         * @return Family created.
         */
        public static Family ui_serif() {
            return new Family(7);
        }

        /**
         * Create a font-family of type "ui-sans-serif".
         *
         * @return Family created.
         */
        public static Family ui_sans_serif() {
            return new Family(8);
        }

        /**
         * Create a font-family of type "ui-monospace".
         *
         * @return Family created.
         */
        public static Family ui_monospace() {
            return new Family(9);
        }

        /**
         * Create a font-family of type "ui-rounded".
         *
         * @return Family created.
         */
        public static Family ui_rounded() {
            return new Family(10);
        }

        /**
         * Create a font-family of type "math".
         *
         * @return Family created.
         */
        public static Family math() {
            return new Family(11);
        }

        /**
         * Create a font-family of type "emoji".
         *
         * @return Family created.
         */
        public static Family emoji() {
            return new Family(12);
        }

        /**
         * Create a font-family of type "fangsong".
         *
         * @return Family created.
         */
        public static Family fangsong() {
            return new Family(13);
        }

        /**
         * Add a fallback for this family.
         *
         * @param fallbackFamily Fallback family to add.
         */
        public void addFallback(Family fallbackFamily) {
            if(fallback == null) {
                fallback = fallbackFamily;
            } else {
                fallback.addFallback(fallbackFamily);
            }
        }

        @Override
        public String toString() {
            String s = switch(type) {
                case 1 -> "serif";
                case 2 -> "sans-serif";
                case 3 -> "monospace";
                case 4 -> "cursive";
                case 5 -> "fantasy";
                case 6 -> "system-ui";
                case 7 -> "ui-serif";
                case 8 -> "ui-sans-serif";
                case 9 -> "ui-monospace";
                case 10 -> "math";
                case 11 -> "emoji";
                case 12 -> "fangsong";
                default -> "'" + family + "'";
            };
            return plus(s);
        }

        private String plus(String s) {
            if(fallback == null) {
                return s;
            }
            return s + "," + fallback;
        }
    }

    /**
     * Represents font-stretch.
     *
     * @author Syam
     */
    public static class Stretch {

        final int value;

        private Stretch(int value) {
            this.value = value;
        }

        private Stretch(double value) {
            this((int)(value * 1000));
        }

        /**
         * Create stretch with the give percentage.
         *
         * @param percentageToStretch Percentage to stretch.
         * @return Stretch created.
         */
        public static Stretch create(double percentageToStretch) {
            return new Stretch(percentageToStretch);
        }

        /**
         * Create a stretch of type "ultra-condensed".
         *
         * @return Stretch created.
         */
        public static Stretch ultra_condensed() {
            return new Stretch(50000);
        }

        /**
         * Create a stretch of type ""normal.
         *
         * @return Stretch created.
         */
        public static Stretch normal() {
            return new Stretch(100000);
        }

        /**
         * Create a stretch of type "extra-condensed".
         *
         * @return Stretch created.
         */
        public static Stretch extra_condensed() {
            return new Stretch(62500);
        }

        /**
         * Create a stretch of type "condensed".
         *
         * @return Stretch created.
         */
        public static Stretch condensed() {
            return new Stretch(75000);
        }

        /**
         * Create a stretch of type "semi-condensed".
         *
         * @return Stretch created.
         */
        public static Stretch semi_condensed() {
            return new Stretch(87500);
        }

        /**
         * Create a stretch of type "expanded".
         *
         * @return Stretch created.
         */
        public static Stretch expanded() {
            return new Stretch(125000);
        }

        /**
         * Create a stretch of type "semi-expanded".
         *
         * @return Stretch created.
         */
        public static Stretch semi_expanded() {
            return new Stretch(112500);
        }

        /**
         * Create a stretch of type "extra-expanded".
         *
         * @return Stretch created.
         */
        public static Stretch extra_expanded() {
            return new Stretch(150000);
        }

        /**
         * Create a stretch of type "ultra-expanded".
         *
         * @return Stretch created.
         */
        public static Stretch ultra_expanded() {
            return new Stretch(200000);
        }

        /**
         * Get the percentage of stretch.
         *
         * @return Stretch percentage.
         */
        public double getPercentage() {
            return value / 1000.0;
        }

        @Override
        public String toString() {
            String s = String.valueOf(value / 1000);
            int r = value % 1000;
            if(r > 0) {
                if(r < 10) {
                    s += ".00" + r;
                } else if(r < 100) {
                    s += ".0" + r;
                } else {
                    s += "." + r;
                }
            }
            return s + "%";
        }
    }
}
