package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomPlaceableItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomUnplaceableItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.record.CommandOperation;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

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

        CommandOperationReader cor = new CommandOperationReader(configuration);
        CommandOperation co = cor.readEach(s, addon);

        CustomItem sfi;
        if (placeable == 1) {
            sfi = new CustomPlaceableItem(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), itemStacks);
        } else {
            sfi = new CustomUnplaceableItem(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), itemStacks);
        }
        sfi.addOperation(co);
        sfi.register(RykenSlimefunCustomizer.INSTANCE);
        ExceptionHandler.handleItemGroupAddItem(addon, igId, sfi);
        return sfi;
    }
}
