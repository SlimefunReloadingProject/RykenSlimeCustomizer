package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item;

import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class RSCItemStack extends CustomItemStack {
    public RSCItemStack(Material type, String name, List<String> lore) {
        super(type, meta -> {
            if (name != null && !name.isBlank()) {
                meta.setDisplayName(name);
            }

            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }
        });
    }

    public RSCItemStack(ItemStack item, String name, List<String> lore) {
        super(item, meta -> {
            if (name != null && !name.isBlank()) {
                meta.setDisplayName(name);
            }

            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }
        });
    }
}
