package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

public class CommonUtils {
    public static final String TYPE_UNDEFINED = "UNDEFINED";

    /**
     * Check the type of the argument.<br>
     * Supported types:
     * <ul>
     *     <li>int</li>
     *     <li>float</li>
     *     <li>double</li>
     *     <li>string(str)</li>
     *     <li>long</li>
     *     <li>boolean(bool)</li>
     * </ul>
     * @param arg the argument
     * @param type the type
     * @return check result
     */
    public static boolean checkArgType(Object arg, String type) {
        String theType = getArgType(arg);
        return theType.equalsIgnoreCase(type);
    }

    /**
     * Get the type of the argument.<br>
     * Supported types:
     * <ul>
     *     <li>int</li>
     *     <li>float</li>
     *     <li>double</li>
     *     <li>string(str)</li>
     *     <li>long</li>
     *     <li>boolean(bool)</li>
     * </ul>
     * @param arg the argument
     * @return arg type
     */
    public static String getArgType(Object arg) {
        if (arg instanceof Integer) {
            return "int";
        } else if (arg instanceof String) {
            return "str";
        } else if (arg instanceof Double) {
            return "double";
        } else if (arg instanceof Boolean) {
            return "bool";
        } else if (arg instanceof Long) {
            return "long";
        } else {
            return arg instanceof Float ? "float" : TYPE_UNDEFINED;
        }
    }
}
