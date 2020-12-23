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
        String name = getName();
        if(name != null) {
            ComponentPart.addComma(sb);
            ComponentPart.encode(sb, "name", name);
            sb.append(',');
        }
        sb.append("\"id\":").append(id);
        if(this instanceof HasPosition) {
            ComponentPart.encodeProperty(sb, ((HasPosition)this).getPosition(false));
        }
        if(this instanceof HasPadding) {
            ComponentPart.encodeProperty(sb, ((HasPadding)this).getPadding(false));
        }
        if(this instanceof HasPolarProperty) {
            ComponentPart.encodeProperty(sb, ((HasPolarProperty) this).getPolarProperty(false));
        }
        sb.append(',');
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
}