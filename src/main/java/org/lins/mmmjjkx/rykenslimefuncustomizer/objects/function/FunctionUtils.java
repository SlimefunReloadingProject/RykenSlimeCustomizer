package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.function;

import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

public class FunctionUtils {
    public static final String TYPE_UNDEFINED = "object";
    public static final Object FAILED_INVOKE = null;
    public static final Object VOID_INVOKE = new Object();

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

    public static Object caseType(String sentence, String type, RkFunction function, int line) {
        return switch (type) {
            case "int" -> (int) parseMath(sentence, function, line);
            case "str","string" ->
            case "bool","boolean"-> {
                String[] split = sentence.split(" ");
                if (split.length == 5) {
                    yield parseCondition(split, function.getVariables(), line, function);
                } else if (split.length == 3) {
                    yield (boolean) function.getVariables().get(split[2]);
                }
                yield Boolean.parseBoolean(sentence);
            }
            case "double" -> parseMath(sentence, function, line);
            default -> null;
        };
    }

    private static double parseMath(String sentence, RkFunction function, int line) {
        String[] split = sentence.split(" ");
        Object origin = function.getVariables().get(split[0]);

        if (!checkArgType(origin, "int") || !checkArgType(origin, "double")) {
            ExceptionHandler.handleWarning("在函数 " + function.getName() + "中的第 " + line + "行执行语句里运算的赋值变量不为int或double，自动转为0");
            return 0;
        }

        if (split.length == 3) {
            Object dest = function.getVariables().get(split[2]);
            if (!checkArgType(dest, "int") || !checkArgType(dest, "double")) {
                ExceptionHandler.handleWarning("在函数 " + function.getName() + "中的第 " + line + "行执行语句里运算的赋值变量不为int或double，自动转为0");

                return 0;
            }

            double ori = (double) origin;
            double des = (double) dest;

            //operator
            return switch (split[2]) {
                case "+=" -> ori + des;
                case "-=" -> ori - des;
                case "*=" -> ori * des;
                case "/=" -> ori / des;
                case "%" -> ori % des;
                default -> 0;
            };
        } else if (split.length == 5) {
            String v1 = split[0];
            String operator = split[1];
            String v2 = split[2];
            String operator2 = split[3];
            String v3 = split[4];
        }

        ExceptionHandler.handleWarning("在函数 " + function.getName() + "中的第 " + line + "行执行语句里无法解析该运算，自动转为0");
        return 0;
    }

    private static boolean parseCondition(String[] condition, Map<String, Object> variables, int line, RkFunction function) {
        if (condition == null) {
            return false;
        }

        String name = condition[0];
        String operator = condition[1];
        String type = getArgType(variables.get(name));
        String value = condition[2];

        if (type.equalsIgnoreCase("boolean") || type.equalsIgnoreCase("bool")) {
            return switch (operator) {
                case "==" -> (Boolean) variables.get(name) == Boolean.parseBoolean(value);
                case "!=" -> (Boolean) variables.get(name) != Boolean.parseBoolean(value);
                default -> false;
            };
        }

        String v1 = condition[3];
        String operator2 = condition[4];
        String v2 = condition[5];

        if ((getArgType(v1).equalsIgnoreCase("double") || getArgType(v2).equalsIgnoreCase("double")) ||
                (getArgType(v1).equalsIgnoreCase("int") || getArgType(v2).equalsIgnoreCase("int"))){
            Double d1c = getDouble(v1, variables);
            Double d2c = getDouble(v2, variables);
            if (d1c == null || d2c == null) {
                return false;
            }

            //int, double
            return switch (operator2) {
                case "==" -> Objects.equals(getDouble(v1, variables), getDouble(v2, variables));
                case "!=" -> !Objects.equals(getDouble(v1, variables), getDouble(v2, variables));
                case ">" -> d1c > d2c;
                case ">=" -> d1c >= d2c;
                case "<" -> d1c < d2c;
                case "<=" -> d1c <= d2c;
                default -> false;
            };
        }

        ExceptionHandler.handleWarning("在函数 " + function.getName() + "中的第 " + line + "行执行语句里无法解析该条件，自动转为false");

        return false;
    }

    @Nullable
    private static String getVarType(String var, Map<String, Object> variables) {
        return getArgType(variables.get(var));
    }

    @Nullable
    private static Double getDouble(String input, Map<String, Object> variables) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return (Double) variables.get(input);
        }
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
        } else {
            return TYPE_UNDEFINED;
        }
    }

    public static void handleLineError(RkFunction function, int line) {
        ExceptionHandler.handleError("在函数 " + function.getName() + "中的执行语句在第 " + line + " 行有语法错误");
    }

    public static void handleLineErrorExt(RkFunction function, int line, String ext) {
        ExceptionHandler.handleError("在函数 " + function.getName() + "中的执行语句在第 " + line + " 行发生错误: "+ ext);
    }

    public static void handleArgAmountNotEquals(RkFunction function, Object[] args) {
        ExceptionHandler.handleError("函数 " + function.getName() + " 要求" + function.getInputArgAmount() + "个参数，但这里有" + args.length + "个参数");
    }
}
