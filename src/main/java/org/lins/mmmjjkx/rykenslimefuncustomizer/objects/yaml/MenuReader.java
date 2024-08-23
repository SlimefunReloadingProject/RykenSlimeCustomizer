package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class MenuReader extends YamlReader<CustomMenu> {
    public static final int NOT_SET = -1;
    private static final NamespacedKey PROGRESS_KEY = new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, "progress");

    public MenuReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomMenu readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ExceptionHandler.HandleResult conflict = ExceptionHandler.handleMenuConflict(s, addon);
        if (conflict == ExceptionHandler.HandleResult.FAILED) return null;

        String title = section.getString("title", "");
        boolean playerInvClickable = section.getBoolean("playerInvClickable", true);
        int size = section.getInt("size", NOT_SET);

        if (section.contains("size") && size != NOT_SET && size % 9 != 0) {
            ExceptionHandler.handleError(
                    "Found error in menu " + s + "in " + addon.getAddonId() + "addon: size must be a multiple of 9");
            return null;
        }

        JavaScriptEval eval = null;
        if (section.contains("script")) {
            String script = section.getString("script", "");
            File file = new File(addon.getScriptsFolder(), script + ".js");
            if (!file.exists()) {
                ExceptionHandler.handleWarning("Found error in menu " + s + "in " + addon.getAddonId()
                        + "addon: cannot find script file " + file.getName() + "skipping script evaluation");
            } else {
                eval = new JavaScriptEval(file, addon);
            }
        }

        if (section.contains("import")) {
            String menuId = section.getString("import", "");
            BlockMenuPreset menuPreset = Slimefun.getRegistry().getMenuPresets().get(menuId);
            if (menuPreset == null) {
                CustomMenu menu =
                        CommonUtils.getIf(addon.getMenus(), m -> m.getId().equals(menuId));
                if (menu == null) {
                    ExceptionHandler.handleError("Found error in menu " + s + "in " + addon.getAddonId()
                            + "addon: cannot find menu " + menuId + " to import");
                    return null;
                } else {
                    return new CustomMenu(s, title, menu);
                }
            }
            return new CustomMenu(s, title, menuPreset, new ItemStack(Material.BLACK_STAINED_GLASS_PANE), eval);
        }

        int progress = 22;

        Map<Integer, ItemStack> slotMap = new HashMap<>();
        ConfigurationSection slots = section.getConfigurationSection("slots");
        if (slots == null) {
            ExceptionHandler.handleError(
                    "Found error in menu " + s + "in " + addon.getAddonId() + "addon: slots section is missing");
            return null;
        }

        ItemStack progressItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

        for (String slot : slots.getKeys(false)) {
            try {
                int realSlot = Integer.parseInt(slot);
                if (realSlot > 53 || realSlot < 0) {
                    ExceptionHandler.handleWarning("There's a slot in menu " + s + "in " + addon.getAddonId()
                            + "addon that is out of range(0-53), skipping it.");
                    continue;
                }
                ConfigurationSection item = slots.getConfigurationSection(slot);
                ItemStack itemStack = CommonUtils.readItem(item, true, addon);
                if (itemStack == null) {
                    ExceptionHandler.handleWarning("Found error in menu " + s + "in " + addon.getAddonId()
                            + "addon: cannot read item in slot " + slot + ", skipping it.");
                    continue;
                }
                if (item.getBoolean("progressbar", false)) {
                    progress = realSlot;
                    if (item.contains("progressBarItem")) {
                        progressItem =
                                CommonUtils.readItem(item.getConfigurationSection("progressBarItem"), true, addon);
                    } else {
                        progressItem = itemStack;
                    }

                    ItemMeta meta = progressItem.getItemMeta();
                    if (meta != null) {
                        PersistentDataContainer pdc = meta.getPersistentDataContainer();
                        pdc.set(PROGRESS_KEY, PersistentDataType.INTEGER, 0);
                    }
                }
                slotMap.put(realSlot, itemStack);
            } catch (NumberFormatException e) {
                String[] range = slot.split("-");
                if (range.length != 2) {
                    ExceptionHandler.handleError("Found error in menu " + s + "in " + addon.getAddonId()
                            + "addon: invalid slot number range " + slot + ", skipping it.");
                    continue;
                }
                if (Integer.parseInt(range[0]) > Integer.parseInt(range[1])) {
                    ExceptionHandler.handleError("Found error in menu " + s + "in " + addon.getAddonId()
                            + "addon: invalid slot number range " + slot + ", skipping it.");
                    continue;
                }
                ConfigurationSection item = slots.getConfigurationSection(slot);
                ItemStack stack = CommonUtils.readItem(item, true, addon);
                if (stack == null) {
                    ExceptionHandler.handleWarning("Found error in menu " + s + "in " + addon.getAddonId()
                            + "addon: cannot read item in slot " + slot + ", skipping it.");
                    continue;
                }
                IntStream intStream = IntStream.rangeClosed(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
                intStream.forEach(i -> {
                    if (i > 53 || i < 0) {
                        ExceptionHandler.handleWarning("There's a slot in menu " + s + "in " + addon.getAddonId()
                                + "addon that is out of range(0-53), skipping it.");
                        return;
                    }
                    slotMap.put(i, stack);
                });
            }
        }

        return new CustomMenu(s, title, slotMap, playerInvClickable, progress, progressItem, eval).setSize(size);
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        return List.of();
    }
}
