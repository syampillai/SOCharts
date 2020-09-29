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
 * Represents orientation property. Certain charts supports this property (Example: {@link TreeChart}.
 *
 * @author Syam
 */
public final class Orientation implements ComponentProperty {

    private String code = null;

    /**
     * Radial orientation.
     */
    public void radial() {
        code = "R";
    }

    /**
     * Right-to-left orientation.
     */
    public void rightToLeft() {
        code = "RL";
    }

    /**
     * Left-to-right orientation.
     */
    public void leftToRight() {
        code = "LR";
    }

    /**
     * Top-to-bottom orientation.
     */
    public void topToBottom() {
        code = "TB";
    }

    /**
     * Bottom-to-top orientation.
     */
    public void bottomToTop() {
        code = "BT";
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        if(code == null) {
            return;
        }
        sb.append("\"layout\":\"");
        if("R".equals(code)) {
            sb.append("radial\",\"label\":{},\"leaves\":{\"label\":{}}");
            return;
        } else {
            sb.append("orthogonal\",\"orient\":\"").append(code).append('"');
        }
        sb.append(",\"label\":{\"position\":\"");
        switch (code) {
            case "TB":
                sb.append("top");
                break;
            case "BT":
                sb.append("bottom");
                break;
            case "LR":
                sb.append("left");
                break;
            case "RL":
                sb.append("right");
                break;
        }
        sb.append("\",\"rotate\":");
        switch (code) {
            case "TB":
                sb.append(-90);
                break;
            case "BT":
                sb.append(90);
                break;
            case "LR":
            case "RL":
                sb.append(0);
                break;
        }
        sb.append(",\"verticalAlign\":\"middle\",\"align\":\"");
        switch (code) {
            case "TB":
            case "BT":
                sb.append("right");
                break;
            case "LR":
                sb.append("right");
                break;
            case "RL":
                sb.append("left");
                break;
        }
        sb.append("\"},\"leaves\":{\"label\":{\"position\":\"");
        switch (code) {
            case "TB":
                sb.append("bottom");
                break;
            case "BT":
                sb.append("top");
                break;
            case "LR":
                sb.append("right");
                break;
            case "RL":
                sb.append("left");
                break;
        }
        sb.append("\",\"rotate\":");
        switch (code) {
            case "TB":
                sb.append(-90);
                break;
            case "BT":
                sb.append(90);
                break;
            case "LR":
            case "RL":
                sb.append(0);
                break;
        }
        sb.append(",\"verticalAlign\":\"middle\",\"align\":\"");
        switch (code) {
            case "TB":
            case "BT":
            case "LR":
                sb.append("left");
                break;
            case "RL":
                sb.append("right");
                break;
        }
        sb.append("\"}}");
    }
}
