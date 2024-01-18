package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.record;

import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record FunctionRecord(String name, List<String> dos, List<String> inputArgs, String outputArg, boolean open) {
    private static Map<String, Object> variables = new HashMap<>();

    public Object doFunc(Object... args) {
        ExceptionHandler.HandleResult result = ExceptionHandler.handleFunctionArgsError(name, inputArgs, Arrays.asList(args));
        if (result == ExceptionHandler.HandleResult.FAILED) {
            return null;
        }
    }
}
