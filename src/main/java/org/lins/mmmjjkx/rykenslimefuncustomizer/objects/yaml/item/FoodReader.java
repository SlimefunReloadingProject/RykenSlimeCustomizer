package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item;

import de.tr7zw.nbtapi.NBT;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.io.File;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomFood;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class FoodReader extends YamlReader<CustomFood> {
    public FoodReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public CustomFood readEach(String s) {
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

        ItemStack[] itemStacks = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "Found an error while loading capacitor " + s + " in addon " + addon.getAddonId()
                        + ": Invalid recipe type '" + recipeType + "'!",
                recipeType);

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        JavaScriptEval eval = null;
        if (section.contains("script")) {
            String script = section.getString("script", "");
            File file = new File(addon.getScriptsFolder(), script + ".js");
            if (!file.exists()) {
                ExceptionHandler.handleWarning("There was an error while loading food " + s + " in addon "
                        + addon.getAddonId() + ": " + "Could not find script file " + file.getName());
            } else {
                eval = new JavaScriptEval(file, addon);
            }
        }

        if (CommonUtils.versionToCode(Bukkit.getMinecraftVersion()) >= 1205) {
            if (Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
                nbtApply(id, section, sfis);
            }
        }

        return new CustomFood(group.getSecondValue(), sfis, rt.getSecondValue(), itemStacks, eval);
    }

    private void nbtApply(String s, ConfigurationSection section, SlimefunItemStack sfis) {
        int nutrition = section.getInt("nutrition");
        float saturation = section.getInt("saturation");
        boolean alwaysEatable = section.getBoolean("always_eatable", false);
        float eatseconds = section.getInt("eat_seconds", 0);
        if (nutrition < 1) {
            ExceptionHandler.handleError("Found an error while loading food " + s + " in addon " + addon.getAddonId()
                    + ": " + "Nutrition value " + nutrition + " is less than 1! Value has been set to 1.");
            nutrition = 1;
        }
        if (saturation < 0f) {
            ExceptionHandler.handleError("Found an error while loading food " + s + " in addon " + addon.getAddonId()
                    + ": " + "Saturation value " + saturation + " is less than 0! Value has been set to 0.");
            saturation = 0f;
        }
        if (eatseconds < 0) {
            ExceptionHandler.handleError("Found an error while loading food " + s + " in addon " + addon.getAddonId()
                    + ": " + "Eat seconds value " + eatseconds + " is less than 0! Value has been set to 1.6.");
            eatseconds = 1.6f;
        }

        final int finalNutrition = nutrition;
        final float finalSaturation = saturation;
        final float finalEatSeconds = eatseconds;

        NBT.modify(sfis, nbt -> {
            nbt.setInteger("nutrition", finalNutrition);
            nbt.setFloat("saturation", finalSaturation);
            nbt.setBoolean("can_always_eat", alwaysEatable);
            nbt.setFloat("eat_seconds", finalEatSeconds);
        });
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String id) {
        ConfigurationSection section = configuration.getConfigurationSection(id);

        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);
        if (stack == null) {
            ExceptionHandler.handleError("Found an error while loading food " + id + " in addon " + addon.getAddonId()
                    + ": " + "The item is null or has an invalid format");
            return null;
        }

        SlimefunItemStack sfis = new SlimefunItemStack(id, stack);

        return List.of(sfis);
    }
}
