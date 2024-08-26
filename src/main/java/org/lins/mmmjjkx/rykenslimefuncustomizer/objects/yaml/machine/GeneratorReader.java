package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomGenerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class GeneratorReader extends YamlReader<CustomGenerator> {
    public GeneratorReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @SneakyThrows
    @Override
    public CustomGenerator readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        String id = section.getString("id_alias", s);

        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        SlimefunItemStack sfis = getPreloadItem(id);
        if (sfis == null) return null;

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "Found an error while loading generator " + s + " in addon " + addon.getAddonId()
                        + ": Invalid recipe type '" + recipeType + "'!",
                recipeType);

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(s));

        List<Integer> input = section.getIntegerList("input");
        List<Integer> output = section.getIntegerList("output");

        ConfigurationSection fuelsSection = section.getConfigurationSection("fuels");
        List<MachineFuel> fuels = readFuels(s, fuelsSection, addon);
        int capacity = section.getInt("capacity", 0);
        int production = section.getInt("production");

        if (production < 1) {
            ExceptionHandler.handleError("Found an error while loading generator " + s + " in addon "
                    + addon.getAddonId() + ": Production cannot be less than 1!");
            return null;
        }

        return new CustomGenerator(
                group.getSecondValue(),
                sfis,
                rt.getSecondValue(),
                recipe,
                menu,
                capacity,
                input,
                output,
                production,
                fuels);
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String id) {
        ConfigurationSection section = configuration.getConfigurationSection(id);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("Found an error while loading generator " + id + " in addon "
                    + addon.getAddonId() + ": " + "The item is null or has an invalid format");
            return null;
        }
        return List.of(new SlimefunItemStack(id, stack));
    }

    private List<MachineFuel> readFuels(String s, ConfigurationSection section, ProjectAddon addon) {
        List<MachineFuel> fuels = new ArrayList<>();

        if (section == null) return fuels;

        for (String key : section.getKeys(false)) {
            ConfigurationSection section1 = section.getConfigurationSection(key);
            if (section1 == null) continue;
            ConfigurationSection item = section1.getConfigurationSection("item");
            ItemStack stack = CommonUtils.readItem(item, true, addon);
            if (stack == null) {
                ExceptionHandler.handleError(
                        "Found an error while loading generator " + s + " in addon " + addon.getAddonId() + ": "
                                + "The fuel " + key + "'s item is null or has an invalid format. Skipping...");
                continue;
            }
            int seconds = section1.getInt("seconds");

            if (seconds < 1) {
                ExceptionHandler.handleError(
                        "Found an error while loading generator " + s + " in addon " + addon.getAddonId() + ": "
                                + "The fuel " + key + "'s production seconds is less than 1. Skipping...");
                continue;
            }

            ItemStack output = null;
            if (section1.contains("output")) {
                ConfigurationSection outputSet = section1.getConfigurationSection("output");
                output = CommonUtils.readItem(outputSet, true, addon);
                if (output == null) {
                    ExceptionHandler.handleError("Found an error while loading generator " + s + " in addon "
                            + addon.getAddonId() + ": " + "The fuel " + key
                            + "'s output item is null or has an invalid format. It will output nothing!");
                }
            }

            MachineFuel fuel = new MachineFuel(seconds, stack, output);
            fuels.add(fuel);
        }
        return fuels;
    }
}
