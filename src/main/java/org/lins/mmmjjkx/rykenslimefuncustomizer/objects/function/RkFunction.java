package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.intellij.lang.annotations.RegExp;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class RkFunction {
    @Getter
    private String name;
    private List<String> dos;
    private List<String> inputArgs;
    @Getter
    private String outputType;
    @Getter
    private boolean open;

    private static final Map<String, Object> variables = new HashMap<>();
    private static final @RegExp String ADD_VARIABLE_REGEX = "(int|long|str|string|bool|boolean|float|double) \\D\\S* = \\w.*";
    private static final @RegExp String SET_VARIABLE_REGEX = "\\S\\D.* = .*";
    private static final @RegExp String RETURN_REGEX = "return \\S\\D.*";

    public int getInputArgAmount() {
        return inputArgs.size();
    }

    public Object doFunc(Object... args) {
        ExceptionHandler.HandleResult check = ExceptionHandler.handleFunctionArgsError(name, inputArgs, Arrays.asList(args));
        if (check == ExceptionHandler.HandleResult.FAILED) {
            return FunctionUtils.FAILED_INVOKE;
        }
        setupVariables(args);
        int line = 0;
        Object rtn;

        for (String sentence : this.dos) {
            if (sentence.matches(ADD_VARIABLE_REGEX)) {
                boolean result = addVariable(sentence, line);
                if (!result) {
                    FunctionUtils.handleLineError(this, line);
                    return FunctionUtils.FAILED_INVOKE;
                }
            } else if (sentence.matches(RETURN_REGEX)) {
                if (outputType.equalsIgnoreCase("void")) {
                    FunctionUtils.handleLineErrorExt(this, line, "无法在void函数中使用返回语句");
                    return FunctionUtils.FAILED_INVOKE;
                }
                String varName = sentence.replace("return ", "").replace(";", "");
                if (!checkArgumentExists(line, varName)) {
                    return FunctionUtils.FAILED_INVOKE;
                }

                rtn = variables.get(varName);
                if (!FunctionUtils.checkArgType(rtn, outputType)) {
                    FunctionUtils.handleLineErrorExt(this, line, "返回值类型与预期返回的类型不符合");
                    return FunctionUtils.FAILED_INVOKE;
                }
                return rtn;
            } else if (sentence.matches(SET_VARIABLE_REGEX)) {
                String[] split = sentence.split("=");
                if (!checkArgumentExists(line, split[0])) {
                    return FunctionUtils.FAILED_INVOKE;
                }
                Object o = variables.get(split[0]);
                if (!FunctionUtils.checkArgType(o, split[1])) {
                    FunctionUtils.handleLineErrorExt(this, line, "赋值类型与预期赋值的类型不符合");
                    return FunctionUtils.FAILED_INVOKE;
                }

                variables.put(split[0], FunctionUtils.caseType(split[1], FunctionUtils.getArgType(o), this, line));
            } else {
                FunctionUtils.handleLineError(this, line);
                return FunctionUtils.FAILED_INVOKE;
            }
            line++;
        }

        return outputType.equalsIgnoreCase("void") ? FunctionUtils.VOID_INVOKE : FunctionUtils.FAILED_INVOKE;
    }

    /**
     * Only for FunctionUtils. You shouldn't edit anything in the map.
     */
    Map<String, Object> getVariables() {
        return variables;
    }

    private boolean checkArgumentExists(int line, String name) {
        Object o = variables.get(name);
        if (o == null) {
            FunctionUtils.handleLineErrorExt(this, line, "变量 " + name + " 不存在");
            return false;
        }
        return true;
    }

    private boolean addVariable(String sentence, int line) {
        String[] split = sentence.split(" ");
        String[] nameWithValue = split[1].split("=");
        String type = split[0];
        Object o = FunctionUtils.caseType(nameWithValue[1], type, this, line);

        if (o == null) {
            ExceptionHandler.handleError("在函数 " + name + "中，变量 " + nameWithValue[0] + " 的类型不匹配");
            return false;
        }

        variables.put(nameWithValue[0], o);
        return true;
    }

    private void setupVariables(Object... args) {
        for (int i = 0; i < inputArgs.size(); i++) {
            for (String arg : inputArgs) {
                String name = arg.split(":")[1];
                variables.put(name, args[i]);
            }
        }
    }
}
