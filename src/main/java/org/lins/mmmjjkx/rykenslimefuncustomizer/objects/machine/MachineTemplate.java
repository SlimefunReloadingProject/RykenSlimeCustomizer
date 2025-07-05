package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.StackUtils;

public record MachineTemplate(ItemStack template, List<CustomMachineRecipe> recipes) {
    public boolean isItemSimilar(ItemStack item) {
        return StackUtils.itemsMatch(item, template, true, true, true);
    }
}
