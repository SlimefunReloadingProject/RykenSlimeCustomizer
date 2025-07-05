package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import java.util.List;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.inventory.ItemStack;

public record MachineTemplate(ItemStack template, List<CustomMachineRecipe> recipes) {
    public boolean isItemSimilar(ItemStack item) {
        return SlimefunUtils.isItemSimilar(item, template, true, true, true);
    }
}
