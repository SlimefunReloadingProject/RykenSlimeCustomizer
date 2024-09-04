package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.List;
import java.util.Objects;
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
        String id = section.getString("id_alias", s);

        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        SlimefunItemStack slimefunItemStack = getPreloadItem(id);
        if (slimefunItemStack == null) return null;

        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "Found an error while loading solar generator " + s + " in addon " + addon.getAddonId()
                        + ": Invalid recipe type '" + recipeType + "'!",
                recipeType);

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        int dayEnergy = section.getInt("dayEnergy");
        int nightEnergy = section.getInt("nightEnergy");

        if (dayEnergy < 1) {
            ExceptionHandler.handleError("Found an error while loading solar generator " + s + " in addon "
                    + addon.getAddonId() + ": " + "The day energy must be greater than 0");
            return null;
        }

        if (nightEnergy < 1) {
            ExceptionHandler.handleError("Found an error while loading solar generator " + s + " in addon "
                    + addon.getAddonId() + ": " + "The night energy must be greater than 0");
            return null;
        }

        int capacity = section.getInt("capacity", 0);
        int lightLevel = section.getInt("lightLevel", 15);

        if (lightLevel < 0 || lightLevel > 15) {
            ExceptionHandler.handleError("Found an error while loading solar generator " + s + " in addon "
                    + addon.getAddonId() + ": " + "The required light level is not between 0 and 15, using 15 instead");
            lightLevel = 15;
        }

        return new CustomSolarGenerator(
                Objects.requireNonNull(group.getSecondValue()),
                dayEnergy,
                nightEnergy,
                slimefunItemStack,
                Objects.requireNonNull(rt.getSecondValue()),
                recipe,
                capacity,
                lightLevel);
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("Found an error while loading solar generator " + s + " in addon "
                    + addon.getAddonId() + ": " + "The item is null or has an invalid format");
            return null;
        }

        return List.of(new SlimefunItemStack(s, stack));
    }
}
