package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.RecipeTypeMap;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;

import java.lang.reflect.Field;

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

    public static HandleResult handleMenuConflict(String id, ProjectAddon addon) {
        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(id));
        if (menu != null) {
            logger.error(serializer.deserialize("&4ERROR | 菜单ID冲突：已存在菜单ID为"+id+"的菜单"));
            return HandleResult.FAILED;
        }
        return HandleResult.SUCCESS;
    }

    public static HandleResult handleGroupIdConflict(String id) {
        ItemGroup ig = CommonUtils.getIf(Slimefun.getRegistry().getAllItemGroups(), i -> i.getKey().getKey().equalsIgnoreCase(id));
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
        if (message == null || message.isBlank()) return;

        logger.warn(serializer.deserialize("&eWARNING | " + message));
    }

    public static void handleError(String message) {
        if (message == null || message.isBlank()) return;

        logger.error(serializer.deserialize("&4ERROR | " + message));
    }

    /**
     * 检测后门等
     * @param message the message
     */
    public static void handleDanger(String message) {
        if (message == null || message.isBlank()) return;

        logger.error(serializer.deserialize("&c&u&l&bD&4&lA&c&lN&b&lG&4&lE&c&lR | " + message));
    }

    public static <T extends Enum<T>> Pair<HandleResult, T> handleEnumValueOf(String msg, Class<T> enumClass, String name) {
        try {
            return new Pair<>(HandleResult.SUCCESS, Enum.valueOf(enumClass, name.toUpperCase()));
        } catch (NullPointerException | IllegalArgumentException e) {
            handleError(msg);
        }
        return new Pair<>(HandleResult.FAILED, null);
    }

    public static Pair<HandleResult, ItemGroup> handleItemGroupGet(ProjectAddon addon, String id) {
        ItemGroup ig = CommonUtils.getIf(addon.getItemGroups(), i -> i.getKey().getKey().equalsIgnoreCase(id));
        if (ig == null) {
            ItemGroup ig2 = CommonUtils.getIf(Slimefun.getRegistry().getAllItemGroups(), i -> i.getKey().getKey().equalsIgnoreCase(id));
            if (ig2 == null) {
                handleError("无法在附属" + addon.getAddonName() + "中找不到该物品组 " + id);
                return new Pair<>(HandleResult.FAILED, null);
            }
            return new Pair<>(HandleResult.SUCCESS, ig2);
        }
        return new Pair<>(HandleResult.SUCCESS, ig);
    }

    public static Pair<HandleResult, RecipeType> getRecipeType(String msg, String fieldName) {
        try {
            Field field = RecipeType.class.getDeclaredField(fieldName);
            return new Pair<>(HandleResult.SUCCESS, (RecipeType) field.get(null));
        } catch (NoSuchFieldException e) {
            RecipeType recipeType = RecipeTypeMap.getRecipeType(fieldName);
            if (recipeType == null) {
                handleError(msg);
                return new Pair<>(HandleResult.FAILED, null);
            }
            return new Pair<>(HandleResult.SUCCESS, recipeType);
        } catch (IllegalAccessException ignored) {
            //it doesn't happen
        }
        return new Pair<>(HandleResult.FAILED, null);
    }

    public enum HandleResult {
        SUCCESS,
        FAILED
    }
}
