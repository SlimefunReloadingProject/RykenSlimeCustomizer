package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    public static void handleWarning(String message){
        logger.warn(serializer.deserialize("&eWARNING | " + message));
    }

    public static void handleError(String message) {
        logger.error(serializer.deserialize("&4ERROR | " + message));
    }

    public static <T extends Enum<T>> Pair<HandleResult, T> handleEnumValueOf(String msg, String nullMsg, Class<T> enumClass, String name) {
        try {
            return new Pair<>(HandleResult.SUCCESS, Enum.valueOf(enumClass, name));
        } catch (NullPointerException e) {
            handleError(nullMsg);
        } catch (IllegalArgumentException e) {
            handleError(msg);
        }
        return new Pair<>(HandleResult.FAILED, null);
    }

    public static HandleResult handleItemGroupAddItem(ProjectAddon addon, String igid, SlimefunItem item) {

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
