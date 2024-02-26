package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item;

import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RSCItemStack extends CustomItemStack {
    public RSCItemStack(ItemStack item, Consumer<ItemMeta> meta) {
        super(item, meta);
    }

    public RSCItemStack(ItemStack item, int amount) {
        super(item, amount);
    }

    public RSCItemStack(ItemStack item, String name, String... lore) {
        super(item, meta -> {
            meta.displayName(CommonUtils.parseToComponent(name));
            meta.lore(Arrays.stream(lore).map(CommonUtils::parseToComponent).toList());
        });
    }

    public RSCItemStack(Material type, String name, String... lore) {
        super(type, meta -> {
            meta.displayName(CommonUtils.parseToComponent(name));
            meta.lore(Arrays.stream(lore).map(CommonUtils::parseToComponent).toList());
        });
    }

    public RSCItemStack(Material type, String name, List<String> lore) {
        super(type, meta -> {
            meta.displayName(CommonUtils.parseToComponent(name));
            meta.lore(lore.stream().map(CommonUtils::parseToComponent).toList());
        });
    }
}
