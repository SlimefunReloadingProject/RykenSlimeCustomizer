package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item;

import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.List;

public class RSCItemStack extends CustomItemStack {

    public RSCItemStack(ItemStack item, int amount) {
        super(item, amount);
    }

    public RSCItemStack(ItemStack item, String name, String... lore) {
        super(item, meta -> {
            if (name != null && !name.isBlank()) {
                meta.displayName(CommonUtils.parseToComponent(name));
            }

            if (lore != null && lore.length > 0) {
                meta.lore(CommonUtils.toComponents(lore));
            }
        });
    }

    public RSCItemStack(Material type, String name, String... lore) {
        super(type, meta -> {
            if (name != null && !name.isBlank()) {
                meta.displayName(CommonUtils.parseToComponent(name));
            }

            if (lore != null && lore.length > 0) {
                meta.lore(CommonUtils.toComponents(lore));
            }
        });
    }

    public RSCItemStack(Material type, String name, List<String> lore) {
        super(type, meta -> {
            if (name != null && !name.isBlank()) {
                meta.displayName(CommonUtils.parseToComponent(name));
            }

            if (lore != null && !lore.isEmpty()) {
                meta.lore(CommonUtils.toComponents(lore));
            }
        });
    }

    public RSCItemStack(ItemStack item, String name, List<String> lore) {
        super(item, meta -> {
            if (name != null && !name.isBlank()) {
                meta.displayName(CommonUtils.parseToComponent(name));
            }

            if (lore != null && !lore.isEmpty()) {
                meta.lore(CommonUtils.toComponents(lore));
            }
        });
    }
}
