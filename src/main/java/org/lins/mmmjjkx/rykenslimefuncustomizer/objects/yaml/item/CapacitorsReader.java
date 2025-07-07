package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.Capacitor;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.List;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class CapacitorsReader extends YamlReader<Capacitor> {
    public CapacitorsReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public Capacitor readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        String id = addon.getId(s, section.getString("id_alias"));
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        SlimefunItemStack sfis = getPreloadItem(id);
        if (sfis == null) return null;

        Pair<RecipeType, ItemStack[]> recipeType = getRecipe(section, addon);
        RecipeType rt = recipeType.getFirstValue();
        ItemStack[] recipe = recipeType.getSecondValue();

        int capacity = section.getInt("capacity");
        if (capacity < 1) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载电容" + s + "时遇到了问题: " + "容量不能小于1");
            return null;
        }

        Capacitor instance = new Capacitor(
                Objects.requireNonNull(group.getSecondValue()), capacity, sfis, Objects.requireNonNull(rt), recipe);

        instance.register(RykenSlimefunCustomizer.INSTANCE);
        return instance;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String id) {
        ConfigurationSection section = configuration.getConfigurationSection(id);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载电容" + id + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        if (!stack.getType().isBlock()) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载电容" + id + "时遇到了问题: " + "物品的材料类型必须是可放置的方块");
            return null;
        }

        return List.of(new SlimefunItemStack(addon.getId(id, section.getString("id_alias")), stack));
    }
}
