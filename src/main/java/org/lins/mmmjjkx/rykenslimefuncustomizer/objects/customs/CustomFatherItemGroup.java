package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

public class CustomFatherItemGroup extends NestedItemGroup {
    public CustomFatherItemGroup(String id, ItemStack item) {
        super(new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, id), item);
    }
}
