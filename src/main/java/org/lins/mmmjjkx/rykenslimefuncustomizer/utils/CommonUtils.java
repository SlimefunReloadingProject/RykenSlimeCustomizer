package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

public class CommonUtils {
    private static final NamespacedKey GLOW = new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, "item_glow");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder().build();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    public static <T extends ItemStack> T doGlow(T item) {
        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(GLOW, PersistentDataType.INTEGER, 1);

        item.setItemMeta(meta);

        return item;
    }


    public static boolean isGlowing(ItemStack item) {
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(GLOW, PersistentDataType.INTEGER);
    }

    public static <T extends ItemStack> T unGlow(T item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.remove(GLOW);

        item.removeEnchantment(Enchantment.ARROW_INFINITE);
        item.removeItemFlags(ItemFlag.HIDE_ENCHANTS);

        return item;
    }

    public static Component parseToComponent(String text) {
        Component component = LEGACY_SERIALIZER.deserialize(text);
        String middle = MINI_MESSAGE.serialize(component);
        return MINI_MESSAGE.deserialize(middle);
    }
}