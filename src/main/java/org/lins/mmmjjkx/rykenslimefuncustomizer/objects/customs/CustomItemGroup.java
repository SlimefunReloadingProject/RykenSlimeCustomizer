package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

public class CustomItemGroup extends ItemGroup {
    public CustomItemGroup(String id, ItemStack item, boolean addable) {
        super(new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, id), item);
    }
}
