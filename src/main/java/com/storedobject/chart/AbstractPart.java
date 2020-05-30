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

/**
 * Represents an abstract {@link ComponentPart} with some common base properties.
 *
 * @author Syam
 */
public abstract class AbstractPart implements ComponentPart {

    private int serial;
    private final long id = SOChart.id.incrementAndGet();

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
            ComponentPart.encode(sb, "name", name);
            sb.append(',');
        }
        sb.append("\"id\":").append(id).append(',');
        Position p = getPosition();
        if(p != null) {
            int len = sb.length();
            p.encodeJSON(sb);
            if(sb.length() != len) {
                sb.append(',');
            }
        }
    }

    /**
     * Get the position of this part. It could be null if not set or not supported. (For some parts
     * it is not logical to define a position or a entirely different positioning mechanism exist).
     *
     * @return Default is <code>null</code>.
     */
    public Position getPosition() {
        return null;
    }

    /**
     * Get the name of this part. It could be null if not set or not supported.
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
}