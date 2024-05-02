package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.CustomItem;

public class CustomDefaultItem extends CustomItem {
    private final Object[] constructorArgs;

    public CustomDefaultItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        constructorArgs = new Object[] {itemGroup, item, recipeType, recipe};
    }

    @Override
    public Object[] constructorArgs() {
        return constructorArgs;
    }
}
