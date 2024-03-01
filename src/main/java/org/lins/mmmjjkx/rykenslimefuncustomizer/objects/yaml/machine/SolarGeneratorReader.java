package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomSolarGenerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.Objects;

public class SolarGeneratorReader extends YamlReader<CustomSolarGenerator> {
    public SolarGeneratorReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public CustomSolarGenerator readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载太阳能发电机"+s+": 物品为空或格式错误导致无法加载");
            return null;
        }

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "错误的配方类型" + recipeType + "!", recipeType
        );

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);

        int dayEnergy = section.getInt("dayEnergy");
        int nightEnergy = section.getInt("nightEnergy");

        if (dayEnergy < 1) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载太阳能发电机"+s+": 白天产量不能小于1");
            return null;
        }

        if (nightEnergy < 1) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载太阳能发电机"+s+": 夜晚产量不能小于1");
            return null;
        }

        int capacity = section.getInt("capacity", 0);

        int lightLevel = section.getInt("lightLevel", 15);

        if (lightLevel < 0 || lightLevel > 15) {
            ExceptionHandler.handleError("在附属"+addon.getAddonName()+"中加载太阳能发电机时发现问题"+s+": 所需光照等级不能小于0或大于15，已转为15");
            lightLevel = 15;
        }

        return new CustomSolarGenerator(Objects.requireNonNull(group.getSecondValue()), dayEnergy, nightEnergy,
                slimefunItemStack, Objects.requireNonNull(rt.getSecondValue()), recipe, capacity, lightLevel);
    }
}
