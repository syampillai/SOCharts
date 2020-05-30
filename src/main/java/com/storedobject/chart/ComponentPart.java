package com.storedobject.chart;

/**
 * Represents a part used by chart {@link Component}s.
 *
 * @author Syam
 */
public interface ComponentPart {

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
     * Encode the JSON string for this part.
     *
     * @param sb Encoded JSON string to be appended to this.
     */
    void encodeJSON(StringBuilder sb);

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
        return Chart.name(cName.substring(cName.lastIndexOf('.') + 1));
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
        if(any instanceof Number) {
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
