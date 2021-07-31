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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Representation of tree-type data.
 *
 * @author Syam
 */
public interface TreeDataProvider extends ComponentPart {

    /**
     * Get data value.
     *
     * @return Value.
     */
    Number getValue();

    /**
     * Get the child nodes.
     *
     * @return Child nodes as a stream.
     */
    Stream<? extends TreeDataProvider> getChildren();

    @Override
    default void encodeJSON(StringBuilder sb) {
        String name = getName();
        if(name == null) {
            name = "Name?";
        }
        sb.append("{\"name\":").append(ComponentPart.escape(name));
        Number value = getValue();
        if(value != null) {
            sb.append(",\"value\":").append(value);
        }
        Label label = getLabel(false);
        if(label != null) {
            label.encodeJSON(sb);
        }
        ItemStyle itemStyle = getItemStyle(false);
        if(itemStyle != null) {
            itemStyle.encodeJSON(sb);
        }
        encode(sb, getChildren());
        sb.append('}');
    }

    private void encode(StringBuilder sb, Stream<? extends TreeDataProvider> children) {
        if(children == null) {
            return;
        }
        AtomicBoolean first = new AtomicBoolean(true);
        children.forEach(d -> {
            if(first.get()) {
                first.set(false);
                sb.append(",\"children\":[");
            } else {
                sb.append(',');
            }
            d.encodeJSON(sb);
        });
        if(!first.get()) {
            sb.append(']');
        }
    }

    @Override
    default void validate() {
    }

    /**
     * Get the label for this tree node.
     *
     * @param create Whether to create it if not exists.
     * @return Return the label.
     */
    default Label getLabel(boolean create) {
        return null;
    }

    /**
     * Get item style.
     *
     * @param create If passed true, a new style is created if not exists.
     * @return Item style.
     */
    default ItemStyle getItemStyle(boolean create) {
        return null;
    }

    class Label extends TextStyle {

        private boolean show = true;

        /**
         * Show labels.
         */
        public void show() {
            show = true;
        }

        /**
         * Hide labels.
         */
        public void hide() {
            show = false;
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            super.encodeJSON(sb);
            ComponentPart.addComma(sb);
            sb.append("\"show\":").append(show);
        }
    }
}
