package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

import java.util.List;
import java.util.function.Predicate;

public class CommonUtils {
    public static final NamespacedKey PLACEABLE = new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, "placeable");
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

    @Nullable
    public static <T> T getIf(Iterable<T> iterable, Predicate<T> filter) {
        for (T t : iterable) {
            if (filter.test(t)) {
                return t;
            }
        }
        return null;
    }

    @NotNull
    public static ItemStack[] readRecipe(ConfigurationSection section) {
        ItemStack[] itemStacks = new ItemStack[9];
        for (int i = 0; i < 9; i ++) {
            ConfigurationSection section1 = section.getConfigurationSection(String.valueOf(i + 1));
            itemStacks[i] = readItem(section1);
        }
        return itemStacks;
    }

    @Nullable
    public static ItemStack readItem(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        if (!section.contains("material")) {
            ExceptionHandler.handleError("请先设置一个材料！");
            return null;
        }

        String type = section.getString("material_type", "mc");
        String material = section.getString("material","");
        int amount = section.getInt("amount", 1);
        List<String> lore = section.getStringList("lore");
        String name = section.getString("name");
        boolean glow = section.getBoolean("glow", false);
        boolean placeable = section.getBoolean("placeable", false);

        CustomItemStack itemStack;

        switch (type.toLowerCase()) {
            default -> {
                Material mat;
                Pair<ExceptionHandler.HandleResult, Material> result =
                        ExceptionHandler.handleEnumValueOf("物品类型"+material+"错误，已转为石头", "", Material.class, material);
                if (result.getFirstValue() == ExceptionHandler.HandleResult.FAILED) {
                    mat = Material.STONE;
                } else {
                    if (result.getSecondValue() == null) {
                        //It shouldn't happen
                        mat = Material.STONE;
                    } else mat = result.getSecondValue();
                }
                CustomItemStack cis = new CustomItemStack(mat, name, lore);
                cis.setAmount(amount);

                itemStack = cis;
            }
            case "none" -> {
                return new ItemStack(Material.AIR, 1);
            }
            case "skull_base64","skull" -> {
                PlayerSkin playerSkin = PlayerSkin.fromBase64(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);
                CustomItemStack cis = new CustomItemStack(head, name, lore.toArray(new String[]{}));
                cis.setAmount(amount);

                itemStack = cis;
            }
            case "skull_url" -> {
                PlayerSkin playerSkin = PlayerSkin.fromURL(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);
                CustomItemStack cis = new CustomItemStack(head, name, lore.toArray(new String[]{}));
                cis.setAmount(amount);

                itemStack = cis;
            }
            case "slimefun" -> {
                SlimefunItem sis = SlimefunItem.getById(material);
                if (sis != null) {
                    ItemStack is = sis.getItem();
                    CustomItemStack cis = new CustomItemStack(is, name, lore.toArray(new String[]{}));
                    cis.setAmount(amount);

                    itemStack = cis;
                    break;
                }
                CustomItemStack cis2 = new CustomItemStack(Material.STONE, name);
                cis2.setAmount(amount);
                itemStack = cis2;
            }
        }

        ItemMeta meta = itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(PLACEABLE, PersistentDataType.INTEGER, placeable ? 1 : 0);
        itemStack.setItemMeta(meta);

        return glow ? doGlow(itemStack) : itemStack;
    }
}