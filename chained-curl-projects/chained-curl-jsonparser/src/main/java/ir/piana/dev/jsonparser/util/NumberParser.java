package ir.piana.dev.jsonparser.util;

public class NumberParser {

    public static Number parse(Number target, String numStr) {
        if (target instanceof Short)
            return Short.valueOf(numStr);
        else if (target instanceof Integer)
            return Integer.valueOf(numStr);
        if (target instanceof Long)
            return Long.valueOf(numStr);
        if (target instanceof Float)
            return Float.valueOf(numStr);
        if (target instanceof Double)
            return Double.valueOf(numStr);
        throw new RuntimeException("The target type is not numeric");
    }

    public static Number parseUp(Number target, String numStr) {
        if (target instanceof Short || target instanceof Integer || target instanceof Long)
            return Long.valueOf(numStr.replaceAll("_", ""));
        if (target instanceof Float || target instanceof Double)
            return Double.valueOf(numStr.replaceAll("_", ""));
        throw new RuntimeException("The target type is not numeric");
    }

    public static Number parseUp(String numStr) {
        try {
            if (numStr.contains("."))
                return Double.valueOf(numStr.replaceAll("_", ""));
            return Long.valueOf(numStr.replaceAll("_", ""));
        } catch (NumberFormatException e) {
            throw new RuntimeException("The target type is not numeric");
        }
    }

    public static Boolean parseBoolean(String bool) {
        return bool.equalsIgnoreCase("1") || bool.equalsIgnoreCase("true");
    }

    public static boolean equalTo(Number target, String numStr) {
        if (target instanceof Short || target instanceof Integer || target instanceof Long)
            return target.longValue() == Long.parseLong(numStr.replaceAll("_", ""));
        if (target instanceof Float || target instanceof Double)
            return target.longValue() == Double.parseDouble(numStr.replaceAll("_", ""));
        throw new RuntimeException("The target type is not numeric");
    }

    public static boolean greeterOrEqual(Number target, String numStr) {
        if (target instanceof Short || target instanceof Integer || target instanceof Long)
            return target.longValue() >= Long.parseLong(numStr.replaceAll("_", ""));
        if (target instanceof Float || target instanceof Double)
            return target.longValue() >= Double.parseDouble(numStr.replaceAll("_", ""));
        throw new RuntimeException("The target type is not numeric");
    }

    public static boolean greeter(Number target, String numStr) {
        if (target instanceof Short || target instanceof Integer || target instanceof Long)
            return target.longValue() > Long.valueOf(numStr.replaceAll("_", "")).longValue();
        if (target instanceof Float || target instanceof Double)
            return target.longValue() > Double.valueOf(numStr.replaceAll("_", "")).doubleValue();
        throw new RuntimeException("The target type is not numeric");
    }

    public static boolean lesserOrEqual(Number target, String numStr) {
        if (target instanceof Short || target instanceof Integer || target instanceof Long)
            return target.longValue() <= Long.parseLong(numStr.replaceAll("_", ""));
        if (target instanceof Float || target instanceof Double)
            return target.longValue() <= Double.parseDouble(numStr.replaceAll("_", ""));
        throw new RuntimeException("The target type is not numeric");
    }

    public static boolean lesser(Number target, String numStr) {
        if (target instanceof Short || target instanceof Integer || target instanceof Long)
            return target.longValue() < Long.parseLong(numStr.replaceAll("_", ""));
        if (target instanceof Float || target instanceof Double)
            return target.longValue() < Double.parseDouble(numStr.replaceAll("_", ""));
        throw new RuntimeException("The target type is not numeric");
    }
}
