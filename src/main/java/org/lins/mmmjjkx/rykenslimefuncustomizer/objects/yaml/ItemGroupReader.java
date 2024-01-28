package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class ItemGroupReader extends YamlReader<ItemGroup> {
    public ItemGroupReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<ItemGroup> readAll(ProjectAddon addon) {
        List<ItemGroup> itemGroups = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var ig = readEach(key, addon);
            if (ig != null) {
                itemGroups.add(ig);
            }
        }
        return itemGroups;
    }

    @Override
    public ItemGroup readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item);
        if (stack == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载物品组"+s+": 物品为空或格式错误导致无法加载");
            return null;
        }
        String type = section.getString("type", "");
        NamespacedKey key = new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, s);
        ItemGroup group = switch (type) {
            default -> new ItemGroup(key, stack);
            case "sub" -> {
                NamespacedKey parent = NamespacedKey.fromString(section.getString("parent", ""));
                if (parent == null) {
                    ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载物品组"+s+": 错误的命名空间键格式！");
                    yield null;
                }
                ItemGroup raw = CommonUtils.getIf(Slimefun.getRegistry().getAllItemGroups(), ig -> ig.getKey().equals(parent));
                if (raw == null) {
                    ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载物品组"+s+": 无法找到父物品组" + parent);
                    yield null;
                }
                if (!(raw instanceof NestedItemGroup nig)) {
                    ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载物品组"+s+": 物品组" + parent + "不是一个嵌套物品组");
                    yield null;
                }
                yield new SubItemGroup(key, nig, stack);
            }
            case "locked" -> {
                List<NamespacedKey> parents = new ArrayList<>();
                for (String ig : section.getStringList("parents")) {
                    parents.add(NamespacedKey.fromString(ig));
                }
                yield new LockedItemGroup(key, stack, parents.toArray(new NamespacedKey[]{}));
            }
            case "nested", "parent" -> new NestedItemGroup(key, stack);
            case "seasonal" -> {
                Month month = Month.of(section.getInt("month", 1));
                yield new SeasonalItemGroup(key, month, 3, stack);
            }
        };
        if (group != null) {
            group.register(RykenSlimefunCustomizer.INSTANCE);
        }
        return group;
    }
}
