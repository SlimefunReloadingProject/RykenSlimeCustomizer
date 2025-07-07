package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomSolarGenerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class SolarGeneratorReader extends YamlReader<CustomSolarGenerator> {
    public SolarGeneratorReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomSolarGenerator readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        String id = addon.getId(s, section.getString("id_alias"));

        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        SlimefunItemStack slimefunItemStack = getPreloadItem(id);
        if (slimefunItemStack == null) return null;

        Pair<RecipeType, ItemStack[]> recipePair = getRecipe(section, addon);
        RecipeType rt = recipePair.getFirstValue();
        ItemStack[] recipe = recipePair.getSecondValue();

        int dayEnergy = section.getInt("dayEnergy");
        int nightEnergy = section.getInt("nightEnergy");

        if (dayEnergy < 1) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载太阳能发电机" + s + "时遇到了问题: " + "白天产电量不能小于1");
            return null;
        }

        if (nightEnergy < 1) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载太阳能发电机" + s + "时遇到了问题: " + "夜晚产电量不能小于1");
            return null;
        }

        int capacity = section.getInt("capacity", 0);
        int lightLevel = section.getInt("lightLevel", 15);

        if (lightLevel < 0 || lightLevel > 15) {
            ExceptionHandler.handleError(
                    "在附属" + addon.getAddonId() + "中加载太阳能发电机" + s + "时遇到了问题: " + "所需光照等级不能小于0或大于15，已转为15");
            lightLevel = 15;
        }

        return new CustomSolarGenerator(
                group.getSecondValue(), dayEnergy, nightEnergy, slimefunItemStack, rt, recipe, capacity, lightLevel);
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载太阳能发电机" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(addon.getId(s, section.getString("id_alias")), stack));
    }
}
