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

        String id = section.getString("id_alias", s);
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        SlimefunItemStack sfis = getPreloadItem(id);
        if (sfis == null) return null;

        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "Found an error while loading capacitor " + s + " in addon " + addon.getAddonId()
                        + ": Invalid recipe type '" + recipeType + "'!",
                recipeType);

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        int capacity = section.getInt("capacity");
        if (capacity < 1) {
            ExceptionHandler.handleError("Found an error while loading capacitor " + s + " in addon "
                    + addon.getAddonId() + ": " + "Capacity must be at least 1");
            return null;
        }

        Capacitor instance = new Capacitor(
                Objects.requireNonNull(group.getSecondValue()),
                capacity,
                sfis,
                Objects.requireNonNull(rt.getSecondValue()),
                recipe);

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
            ExceptionHandler.handleError("Found an error while loading capacitor " + id + " in addon "
                    + addon.getAddonId() + ": " + "The item is null or has an invalid format");
            return null;
        }

        if (!stack.getType().isBlock()) {
            ExceptionHandler.handleError("Found an error while loading capacitor " + id + " in addon "
                    + addon.getAddonId() + ": " + "The item's material type must be a placeable block");
            return null;
        }

        return List.of(new SlimefunItemStack(id, stack));
    }
}
