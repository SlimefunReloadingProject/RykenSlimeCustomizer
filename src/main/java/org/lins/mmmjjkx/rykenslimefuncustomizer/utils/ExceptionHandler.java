package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.lang.reflect.Field;
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

    public static HandleResult handleGroupIdConflict(String id) {
        ItemGroup ig = CommonUtils.getIf(Slimefun.getRegistry().getAllItemGroups(), i -> i.getKey().getKey().equals(id));
        if (ig != null) {
            if (ig.getAddon() != null) {
                logger.error(serializer.deserialize("&4ERROR | ID冲突：" + id + "与物品组 " + ig.getKey().getKey() + "(来自"+ig.getAddon().getName()+")冲突"));
                return HandleResult.FAILED;
            }
            logger.error(serializer.deserialize("&4ERROR | ID冲突：" + id + "与物品组 " + ig.getKey().getKey() + "冲突"));
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
            return new Pair<>(HandleResult.SUCCESS, Enum.valueOf(enumClass, name.toUpperCase()));
        } catch (NullPointerException e) {
            handleError(nullMsg);
        } catch (IllegalArgumentException e) {
            handleError(msg);
        }
        return new Pair<>(HandleResult.FAILED, null);
    }

    public static HandleResult handleItemGroupAddItem(ProjectAddon addon, String igid, SlimefunItem item) {
        Pair<HandleResult, ItemGroup> result = handleItemGroupGet(addon, igid);
        if (result.getFirstValue() == HandleResult.FAILED) return HandleResult.FAILED;
        ItemGroup ig = result.getSecondValue();
        try {
            if (ig != null) {
                ig.add(item);
            }
            return HandleResult.SUCCESS;
        } catch (UnsupportedOperationException e) {
            handleError("父物品组"+igid+"不能添加物品，只能添加子物品组！");
            return HandleResult.FAILED;
        }
    }

    public static Pair<HandleResult, ItemGroup> handleItemGroupGet(ProjectAddon addon, String id) {
        ItemGroup ig = addon.getItemGroup(id);
        if (ig == null) {
            handleError("无法在附属"+addon.getAddonName()+"中找不到该物品组 " + id);
            return new Pair<>(HandleResult.FAILED, null);
        }
        return new Pair<>(HandleResult.SUCCESS, ig);
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

    public static <T> Pair<HandleResult, T> handleField(String msg, String nullMsg, Class<T> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return new Pair<>(HandleResult.SUCCESS, (T) field.get(null));
        } catch (NoSuchFieldException e) {
            handleError(nullMsg);
        } catch (IllegalAccessException e) {
            handleError(msg);
        }
        return new Pair<>(HandleResult.FAILED, null);
    }

    public enum HandleResult {
        SUCCESS,
        FAILED
    }
}
