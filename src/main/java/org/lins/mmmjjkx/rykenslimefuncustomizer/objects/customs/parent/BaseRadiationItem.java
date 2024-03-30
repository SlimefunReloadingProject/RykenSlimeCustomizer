package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import io.github.thebusybiscuit.slimefun4.implementation.items.RadioactiveItem;
import org.bukkit.inventory.ItemStack;

public abstract class BaseRadiationItem extends RadioactiveItem {
    public BaseRadiationItem(ItemGroup itemGroup, Radioactivity radioactivity, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, radioactivity, item, recipeType, recipe);
    }

    public abstract Object[] constructArgs();
}
