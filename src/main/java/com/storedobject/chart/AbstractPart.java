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
    public final long getId() {
        return id;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        ComponentPart.encode(sb, "name", getName());
        ComponentPart.encode(sb, "id", id);
        if(this instanceof HasPosition) {
            ComponentPart.encode(sb, null, ((HasPosition)this).getPosition(false));
        }
        if(this instanceof HasPadding) {
            ComponentPart.encode(sb, null, ((HasPadding)this).getPadding(false));
        }
        if(this instanceof HasPolarProperty) {
            ComponentPart.encode(sb, null, ((HasPolarProperty) this).getPolarProperty(false));
        }
        if(this instanceof HasAnimation) {
            ComponentPart.encode(sb, null, ((HasAnimation)this).getAnimation(false));
        }
        if(this instanceof HasEmphasis) {
            ComponentPart.encode(sb, "emphasis", ((HasEmphasis)this).getEmphasis(false));
        }
        if(z >= 0) {
            ComponentPart.encode(sb, "z", z);
        }
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
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPart that = (AbstractPart) o;
        return id == that.id;
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