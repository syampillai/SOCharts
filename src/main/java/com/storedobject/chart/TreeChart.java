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
 * Tree chart.
 *
 * @author Syam
 */
public class TreeChart extends AbstractDataChart implements HasPosition {

    private TreeDataProvider data;
    private Position position;
    private Orientation orientation;

    /**
     * Create a tree chart. Data can be set later.
     */
    public TreeChart() {
        this(null);
    }

    /**
     * Create a tree chart of the set of data.
     *
     * @param data Data to be used.
     */
    public TreeChart(TreeDataProvider data) {
        super(Type.Tree);
        getOrientation(true).radial();
    }

    public TreeDataProvider getTreeData() {
        return data;
    }

    public void setTreeData(TreeDataProvider data) {
        this.data = data;
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        if(data == null) {
            throw new Exception("No data provided for " + className());
        }
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        if(!skippingData) {
            sb.append(",\"data\":[");
            data.encodeJSON(sb);
            sb.append(']');
        }
        ComponentPart.addComma(sb);
        ComponentPart.encode(sb, "expandAndCollapse", true);
        ComponentPart.encodeProperty(sb, orientation);
    }

    @Override
    public final Position getPosition(boolean create) {
        if(position == null && create) {
            position = new Position();
        }
        return position;
    }

    @Override
    public final void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Get orientation.
     *
     * @param create If passed <code>true</code>, a new orientation is created.
     * @return Orientation.
     */
    public final Orientation getOrientation(boolean create) {
        if(orientation == null && create) {
            orientation = new Orientation();
        }
        return orientation;
    }

    /**
     * Set orientation.
     *
     * @param orientation Orientation to set.
     */
    public final void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
}
