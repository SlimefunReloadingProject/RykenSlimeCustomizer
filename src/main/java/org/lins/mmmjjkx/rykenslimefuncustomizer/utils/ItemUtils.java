package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {
    /**
     * @param itemStacks original item array
     * @return how many items there are in total
     */
    public static int getAllItemAmount(@Nonnull ItemStack... itemStacks) {
        int amount = 0;
        for (ItemStack itemStack : itemStacks) {
            if (itemStack == null || itemStack.getType().isAir()) {
                continue;
            }

            amount += itemStack.getAmount();
        }

        return amount;
    }

    /**
     * @param itemStacks original item array
     * @return how many kinds of item there are in total
     */
    public static int getAllItemTypeAmount(@Nonnull ItemStack... itemStacks) {
        Set<SlimefunItem> sfItems = new HashSet<>();
        Set<Material> materials = new HashSet<>();

        for (ItemStack itemStack : itemStacks) {

            if (itemStack == null || itemStack.getType().isAir()) {
                continue;
            }

            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
            if (sfItem != null) {
                sfItems.add(sfItem);
            } else {
                materials.add(itemStack.getType());
            }
        }

        return sfItems.size() + materials.size();
    }
}
