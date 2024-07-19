package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomTemplateMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomTemplateMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

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
                "在附属" + addon.getAddonId() + "中加载继承物品" + s + "时遇到了问题: " + "错误的配方类型" + recipeType + "!", recipeType);
        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;



        return null;
    }

    private ItemStack[] removeNulls(ItemStack[] origin) {
        int count = 0;
        for (ItemStack element : origin) {
            if (element != null) {
                count++;
            }
        }

        ItemStack[] newArray = new ItemStack[count];

        int index = 0;
        for (ItemStack element : origin) {
            if (element != null) {
                newArray[index] = element;
                index++;
            }
        }

        return newArray;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载模板机器" + s + "时遇到了问题: " + "物品为空或格式错误导致无法加载");
            return null;
        }

        return List.of(new SlimefunItemStack(s, stack));
    }
}
