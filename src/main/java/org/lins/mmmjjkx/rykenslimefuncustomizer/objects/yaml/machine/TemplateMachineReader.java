package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomTemplateMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineTemplate;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class TemplateMachineReader extends YamlReader<CustomTemplateMachine> {
    public TemplateMachineReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomTemplateMachine readEach(String s) {
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
                "Found an error while loading super item " + s + " in addon " + addon.getAddonId()
                        + ": Invalid recipe type '" + recipeType + "'!",
                recipeType);
        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        boolean fasterIfMoreTemplates = section.getBoolean("fasterIfMoreTemplates", false);
        boolean moreOutputIfMoreTemplates = section.getBoolean("moreOutputIfMoreTemplates", false);

        List<Integer> input = section.getIntegerList("input");
        List<Integer> output = section.getIntegerList("output");

        if (output.isEmpty()) {
            ExceptionHandler.handleError("Found an error while loading template machine " + s + " in addon "
                    + addon.getAddonId() + ": " + "There must be at least one output slot!");
            return null;
        }

        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(s));

        if (menu == null) {
            ExceptionHandler.handleError("Found an error while loading template machine " + s + " in addon "
                    + addon.getAddonId() + ": The corresponding menu is not found");
            return null;
        }

        if (menu.getProgressSlot() < 0) {
            ExceptionHandler.handleError("Found an error while loading template machine " + s + " in addon "
                    + addon.getAddonId() + ": " + "The progress slot is not set");
            return null;
        }

        List<MachineTemplate> templates =
                readTemplates(id, input.size(), output.size(), section.getConfigurationSection("recipes"), addon);

        int templateSlot = section.getInt("templateSlot");

        if (templateSlot < 0 || templateSlot >= 54) {
            ExceptionHandler.handleError("Found an error while loading template machine " + s + " in addon "
                    + addon.getAddonId() + ": " + "The template slot is not set or is out of range(0-53)");
            return null;
        }

        int capacity = section.getInt("capacity");

        if (capacity < 0) {
            ExceptionHandler.handleError("Found an error while loading template machine " + s + " in addon "
                    + addon.getAddonId() + ": " + "The capacity is not set or is less than 0");
            return null;
        }

        int energy = section.getInt("consumption");

        if (energy <= 0) {
            ExceptionHandler.handleError("Found an error while loading template machine " + s + " in addon "
                    + addon.getAddonId() + ": The energy consumption is not set or is less than or equal to 0");
            return null;
        }

        return new CustomTemplateMachine(
                group.getSecondValue(),
                sfis,
                rt.getSecondValue(),
                recipe,
                menu,
                input,
                output,
                templateSlot,
                templates,
                energy,
                capacity,
                fasterIfMoreTemplates,
                moreOutputIfMoreTemplates);
    }

    private List<MachineTemplate> readTemplates(
            String s, int inputSize, int outputSize, ConfigurationSection section, ProjectAddon addon) {
        List<MachineTemplate> list = new ArrayList<>();
        if (section == null) {
            return list;
        }

        for (String key : section.getKeys(false)) {
            SlimefunItemStack item = getPreloadItem(key);
            if (item == null) {
                ExceptionHandler.handleError("Found an error while loading template machine " + s + " in addon "
                        + addon.getAddonId() + ": " + "Cannot find the item " + key
                        + " as a template item. You should input an Slimefun item id here.");
                continue;
            }
            List<CustomMachineRecipe> recipes =
                    readRecipes(s, inputSize, outputSize, section.getConfigurationSection(key), addon);
            list.add(new MachineTemplate(item, recipes));
        }

        return list;
    }

    private List<CustomMachineRecipe> readRecipes(
            String s, int inputSize, int outputSize, ConfigurationSection section, ProjectAddon addon) {
        List<CustomMachineRecipe> list = new ArrayList<>();
        if (section == null) {
            return list;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection recipes = section.getConfigurationSection(key);
            if (recipes == null) continue;
            int seconds = recipes.getInt("seconds");
            if (seconds < 0) {
                ExceptionHandler.handleError("Found an error while loading template machine " + s + "'s recipe " + key
                        + " in addon " + addon.getAddonId() + ": " + "The seconds of the recipe " + seconds
                        + " is less than 0");
                continue;
            }

            ConfigurationSection inputs = recipes.getConfigurationSection("input");
            ItemStack[] input = CommonUtils.readRecipe(inputs, addon, inputSize);
            ConfigurationSection outputs = recipes.getConfigurationSection("output");
            if (outputs == null) {
                ExceptionHandler.handleError(
                        "Found an error while loading template machine " + s + "'s recipe " + key + " in addon "
                                + addon.getAddonId() + ": " + "The output section is null or has an invalid format");
                continue;
            }

            List<Integer> chances = new ArrayList<>();

            ItemStack[] output = new ItemStack[outputSize];
            for (int i = 0; i < outputSize; i++) {
                ConfigurationSection section1 = outputs.getConfigurationSection(String.valueOf(i + 1));
                var item = CommonUtils.readItem(section1, true, addon);
                if (item != null) {
                    int chance = section1.getInt("chance", 100);

                    if (chance < 1) {
                        ExceptionHandler.handleError(
                                "Found an error while loading template machine " + s + " in addon " + addon.getAddonId()
                                        + ": " + "The chance of output " + chance + " is less than 1. Using 1 instead");
                        chance = 1;
                    }

                    output[i] = item;
                    chances.add(chance);
                }
            }

            boolean chooseOne = recipes.getBoolean("chooseOne", false);
            boolean forDisplay = recipes.getBoolean("forDisplay", false);

            input = CommonUtils.removeNulls(input);
            output = CommonUtils.removeNulls(output);

            list.add(new CustomMachineRecipe(seconds, input, output, chances, chooseOne, forDisplay));
        }
        return list;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("Found an error while loading template machine " + s + " in addon "
                    + addon.getAddonId() + ": " + "The item is null or has an invalid format");
            return null;
        }

        return List.of(new SlimefunItemStack(s, stack));
    }
}
