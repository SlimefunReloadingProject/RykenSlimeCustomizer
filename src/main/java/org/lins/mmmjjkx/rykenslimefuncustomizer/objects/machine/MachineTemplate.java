package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record MachineTemplate(ItemStack template, List<CustomTemplateMachineRecipe> recipes) {
    public boolean isItemSimilar(ItemStack item) {
        return SlimefunUtils.isItemSimilar(item, template, true);
    }
}
