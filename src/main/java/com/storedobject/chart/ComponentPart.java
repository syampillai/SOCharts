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

import com.storedobject.helper.ID;

import java.util.Arrays;

/**
 * Represents a part used by chart {@link Component}s.
 *
 * @author Syam
 */
public interface ComponentPart extends ComponentProperty {

    /**
     * Set the rendering index of this part. Rendering index is the position of this part in on the chart when it is
     * being rendered. It is up to the part to keep this value if required.
     *
     * @param index Rendering index.
     */
    default void setRenderingIndex(int index) {
    }

    /**
     * Return the rendering index of this part. The default implementation returns -1.
     *
     * @return Rendering index.
     */
    default int getRenderingIndex() {
        return -1;
    }

    /**
     * Each part should have a unique Id. (It can be a final variable and can be set by
     * calling {@link ID#newID()}.
     *
     * @return Unique Id.
     */
    long getId();

    /**
     * Set a serial number (Serial number used internal purposes only). The implementation of this method must be
     * in such a way that the serial number set here must be returned by the {@link #getSerial()} method.
     *
     * @param serial Serial number to set.
     */
    void setSerial(int serial);

    /**
     * Get the current serial number (Serial number used internal purposes only). The serial number set by the
     * {@link #setSerial(int)} method should be returned by this method.
     *
     * @return Current serial number.
     */
    int getSerial();

    /**
     * Helper method: Encode a (name, value) pair.
     *
     * @param sb Encoded JSON string to be appended to this.
     * @param name Name to be encoded.
     * @param value Value to be encoded.
     */
    static void encode(StringBuilder sb, String name, Object value) {
        if(value == null && name == null) {
            return;
        }
        if(value instanceof ComponentProperty) {
            addComma(sb);
            if(name != null) {
                sb.append('"').append(name).append("\":{");
            }
            ((ComponentProperty)value).encodeJSON(sb);
            if(name != null) {
                sb.append('}');
            }
            return;
        }
        if(name != null && value != null) {
            addComma(sb);
            sb.append('"').append(name).append("\":").append(escape(value));
        }
    }

    /**
     * Helper method: Add a comma if required.
     *
     * @param sb Add a comma to this.
     */
    static void addComma(StringBuilder sb) {
        int len = sb.length();
        if(len == 0) {
            return;
        }
        char c;
        while(len > 0) {
            --len;
            c = sb.charAt(len);
            if(c == ' ' || c == '\n') {
                continue;
            }
            if(c == '{' || c == '[' || c == ',') {
                break;
            }
            sb.append(',');
            break;
        }
    }

    /**
     * Helper method: Encode a generic function
     * @param sb Encoded JSON string to be appended to this.
     * @param body Body of the function.
     * @param name Name to be encoded (if null, name-part will not be encoded)
     * @param params Parameters of the function.
     */
    static void encodeFunction(StringBuilder sb, String name, String body, String... params) {
        if(name != null) {
            addComma(sb);
            sb.append('"').append(name).append("\":");
        }
        sb.append("{\"function\":{").append("\"params\":[");
        if(params != null && params.length > 0) {
            boolean first = true;
            for(String p: params) {
                if(first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append('"').append(p).append('"');
            }
        }
        if(!body.endsWith(";")) {
            body += ';';
        }
        sb.append("],\"body\":").append(escape(body)).append("}}");
    }

    /**
     * Helper method: Encode a generic function as a string.
     * @param body Body of the function.
     * @param params Parameters.
     * @return Encoded value.
     */
    static String encodeFunction(String body, String... params) {
        StringBuilder s = new StringBuilder("{\"function\":{\"params\":\"");
        for(int i = 0; i < params.length; i++) {
            if(i > 0) {
                s.append(',');
            }
            s.append(params[i]);
        }
        s.append("\",\"body\":").append(escape(body)).append("}}");
        return s.toString();
    }

    /**
     * Helper method: Replace trailing commas with spaces.
     *
     * @param sb Replace trailing commas in this.
     */
    static void removeComma(StringBuilder sb) {
        int len = sb.length() - 1;
        while (len >= 0 && sb.charAt(len) == ',') {
            sb.setCharAt(len, ' ');
            --len;
        }
    }

    /**
     * This method is invoked by {@link SOChart} to check if the component or part is valid or not.
     *
     * @throws ChartException Raises exception if the component or part is not valid.
     */
    void validate() throws ChartException;

    /**
     * Helper method to return the class name of the component/part in a more human-friendly way.
     *
     * @return Name of the class that can be used for showing a message to the end users.
     */
    default String className() {
        String name = getName();
        return className(getClass()) + (name == null ? "" : (" (" + name + ")"));
    }

    /**
     * Get the name of this part.
     *
     * @return Name
     */
    default String getName() {
        return null;
    }

    /**
     * Set a name for this part.
     *
     * @param name Name to set.
     */
    default void setName(String name) {
    }

    /**
     * Helper method to return the class name of a given class in a more human-friendly way.
     *
     * @param anyClass Any class.
     * @return Name of the class that can be used for showing a message to the end users.
     */
    static String className(Class<?> anyClass) {
        String cName = anyClass.getName();
        return Chart.name(cName.substring(cName.lastIndexOf('.') + 1)).replace('$', '/');
    }

    /**
     * Helper method to escape invalid characters in JSON strings. Please note that this method returns a
     * double-quoted string unless the parameter is a number.
     * For example, escape("Hello") will return "Hello" not Hello.
     *
     * @param any Anything to encode.
     * @return Encoded string.
     */
    static String escape(Object any) {
        String string = any == null ? "" : any.toString();
        if(string == null) {
            string = "";
        }
        if(any instanceof Number || any instanceof Boolean) {
            return any.toString();
        }
        if(string.startsWith("\"") && string.endsWith("\"")) {
            return string; // Special case - already encoded.
        }
        if(string.contains("\"")) {
            string = string.replace("\"", "\\\"");
        }
        if(string.contains("\n")) {
            string = string.replace("\n", "\\n");
        }
        return '"' + string + '"';
    }
}
