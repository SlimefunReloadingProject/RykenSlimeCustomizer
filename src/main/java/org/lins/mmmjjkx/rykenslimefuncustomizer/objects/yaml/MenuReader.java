package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class MenuReader extends YamlReader<CustomMenu> {
    public MenuReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<CustomMenu> readAll(ProjectAddon addon) {
        List<CustomMenu> menus = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var menu = readEach(key, addon);
            if (menu != null) {
                menus.add(menu);
            }
        }
        return menus;
    }

    @Override
    public CustomMenu readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ExceptionHandler.HandleResult conflict = ExceptionHandler.handleMenuConflict(s, addon);
        if (conflict == ExceptionHandler.HandleResult.FAILED) return null;

        String title = configuration.getString("title", "");
        boolean playerInvClickable = configuration.getBoolean("playerInvClickable", true);

        Map<Integer, ItemStack> slotMap = new HashMap<>();
        ConfigurationSection slots = section.getConfigurationSection("slots");
        if (slots == null) return new CustomMenu(s, title, slotMap, playerInvClickable, null);

        int progress = -1;

        JavaScriptEval eval = null;
        if (section.contains("script")) {
            String script = section.getString("script", "");
            File file = new File(addon.getScriptsFolder(), script + ".js");
            if (!file.exists()) {
                ExceptionHandler.handleWarning("找不到脚本文件 " + file.getName());
            } else {
                eval = new JavaScriptEval(file);
            }
        }

        for (String slot : slots.getKeys(false)) {
            try {
                int realSlot = Integer.parseInt(slot);
                if (realSlot > 53 || realSlot < 0) {
                    ExceptionHandler.handleWarning("在菜单"+s+"中有位于槽位大于53或小于0的物品，跳过对此物品的读取。");
                    continue;
                }
                ConfigurationSection item = slots.getConfigurationSection(String.valueOf(realSlot));
                ItemStack itemStack = CommonUtils.readItem(item, true, addon);
                if (itemStack == null) {
                    ExceptionHandler.handleWarning("在菜单"+s+"中有位于槽位"+realSlot+"的物品格式错误或输入了错误的数据无法读取，跳过对此物品的读取。");
                    continue;
                }
                if (section.getBoolean("progressbar", false)) {
                    progress = realSlot;
                }
                slotMap.put(realSlot, itemStack);
            } catch (NumberFormatException e) {
                String[] range = slot.split("-");
                if (range.length != 2) {
                    ExceptionHandler.handleError("在菜单"+s+"中有错误的槽位区间表达式" + slot);
                    continue;
                }
                ConfigurationSection item = slots.getConfigurationSection(slot);
                ItemStack stack = CommonUtils.readItem(item, true, addon);
                if (stack == null) {
                    ExceptionHandler.handleWarning("在菜单"+s+"中有位于区间槽位"+slot+"的物品格式错误或输入了错误的数据无法读取，跳过对此物品的读取。");
                    continue;
                }
                IntStream intStream = IntStream.rangeClosed(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
                intStream.forEach(i -> {
                    if (i > 53 || i < 0) {
                        ExceptionHandler.handleWarning("在菜单"+s+"中有位于区间槽位大于53或小于0，跳过对此槽位放置物品。");
                        return;
                    }
                    slotMap.put(i, stack);
                });
            }
        }

        CustomMenu menu = new CustomMenu(s, title, slotMap, playerInvClickable, progress, eval);
        menu.outSideInit();
        return menu;
    }
}
