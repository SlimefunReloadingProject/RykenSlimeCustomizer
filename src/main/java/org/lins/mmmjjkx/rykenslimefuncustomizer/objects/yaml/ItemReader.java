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
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomPlaceableItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomUnplaceableItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class ItemReader extends YamlReader<SlimefunItem> {
    public ItemReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<SlimefunItem> readAll(ProjectAddon addon) {
        List<SlimefunItem> items = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var item = readEach(key, addon);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public SlimefunItem readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);
        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item);

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

        //TODO action read

        return switch (placeable) {
            case 1 -> new CustomPlaceableItem(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), itemStacks);
            default -> new CustomUnplaceableItem(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), itemStacks);
        };
    }

    @Override
    public void save(SlimefunItem slimefunItem) {

    }
}
