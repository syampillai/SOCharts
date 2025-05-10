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

import com.storedobject.helper.ID;

import java.util.Objects;

/**
 * Represents an abstract {@link ComponentPart} with some common base properties.
 *
 * @author Syam
 */
public abstract class AbstractPart implements ComponentPart {

    private int serial;
    private final long id = ID.newID();
    private int z = -1;

    /**
     * Get a unique Id for this part.
     *
     * @return Unique Id.
     */
    @Override
    public final long getId() {
        return id;
    }

    /**
     * Whether Id needs to be sent as part of the JSON or not.
     *
     * @return True/false.
     */
    protected boolean hasId() {
        return true;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        ComponentPart.encode(sb, "name", getName());
        if(hasId() && !(this instanceof Wrapped)) {
            ComponentPart.encode(sb, "id", "" + getId());
        }
        if(this instanceof HasPosition h) {
            ComponentPart.encode(sb, null, h.getPosition(false));
        }
        if(this instanceof HasPadding h) {
            ComponentPart.encode(sb, null, h.getPadding(false));
        }
        if(this instanceof HasPolarProperty h) {
            ComponentPart.encode(sb, null, h.getPolarProperty(false));
        }
        if(this instanceof HasAnimation h) {
            ComponentPart.encode(sb, null, h.getAnimation(false));
        }
        if(this instanceof HasEmphasis h) {
            ComponentPart.encode(sb, "emphasis", h.getEmphasis(false));
        }
        if(this instanceof HasLabel h) {
            ComponentPart.encode(sb, getLabelTag(), h.getLabel(false));
        }
        if(this instanceof HasItemStyle h) {
            ComponentPart.encode(sb, "itemStyle", h.getItemStyle(false));
        }
        if(z >= 0) {
            ComponentPart.encode(sb, "z", z);
        }
    }

    protected String getLabelTag() {
        return "label";
    }

    /**
     * Get the name of this part. It could be null if not set or not supported.
     *
     * @return Default is <code>null</code>.
     */
    public String getName() {
        return null;
    }

    @Override
    public final int getSerial() {
        return serial;
    }

    @Override
    public final void setSerial(int serial) {
        this.serial = serial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof AbstractPart ap)) {
            return false;
        }
        return id == ap.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Get the Z (determines the overlap order when displayed on the screen) of this.
     *
     * @return Z value.
     */
    public final int getZ() {
        return z;
    }

    /**
     * Set the Z (determines the overlap order when displayed on the screen) of this.
     *
     * @param z Z value to set.
     */
    public final void setZ(int z) {
        this.z = z;
    }
}