package com.storedobject.chart;

/**
 * Interface to denote that a {@link ComponentPart} supports animation.
 *
 * @author Syam
 */
public interface HasAnimation {

    /**
     * Get the instance of this property. (If <code>true</code> is passed as the parameter,
     * a new instance will be created if not already exists).
     *
     * @param create Whether to create it or not.
     * @return Instance.
     */
    Animation getAnimation(boolean create);

    /**
     * Set it to this instance.
     *
     * @param instance Instance to set.
     */
    void setAnimation(Animation instance);
}
