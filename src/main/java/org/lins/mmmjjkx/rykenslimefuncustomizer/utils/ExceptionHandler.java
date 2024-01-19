package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public static void handleError(String message) {
        logger.error(serializer.deserialize("&4ERROR | " + message));
    }

    public static <T extends Enum<T>> Pair<HandleResult, Enum<T>> handleEnumValueOf(String msg, String nullMsg, Class<T> enumClass, String name) {
        try {
            return new Pair<>(HandleResult.SUCCESS, Enum.valueOf(enumClass, name));
        } catch (NullPointerException e) {
            handleError(nullMsg);
        } catch (IllegalArgumentException e) {
            handleError(msg);
        }
        return new Pair<>(HandleResult.FAILED, null);
    }

    public static <T> Pair<HandleResult, T> handleValueOf(String msg, String nullMsg, Class<T> clazz, String name, String methodName) {
        try {
            Method method = clazz.getMethod(methodName, String.class);
            return new Pair<>(HandleResult.SUCCESS, (T) method.invoke(null, name));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return new Pair<>(HandleResult.FAILED,null);
        } catch (NullPointerException e) {
            handleError(nullMsg);
        } catch (IllegalArgumentException e) {
            handleError(msg);
        }
        return new Pair<>(HandleResult.FAILED, null);
    }

    public enum HandleResult {
        SUCCESS,
        FAILED
    }
}
