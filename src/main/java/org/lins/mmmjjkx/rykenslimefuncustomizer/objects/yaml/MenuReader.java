package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        String title = configuration.getString("title", "");
        boolean emptySlotsClickable, playerInvClickable;
        emptySlotsClickable = configuration.getBoolean("emptySlotsClickable", true);
        playerInvClickable = configuration.getBoolean("playerInvClickable", true);

        Map<Integer, ItemStack> slotMap = new HashMap<>();
        ConfigurationSection slots = section.getConfigurationSection("slots");
        if (slots == null) return new CustomMenu(title, slotMap, emptySlotsClickable, playerInvClickable);

        for (String slot : slots.getKeys(false)) {
            int realSlot = Integer.parseInt(slot);
            if (realSlot > 53 || realSlot < 0) {
                ExceptionHandler.handleWarning("在菜单"+s+"中，有位于槽位大于53或小于0的物品，跳过对此物品的读取。");
                continue;
            }
            ConfigurationSection item = slots.getConfigurationSection(String.valueOf(realSlot));
            ItemStack itemStack = CommonUtils.readItem(item);
            if (itemStack == null) {
                ExceptionHandler.handleWarning("在菜单"+s+"中，有位于槽位"+realSlot+"的物品格式错误或输入了错误的数据无法读取，跳过对此物品的读取。");
                continue;
            }
            slotMap.put(realSlot, itemStack);
        }

        return new CustomMenu(title, slotMap, emptySlotsClickable, playerInvClickable);
    }
}
