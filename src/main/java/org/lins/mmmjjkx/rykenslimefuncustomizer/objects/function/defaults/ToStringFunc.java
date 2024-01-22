package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.function.defaults;

import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.function.FunctionUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.function.RkFunction;

import java.util.List;

public class ToStringFunc extends RkFunction {
    public ToStringFunc() {
        super("toString", List.of(), List.of("object:o"), "string", true);
    }

    @Override
    public Object doFunc(Object... args) {
        if (args.length == 1) {
            return args[0].toString();
        }
        FunctionUtils.handleArgAmountNotEquals(this, args);
        return FunctionUtils.FAILED_INVOKE;
    }
}
