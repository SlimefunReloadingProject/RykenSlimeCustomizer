package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomGeoResource;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class GeoResourceReader extends YamlReader<CustomGeoResource> {
    public GeoResourceReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<CustomGeoResource> readAll(ProjectAddon addon) {
        List<CustomGeoResource> resources = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var geo = readEach(key, addon);
            if (geo != null) {
                resources.add(geo);
            }
        }
        return resources;
    }

    @Override
    public CustomGeoResource readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section != null) {
            ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);
            if (result == ExceptionHandler.HandleResult.FAILED) return null;

            String igId = section.getString("item_group");
            ConfigurationSection item = section.getConfigurationSection("item");
            ItemStack stack = CommonUtils.readItem(item);
            SlimefunItem slimefunItem;
            int placeable = stack.getItemMeta().getPersistentDataContainer().get(CommonUtils.PLACEABLE, PersistentDataType.INTEGER).intValue();
            Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
            if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
            ItemStack[] itemStacks = CommonUtils.readRecipe(section.getConfigurationSection("recipe"));
            String recipetype = section.getString("recipe_type", "NULL");

            Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.handleField(
                    "错误的配方类型" + recipetype + "!", "", RecipeType.class, recipetype
            );

            if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
            SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);



            ExceptionHandler.HandleResult result1 = ExceptionHandler.handleItemGroupAddItem(addon, igId);
        }
        return null;
    }

    @Override
    public void save(CustomGeoResource customGeoResource) {

    }
}