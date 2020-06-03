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
 * Represents a part used by chart {@link Component}s.
 *
 * @author Syam
 */
public interface ComponentPart extends ComponentProperty {

    /**
     * Each part should have a unique Id. (It can be a final variable and can be set by
     * incrementing {@link SOChart#id}).
     *
     * @return Unique Id.
     */
    long getId();

    /**
     * Set a serial number (Serial number used internal purposes only).
     *
     * @param serial Serial number to set.
     */
    default void setSerial(int serial) {
    }

    /**
     * Get the current serial number (Serial number used internal purposes only).
     *
     * @return Current serial number.
     */
    default int getSerial() {
        return -1;
    }

    /**
     * Helper method: Encode a (name, value) pair.
     *
     * @param sb Encoded JSON string to be appended to this.
     * @param name Name to be encoded.
     * @param value Value to be encoded.
     */
    static void encode(StringBuilder sb, String name, Object value) {
        sb.append('"').append(name).append("\":").append(escape(value));
    }

    /**
     * Helper method: Encode a {@link ComponentProperty}.
     *
     * @param sb Encoded JSON string to be appended to this.
     * @param componentProperty Component property (could be <code>null</code>).
     */
    static void encodeProperty(StringBuilder sb, ComponentProperty componentProperty) {
        if(componentProperty != null) {
            addComma(sb);
            componentProperty.encodeJSON(sb);
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
     * @throws Exception Raises exception if the component or part is not valid.
     */
    void validate() throws Exception;

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
        if(string.contains("\"")) {
            string = string.replace("\"", "\\\"");
        }
        if(string.contains("\n")) {
            string = string.replace("\n", "\\n");
        }
        return '"' + string + '"';
    }
}
