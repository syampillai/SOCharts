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

import java.util.stream.Stream;

/**
 * Tree chart.
 *
 * @author Syam
 */
public class TreeChart extends Chart implements HasPosition {

    private TreeDataProvider data;
    private Position position;
    private Orientation orientation;
    private final TD td;

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
        super(ChartType.Tree);
        this.data = data;
        super.setData(td = new TD());
        getOrientation(true).radial();
    }

    @Override
    public void setData(AbstractDataProvider<?>... data) {
    }

    /**
     * Get the data associated with this chart.
     *
     * @return Data provider.
     */
    public TreeDataProvider getTreeData() {
        return data;
    }

    /**
     * Set data to the chart.
     *
     * @param data Data provider to set.
     */
    public void setTreeData(TreeDataProvider data) {
        this.data = data;
    }

    @Override
    public void validate() throws ChartException {
        super.validate();
        if(data == null) {
            throw new ChartException("No data provided for " + className());
        }
    }

    @Override
    protected AbstractDataProvider<?> dataToEmbed() {
        return td;
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.addComma(sb);
        ComponentPart.encode(sb, "expandAndCollapse", true);
        ComponentPart.encode(sb, null, orientation);
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

    private class TD extends BasicDataProvider<Object> {

        @Override
        public Stream<Object> stream() {
            return Stream.of(data);
        }

        @Override
        public void encode(StringBuilder sb, Object value) {
            ((TreeDataProvider)value).encodeJSON(sb);
        }
    }
}
