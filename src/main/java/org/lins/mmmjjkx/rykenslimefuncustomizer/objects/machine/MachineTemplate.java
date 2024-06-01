package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.List;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.inventory.ItemStack;

public record MachineTemplate(ItemStack item, int cost, List<Pair<RecipeMachineRecipe, Integer>> recipes) {
    public boolean isSimilar(ItemStack item) {
        return SlimefunUtils.isItemSimilar(item, this.item, true);
    }
}
