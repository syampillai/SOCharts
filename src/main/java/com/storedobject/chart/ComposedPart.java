package com.storedobject.chart;

/**
 * A part whose properties can be composed of a few configuration variables.
 *
 * @author Syam
 */
public class ComposedPart extends AbstractPart
        implements HasPosition, HasPadding, HasPolarProperty, HasAnimation, HasEmphasis, HasLabel, HasItemStyle {

    private final boolean hasVisibility, hasPosition, hasPadding, hasPolarProperty, hasAnimation, hasEmphasis, hasLabel,
            hasItemStyle;
    private boolean show = true;
    private PolarProperty polarProperty;
    private Position position;
    private Padding padding;
    private Animation animation;
    private Emphasis emphasis;
    private Label label;
    private ItemStyle itemStyle;

    /**
     * Constructor.
     *
     * @param hasVisibility Whether this part supports visibility or not.
     * @param hasPosition Whether this part supports positioning or not.
     * @param hasPadding  Whether this part supports padding or not.
     * @param hasPolarProperty Whether this part supports polar property or not.
     * @param hasAnimation  Whether this part supports animation or not.
     * @param hasEmphasis  Whether this part supports emphasis or not.
     * @param hasLabel  Whether this part supports label or not.
     * @param hasItemStyle  Whether this part supports item-style or not.
     */
    public ComposedPart(boolean hasVisibility, boolean hasPosition, boolean hasPadding, boolean hasPolarProperty,
                        boolean hasAnimation, boolean hasEmphasis, boolean hasLabel, boolean hasItemStyle) {
        this.hasVisibility = hasVisibility;
        this.hasPosition = hasPosition;
        this.hasPadding = hasPadding;
        this.hasPolarProperty = hasPolarProperty;
        this.hasAnimation = hasAnimation;
        this.hasEmphasis = hasEmphasis;
        this.hasLabel = hasLabel;
        this.hasItemStyle = hasItemStyle;
    }

    /**
     * Show this part. This call ignored if the component doesn't have visibility property.
     */
    public void show() {
        show = true;
    }

    /**
     * Hide this part. This call ignored if the component doesn't have visibility property.
     */
    public void hide() {
        show = false;
    }

    @Override
    public void validate() throws ChartException {
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(hasVisibility) {
            ComponentPart.encode(sb, "show", show);
        }
    }

    @Override
    public final PolarProperty getPolarProperty(boolean create) {
        if(hasPolarProperty && polarProperty == null && create) {
            polarProperty = new PolarProperty();
        }
        return polarProperty;
    }

    @Override
    public final void setPolarProperty(PolarProperty polarProperty) {
        if(hasPolarProperty) {
            this.polarProperty = polarProperty;
        }
    }

    @Override
    public final Position getPosition(boolean create) {
        if(hasPosition && position == null && create) {
            position = new Position();
        }
        return position;
    }

    @Override
    public final void setPosition(Position position) {
        if(hasPosition) {
            this.position = position;
        }
    }

    @Override
    public Padding getPadding(boolean create) {
        if(hasPadding && padding == null && create) {
            padding = new Padding();
        }
        return padding;
    }

    @Override
    public void setPadding(Padding padding) {
        if(hasPadding) {
            this.padding = padding;
        }
    }

    @Override
    public Animation getAnimation(boolean create) {
        if(hasAnimation && create && animation == null) {
            animation = new Animation();
        }
        return animation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if(hasAnimation) {
            this.animation = animation;
        }
    }

    @Override
    public Emphasis getEmphasis(boolean create) {
        if(hasEmphasis && emphasis == null && create) {
            emphasis = new Emphasis();
        }
        return emphasis;
    }

    @Override
    public void setEmphasis(Emphasis emphasis) {
        if(hasEmphasis) {
            this.emphasis = emphasis;
        }
    }

    @Override
    public Label getLabel(boolean create) {
        if(hasLabel && label == null && create) {
            Class<? extends Label> labelClass = getLabelClass();
            if(labelClass != null) {
                try {
                    label = labelClass.getDeclaredConstructor().newInstance();
                } catch(Throwable ignored) {
                }
            }
        }
        return label;
    }

    @Override
    public void setLabel(Label label) {
        if(hasLabel) {
            if(label != null && !getLabelClass().isAssignableFrom(label.getClass())) {
                return;
            }
            this.label = label;
        }
    }

    /**
     * This is invoked to determine the class of the label to create. The class should have a default constructor.
     *
     * @return Label.
     */
    protected Class<? extends Label> getLabelClass() {
        return null;
    }

    @Override
    public ItemStyle getItemStyle(boolean create) {
        if(itemStyle == null && create && hasItemStyle) {
            itemStyle = new ItemStyle();
        }
        return itemStyle;
    }

    @Override
    public void setItemStyle(ItemStyle itemStyle) {
        if(hasItemStyle) {
            this.itemStyle = itemStyle;
        }
    }
}
