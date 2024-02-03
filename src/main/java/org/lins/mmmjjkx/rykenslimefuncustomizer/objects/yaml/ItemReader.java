package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomPlaceableItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomUnplaceableItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemReader extends YamlReader<CustomItem> {
    public ItemReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<CustomItem> readAll(ProjectAddon addon) {
        List<CustomItem> items = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var item = readEach(key, addon);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
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

        int placeable = stack.getItemMeta().getPersistentDataContainer().getOrDefault(CommonUtils.PLACEABLE, PersistentDataType.INTEGER, 0);
        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] itemStacks = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.handleField(
                "错误的配方类型" + recipeType + "!", "", RecipeType.class, recipeType
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
                eval = new JavaScriptEval(file);
            }
        }

        CustomItem sfi;
        if (placeable == 1) {
            sfi = new CustomPlaceableItem(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), itemStacks, eval);
        } else {
            sfi = new CustomUnplaceableItem(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), itemStacks, eval);
        }

        ExceptionHandler.handleItemGroupAddItem(addon, igId, sfi);
        return sfi;
    }
}
