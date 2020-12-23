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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * An implementation of {@link TreeDataProvider}.
 *
 * @author Syam
 */
public class TreeData implements TreeDataProvider {

    private final long id = ID.newID();
    private final String name;
    private final Number value;
    private List<TreeData> children;

    /**
     * Constructor.
     *
     * @param name Name.
     * @param value Value.
     */
    public TreeData(String name, Number value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Number getValue() {
        return value;
    }

    @Override
    public Stream<? extends TreeDataProvider> getChildren() {
        return children == null ? null : children.stream();
    }

    @Override
    public final long getId() {
        return id;
    }

    /**
     * Add children.
     *
     * @param treeData Data to add to children.
     */
    public void add(TreeData... treeData) {
        if(treeData != null) {
            for(TreeData td: treeData) {
                if(td != null) {
                    if (children == null) {
                        children = new ArrayList<>();
                    }
                    children.add(td);
                }
            }
        }
    }

    /**
     * Remove children.
     *
     * @param treeData Data to be removed from children.
     */
    public void remove(TreeData... treeData) {
        if(treeData != null && children != null) {
            for(TreeData td: treeData) {
                if(td != null) {
                    children.remove(td);
                }
            }
        }
    }

    /**
     * Get the data at the given index.
     *
     * @param index Index.
     * @return Data at the given index. Returns <code>null</code> for out of bound indices.
     */
    public TreeData get(int index) {
        return children != null && index >= 0 && index < children.size() ? children.get(index) : null;
    }
}
