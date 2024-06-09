package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public record MachineTemplate(ItemStack item, int cost, List<Pair<CustomMachineRecipe, Integer>> recipes) {
    public boolean isSimilar(ItemStack item) {
        return SlimefunUtils.isItemSimilar(item, this.item, true);
    }
}
