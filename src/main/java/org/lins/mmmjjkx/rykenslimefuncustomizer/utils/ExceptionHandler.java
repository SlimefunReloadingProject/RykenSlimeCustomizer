package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.Colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.RecipeTypeMap;

public class ExceptionHandler {
    private static final @NotNull ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static HandleResult handleIdConflict(String id) {
        SlimefunItem i = SlimefunItem.getById(id);
        if (i != null) {
            console.sendMessage(CMIChatColor.translate(
                    "&4ERROR | ID冲突：" + id + "与" + i.getAddon().getName() + "中的物品发生了ID冲突"));
            return HandleResult.FAILED;
        }
        return HandleResult.SUCCESS;
    }

    public static HandleResult handleMenuConflict(String id, ProjectAddon addon) {
        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(id));
        if (menu != null) {
            console.sendMessage(CMIChatColor.translate("&4ERROR | ID冲突：已存在菜单ID为" + id + "的菜单"));
            return HandleResult.FAILED;
        }
        return HandleResult.SUCCESS;
    }

    public static HandleResult handleGroupIdConflict(String id) {
        List<ItemGroup> allGroups = Slimefun.getRegistry().getAllItemGroups();
        ItemGroup ig = CommonUtils.getIf(
                allGroups,
                i -> i.getKey().getKey().equalsIgnoreCase(id));
        if (ig != null) {
            for (ItemGroup itemGroup : allGroups) {
                if (itemGroup.getKey().getNamespace().equals("rykenslimecustomizer") && itemGroup.getKey().getKey().equals(id)) {
                    console.sendMessage(CMIChatColor.translate("&4ERROR | ID冲突：" + id + "与物品组 "
                            + ig.getKey().getKey() + "发生ID冲突"));
                    return HandleResult.FAILED;
                }
            }
        }
        return HandleResult.SUCCESS;
    }

    public static void handleWarning(String message) {
        if (message == null || message.isBlank()) return;

        console.sendMessage(CMIChatColor.translate("&eWARNING | " + message));
    }

    public static void handleError(String message) {
        if (message == null || message.isBlank()) return;

        console.sendMessage(CMIChatColor.translate("&4ERROR | " + message));
    }

    public static void handleError(String message, Throwable e) {
        if (message == null || message.isBlank()) return;

        if (e != null) {
            console.sendMessage(CMIChatColor.translate("&4ERROR | " + message));
            e.printStackTrace();
        } else {
            handleError(message);
        }
    }

    public static void debugLog(String message) {
        if (RykenSlimefunCustomizer.INSTANCE.getConfig().getBoolean("debug")) {
            if (message == null || message.isBlank()) return;

            console.sendMessage(CMIChatColor.translate("&6DEBUG | " + message));
        }
    }

    /**
     * 检测后门等
     * @param message the message
     */
    public static void handleDanger(String message) {
        if (message == null || message.isBlank()) return;

        console.sendMessage(CMIChatColor.translate("&c&u&l&bD&4&lA&c&lN&b&lG&4&lE&c&lR | " + message));
    }

    public static void info(String message) {
        if (message == null || message.isBlank()) return;

        console.sendMessage(CMIChatColor.translate("&aINFO | " + message));
    }

    public static <T extends Enum<T>> Pair<HandleResult, T> handleEnumValueOf(
            String msg, Class<T> enumClass, String name) {
        try {
            return new Pair<>(HandleResult.SUCCESS, Enum.valueOf(enumClass, name.toUpperCase()));
        } catch (NullPointerException | IllegalArgumentException e) {
            handleError(msg);
        }
        return new Pair<>(HandleResult.FAILED, null);
    }

    public static Pair<HandleResult, ItemGroup> handleItemGroupGet(ProjectAddon addon, String id) {
        // fast path
        ItemGroup ig = CommonUtils.getIf(addon.getItemGroups(), i -> i.getKey().getKey().equalsIgnoreCase(id));

        // 检测是否为rsc内部分类
        if ((ig == null) && (id.startsWith("outside"))){
            String[] group = id.substring(8).split(":");
            if (group.length != 2) {
                console.sendMessage(CMIChatColor.translate("&4ERROR | 分类 " + id + " 格式错误。 示例: outside:slimefun:misc"));
                return new Pair<>(HandleResult.FAILED, null);
            }
            // 命名空间+名字
            for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups()) {
                if (itemGroup.getKey().getNamespace().equals(group[0]) && itemGroup.getKey().getKey().equals(group[1])) {
                    return new Pair<>(HandleResult.SUCCESS, itemGroup);
                }
            }
            console.sendMessage(CMIChatColor.translate("&4ERROR | 无法找到分类 " + id + "！"));
            return new Pair<>(HandleResult.FAILED, null);
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
            // it doesn't happen
        }
        return new Pair<>(HandleResult.FAILED, null);
    }

    public enum HandleResult {
        SUCCESS,
        FAILED
    }
}
