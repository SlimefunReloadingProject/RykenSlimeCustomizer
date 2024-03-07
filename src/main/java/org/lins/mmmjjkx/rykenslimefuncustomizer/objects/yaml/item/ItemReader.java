package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomEnergyItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.CustomItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomUnplaceableItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;

public class ItemReader extends YamlReader<CustomItem> {
    public ItemReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public CustomItem readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载物品"+s+": 物品为空或格式错误导致无法加载");
            return null;
        }

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] itemStacks = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                "错误的配方类型" + recipeType + "!", recipeType
        );

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);

        JavaScriptEval eval = null;
        if (section.contains("script")) {
            String script = section.getString("script", "");
            File file = new File(addon.getScriptsFolder(), script + ".js");
            if (!file.exists()) {
                ExceptionHandler.handleWarning("找不到脚本文件 " + file.getName());
            } else {
                eval = new JavaScriptEval(file, addon);
            }
        }

        CustomItem instance;

        boolean energy = section.contains("energy_capacity");
        if (energy) {
            double energyCapacity = section.getDouble("energy_capacity");
            if (energyCapacity < 1) {
                ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载物品"+s+ "能源容量不能小于1");
                return null;
            }
            instance = new CustomEnergyItem(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), itemStacks, (float) energyCapacity, eval);
        } else {
            instance = new CustomUnplaceableItem(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), itemStacks, eval);
        }

        instance.register(RykenSlimefunCustomizer.INSTANCE);

        return instance;
    }
}
