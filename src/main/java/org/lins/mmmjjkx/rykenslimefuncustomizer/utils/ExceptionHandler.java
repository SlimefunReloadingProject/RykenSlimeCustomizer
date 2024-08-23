package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.RecipeTypeMap;

public class ExceptionHandler {
    private static final @NotNull ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static HandleResult handleIdConflict(String id) {
        SlimefunItem i = SlimefunItem.getById(id);
        if (i != null) {
            console.sendMessage(
                    CMIChatColor.translate(
                            "&4ERROR | ID conflict: " + id + "and the item in addon called "
                                    + i.getAddon().getName()
                                    + " have the same ID. Please change the ID of the item in the addon or remove the item in the addon. "));
            return HandleResult.FAILED;
        }
        return HandleResult.SUCCESS;
    }

    public static HandleResult handleMenuConflict(String id, ProjectAddon addon) {
        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(id));
        if (menu != null) {
            console.sendMessage(
                    CMIChatColor.translate("&4ERROR | ID conflict: there's already a menu with the ID " + id));
            return HandleResult.FAILED;
        }
        return HandleResult.SUCCESS;
    }

    public static HandleResult handleGroupIdConflict(String id) {
        ItemGroup ig = CommonUtils.getIf(
                Slimefun.getRegistry().getAllItemGroups(),
                i -> i.getKey().getKey().equalsIgnoreCase(id));
        if (ig != null) {
            if (ig.getAddon() != null) {
                if (ig.getAddon().getClass() == RykenSlimefunCustomizer.class) {
                    console.sendMessage(CMIChatColor.translate("&4ERROR | ID conflict: " + id + "and the item group "
                            + ig.getKey().getKey() + " has the same key."));
                    return HandleResult.FAILED;
                }
                return HandleResult.SUCCESS;
            }
            console.sendMessage(CMIChatColor.translate("&4ERROR | ID conflict: " + id + "and the item group "
                    + ig.getKey().getKey() + " has the same key."));
            return HandleResult.FAILED;
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
        ItemGroup ig = CommonUtils.getIf(
                addon.getItemGroups(), i -> i.getKey().getKey().equalsIgnoreCase(id));
        if (ig == null) {
            ItemGroup ig2 = CommonUtils.getIf(
                    Slimefun.getRegistry().getAllItemGroups(),
                    i -> i.getKey().toString().equalsIgnoreCase(id));
            if (ig2 == null) {
                handleError("Cannot find item group with ID " + id + " in addon called " + addon.getAddonName());
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
            // it doesn't happen
        }
        return new Pair<>(HandleResult.FAILED, null);
    }

    public enum HandleResult {
        SUCCESS,
        FAILED
    }
}
