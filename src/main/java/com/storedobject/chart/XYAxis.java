/*
 *  Copyright 2019-2021 Syam Pillai
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
 * Representation of XY axis (base for both {@link XAxis} and {@link YAxis}).
 *
 * @author Syam
 */
public abstract class XYAxis extends Axis {

    private boolean opposite = false;
    private int offset = 0;

    /**
     * Constructor.
     *
     * @param dataType Data type.
     */
    public XYAxis(DataType dataType) {
        super(dataType);
    }

    /**
     * Constructor.
     *
     * @param data Data type will be determined from the data provider. Note: It is not required to use
     *             same the data on this axis.
     */
    public XYAxis(AbstractDataProvider<?> data) {
        super(data);
    }

    abstract String positionString();

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(opposite) {
            sb.append(',');
            ComponentPart.encode(sb, "position", positionString());
        }
        int offset = getOffset();
        if(offset > 0) {
            sb.append(',');
            ComponentPart.encode(sb, "offset", offset);
        }
    }

    /**
     * Is this displayed on the opposite side (For example, an X axis is normally displayed at the bottom but
     * we can also display it at the top).
     *
     * @return True or false.
     */
    public final boolean isOpposite() {
        return opposite;
    }

    /**
     * Set the attribute to make it displayed on the opposite side.
     *
     * @param opposite True or false.
     */
    public void setOpposite(boolean opposite) {
        this.opposite = opposite;
    }

    /**
     * Get the offset of the axis. Normally, the offset is zero and if non-zero, the display will be shifted from
     * the normal position by that many pixels. This is useful if more than one axis are displayed together.
     *
     * @return Offset value.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Set an offset value so that the axis will be displayed at an offset from the normal position.
     *
     * @param offset Offset value in pixels.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
