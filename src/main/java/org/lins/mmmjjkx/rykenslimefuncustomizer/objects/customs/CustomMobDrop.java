package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RandomMobDrop;
import org.bukkit.inventory.ItemStack;

public class CustomMobDrop extends CustomUnplaceableItem implements RandomMobDrop {
    private final int chance;

    public CustomMobDrop(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe, int chance) {
        super(itemGroup, item, RecipeType.MOB_DROP, recipe, null);
        this.chance = chance;
    }

    @Override
    public int getMobDropChance() {
        return chance >= 100 ? 100 : Math.max(chance, 1);
    }
}
