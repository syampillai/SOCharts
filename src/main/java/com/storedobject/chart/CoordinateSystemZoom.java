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
 * Represents "data zoom" component that works inside a coordinate system. Data zoom components allow the end-users
 * to zoom in and to zoom out charts using mouse and/or touch devices.
 *
 * @author Syam
 */
public class CoordinateSystemZoom extends AbstractDataZoom {

    private boolean enabled = true;
    private Object zoomOnMouseWheel, moveOnMouseWheel, moveOnMouseMove;

    /**
     * Constructor.
     *
     * @param coordinateSystem Coordinate system.
     * @param axes Axis list.
     */
    public CoordinateSystemZoom(CoordinateSystem coordinateSystem, Axis... axes) {
        super("inside", coordinateSystem, axes);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        sb.append(",\"disabled\":").append(!enabled);
        if(zoomOnMouseWheel != null) {
            ComponentPart.encode(sb, "zoomOnMouseWheel", zoomOnMouseWheel);
            zoomOnMouseWheel = null;
        }
        if(moveOnMouseWheel != null) {
            ComponentPart.encode(sb, "moveOnMouseWheel", moveOnMouseWheel);
            moveOnMouseWheel = null;
        }
        if(moveOnMouseMove != null) {
            ComponentPart.encode(sb, "moveOnMouseMove", moveOnMouseMove);
            moveOnMouseMove = null;
        }
    }

    /**
     * Enable the zooming feature.
     */
    public void enable() {
        enabled = true;
    }

    /**
     * Disable the zooming feature.
     */
    public void disable() {
        enabled = false;
    }

    /**
     * Whether to trigger the zoom on mouse wheel or not.
     *
     * @param zoomOnMouseWheel True or false.
     */
    public void zoomOnMouseWheel(boolean zoomOnMouseWheel) {
        this.zoomOnMouseWheel = zoomOnMouseWheel;
    }

    /**
     * Switch on the zooming feature when the mouse wheel is moved with a special key combination.
     *
     * @param key Special key to press while moving the mouse wheel.
     */
    public void zoomOnMouseWheel(Key key) {
        this.zoomOnMouseWheel = key;
    }

    /**
     * Whether to trigger the data window move on mouse wheel or not.
     *
     * @param moveOnMouseWheel True or false.
     */
    public void moveOnMouseWheel(boolean moveOnMouseWheel) {
        this.moveOnMouseWheel = moveOnMouseWheel;
    }

    /**
     * Switch on the data window moving feature when the mouse wheel is moved with a special key combination.
     *
     * @param key Special key to press while moving the mouse wheel.
     */
    public void moveOnMouseWheel(Key key) {
        this.moveOnMouseWheel = key;
    }

    /**
     * Whether to trigger the data window move on mouse move or not.
     *
     * @param moveOnMouseMove True or false.
     */
    public void moveOnMouseMove(boolean moveOnMouseMove) {
        this.moveOnMouseMove = moveOnMouseMove;
    }

    /**
     * Switch on the data window moving feature when the mouse is moved with a special key combination.
     *
     * @param key Special key to press while moving the mouse.
     */
    public void moveOnMouseMove(Key key) {
        this.moveOnMouseMove = key;
    }
}
