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
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载菜单" + s + "时遇到了问题: " + "菜单大小必须是9的倍数。");
            return null;
        }

        JavaScriptEval eval = null;
        if (section.contains("script")) {
            String script = section.getString("script", "");
            File file = new File(addon.getScriptsFolder(), script + ".js");
            if (!file.exists()) {
                ExceptionHandler.handleWarning(
                        "在附属" + addon.getAddonId() + "中加载菜单" + s + "时遇到了问题: " + "找不到脚本文件 " + file.getName());
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
                    ExceptionHandler.handleError(
                            "在附属" + addon.getAddonId() + "中加载菜单" + s + "时遇到了问题: " + "无法加载机器菜单" + s + ": 无法找到要导入的菜单");
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
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载菜单" + s + "时遇到了问题: " + "没有设置物品。");
            return null;
        }

        ItemStack progressItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

        for (String slot : slots.getKeys(false)) {
            try {
                int realSlot = Integer.parseInt(slot);
                if (realSlot > 53 || realSlot < 0) {
                    ExceptionHandler.handleWarning(
                            "在附属" + addon.getAddonId() + "中加载菜单" + s + "时遇到了问题: " + "位于槽位大于53或小于0的物品，跳过对此物品的读取。");
                    continue;
                }
                ConfigurationSection item = slots.getConfigurationSection(slot);
                ItemStack itemStack = CommonUtils.readItem(item, true, addon);
                if (itemStack == null) {
                    ExceptionHandler.handleWarning("在附属" + addon.getAddonId() + "中加载菜单" + s + "时遇到了问题: " + "位于槽位"
                            + realSlot + "的物品格式错误或输入了错误的数据无法读取，跳过对此物品的读取。");
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
                    ExceptionHandler.handleError(
                            "在附属" + addon.getAddonId() + "中加载菜单" + s + "时遇到了问题: " + "有错误的槽位区间表达式" + slot);
                    continue;
                }
                ConfigurationSection item = slots.getConfigurationSection(slot);
                ItemStack stack = CommonUtils.readItem(item, true, addon);
                if (stack == null) {
                    ExceptionHandler.handleWarning("在附属" + addon.getAddonId() + "中加载菜单" + s + "时遇到了问题: " + "位于区间槽位"
                            + slot + "的物品格式错误或输入了错误的数据无法读取，跳过对此物品的读取。");
                    continue;
                }
                IntStream intStream = IntStream.rangeClosed(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
                intStream.forEach(i -> {
                    if (i > 53 || i < 0) {
                        ExceptionHandler.handleWarning(
                                "在附属" + addon.getAddonId() + "中加载菜单" + s + "时遇到了问题: " + "位于区间槽位大于53或小于0，跳过对此槽位放置物品。");
                        return;
                    }
                    slotMap.put(i, stack);
                });
            }
        }

        return new CustomMenu(s, title, slotMap, playerInvClickable, progress, progressItem, eval).setSize(size);
    }

    // 菜单不需要预加载物品
    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        return List.of();
    }
}
