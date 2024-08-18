package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.factories.SimpleMachineFactory;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.SimpleMachineType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class SimpleMachineReader extends YamlReader<SlimefunItem> {
    public SimpleMachineReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public SlimefunItem readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        String id = section.getString("id_alias", s);

        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(id);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");

        SlimefunItemStack slimefunItemStack = getPreloadItem(id);
        if (slimefunItemStack == null) return null;

        Pair<ExceptionHandler.HandleResult, ItemGroup> itemGroupPair = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (itemGroupPair.getFirstValue() == ExceptionHandler.HandleResult.FAILED
                || itemGroupPair.getSecondValue() == null) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> resultRecipeTypePair = ExceptionHandler.getRecipeType(
                "Found an error while loading recipe machine " + s + " in addon " + addon.getAddonId()
                        + ": Invalid recipe type '" + recipeType + "'!",
                recipeType);

        if (resultRecipeTypePair.getFirstValue() == ExceptionHandler.HandleResult.FAILED
                || resultRecipeTypePair.getSecondValue() == null) return null;

        String machineTypeStr = section.getString("type");

        Pair<ExceptionHandler.HandleResult, SimpleMachineType> machineTypePair = ExceptionHandler.handleEnumValueOf(
                "Found an error while loading simple machine " + s + " in addon " + addon.getAddonId()
                        + ": Invalid simple machine type: " + machineTypeStr,
                SimpleMachineType.class,
                machineTypeStr);
        if (machineTypePair.getFirstValue() == ExceptionHandler.HandleResult.FAILED
                || machineTypePair.getSecondValue() == null) {
            return null;
        }

        SimpleMachineType machineType = machineTypePair.getSecondValue();
        ConfigurationSection settings = section.getConfigurationSection("settings");

        if (settings == null) {
            ExceptionHandler.handleError("Found an error while loading simple machine " + s + " in addon "
                    + addon.getAddonId() + ": Machine has no settings");
            return null;
        }

        int capacity = 0;
        int consumption = 0;
        int speed = 1;
        int radius = 1;
        int repairFactor = 10;

        if (machineType.isEnergy()) {
            capacity = settings.getInt("capacity");
            if (capacity < 1) {
                ExceptionHandler.handleError("Found an error while loading simple machine " + s + " in addon "
                        + addon.getAddonId() + ": Energy capacity is less than 1");
                return null;
            }

            consumption = settings.getInt("consumption");
            if (consumption < 1) {
                ExceptionHandler.handleError("Found an error while loading simple machine " + s + " in addon "
                        + addon.getAddonId() + ": Energy consumption is less than 1");
                return null;
            }

            if (!isAccelerator(machineType)) {
                speed = settings.getInt("speed", 1);
                if (speed < 1) {
                    ExceptionHandler.handleError("Found an error while loading simple machine " + s + " in addon "
                            + addon.getAddonId() + ": Speed is less than 1");
                    return null;
                }
            } else {
                radius = settings.getInt("radius", 1);
                if (radius < 1) {
                    ExceptionHandler.handleError("Found an error while loading simple machine " + s + " in addon "
                            + addon.getAddonId() + ": Radius is less than 1");
                    return null;
                }

                if (machineType == SimpleMachineType.CROP_GROWTH_ACCELERATOR) {
                    speed = settings.getInt("speed", 1);
                    if (speed < 1) {
                        ExceptionHandler.handleError("Found an error while loading simple machine " + s + " in addon "
                                + addon.getAddonId() + ": Speed is less than 1");
                        return null;
                    }
                }
            }

            if (machineType == SimpleMachineType.AUTO_ANVIL) {
                repairFactor = settings.getInt("repair_factor", 10);
                if (repairFactor < 1) {
                    ExceptionHandler.handleError("Found an error while loading simple machine " + s + " in addon "
                            + addon.getAddonId() + ": Repair factor is less than 1");
                    return null;
                }
            }
        }

        SlimefunItem instance = SimpleMachineFactory.create(
                itemGroupPair.getSecondValue(),
                slimefunItemStack,
                resultRecipeTypePair.getSecondValue(),
                recipe,
                machineType,
                capacity,
                consumption,
                speed,
                radius,
                repairFactor);

        instance.register(RykenSlimefunCustomizer.INSTANCE);

        return instance;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("Found an error while loading simple machine " + s + " in addon "
                    + addon.getAddonId() + ": " + "The item is null or has an invalid format");
            return null;
        }

        return List.of(new SlimefunItemStack(s, stack));
    }

    private boolean isAccelerator(SimpleMachineType type) {
        return type == SimpleMachineType.TREE_GROWTH_ACCELERATOR
                || type == SimpleMachineType.CROP_GROWTH_ACCELERATOR
                || type == SimpleMachineType.ANIMAL_GROWTH_ACCELERATOR;
    }
}
