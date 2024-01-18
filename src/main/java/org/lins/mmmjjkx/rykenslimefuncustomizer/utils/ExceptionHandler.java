package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

import java.util.List;

public class ExceptionHandler {
    private static final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
    private static final @NotNull ComponentLogger logger = RykenSlimefunCustomizer.INSTANCE.getComponentLogger();

    public static HandleResult handleIdConflict(String id) {
        SlimefunItem i = SlimefunItem.getById(id);
        if (i != null) {
            logger.error(serializer.deserialize("&4ERROR | ID冲突：" + id + "与" + i.getAddon().getName() + "中的物品ID冲突"));
            return HandleResult.FAILED;
        }
        return HandleResult.SUCCESS;
    }

    public static HandleResult handleFunctionArgsError(String name, List<String> requiredInputs, List<Object> args) {
        List<String> types_required = requiredInputs.stream().map(s -> s.split(":")[1]).toList();
        List<String> types_args = args.stream().map(o -> CommonUtils.getArgType(args)).toList();

        if (types_args.equals(types_required)) {
            return HandleResult.SUCCESS;
        }

        logger.error(serializer.deserialize("&4ERROR | 函数参数类型错误：" + name + "需要类型为" + types_required + "的参数，但实际输入了" + types_args + "这些类型的参数"));
        return HandleResult.FAILED;
    }

    public enum HandleResult {
        SUCCESS,
        FAILED
    }
}
