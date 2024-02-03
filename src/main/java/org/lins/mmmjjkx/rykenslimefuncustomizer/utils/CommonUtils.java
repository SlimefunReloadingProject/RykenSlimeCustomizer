package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    @SuppressWarnings("unused")
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
    public static ItemStack[] readRecipe(ConfigurationSection section, ProjectAddon addon) {
        ItemStack[] itemStacks = new ItemStack[9];
        for (int i = 0; i < 9; i ++) {
            ConfigurationSection section1 = section.getConfigurationSection(String.valueOf(i + 1));
            itemStacks[i] = readItem(section1, true, addon);
        }
        return itemStacks;
    }

    @Nullable
    public static ItemStack readItem(ConfigurationSection section, boolean countable, ProjectAddon addon) {
        if (section == null) {
            return null;
        }

        String type = section.getString("material_type", "mc");

        if (!type.equalsIgnoreCase("none") && !section.contains("material")) {
            ExceptionHandler.handleError("请先设置一个材料！");
            return null;
        }

        String material = section.getString("material","");
        List<String> lore = section.getStringList("lore");
        String name = section.getString("name");
        boolean glow = section.getBoolean("glow", false);
        boolean placeable = section.getBoolean("placeable", false);
        int modelId = section.getInt("modelId");
        int amount = section.getInt("amount", 1);

        ItemStack itemStack;

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

                itemStack = new CustomItemStack(mat, name, lore);
            }
            case "none" -> {
                return new ItemStack(Material.AIR, 1);
            }
            case "skull_base64","skull" -> {
                PlayerSkin playerSkin = PlayerSkin.fromBase64(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = new CustomItemStack(head, name, lore.toArray(new String[]{}));
            }
            case "skull_url" -> {
                PlayerSkin playerSkin = PlayerSkin.fromURL(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = new CustomItemStack(head, name, lore.toArray(new String[]{}));
            }
            case "slimefun" -> {
                SlimefunItem sis = SlimefunItem.getById(material);
                if (sis != null) {
                    ItemStack is = sis.getItem();

                    itemStack = new CustomItemStack(is, name, lore.toArray(new String[]{}));
                    break;
                }
                ExceptionHandler.handleError("无法找到粘液物品"+material+"，已转为石头");
                itemStack = new CustomItemStack(Material.STONE, name);
            }
            case "full_slimefun" -> {
                SlimefunItem sis = SlimefunItem.getById(material);
                if (sis != null) {
                    return sis.getItem();
                }
                ExceptionHandler.handleError("无法找到粘液物品"+material+"，已转为石头");
                itemStack = new CustomItemStack(Material.STONE, name);
            }
            case "saveditem" -> {
                File file = new File(addon.getSavedItemsFolder(), material + ".yml");
                if (!file.exists()) {
                    ExceptionHandler.handleError("保存物品的文件"+material+"不存在，已转为石头");
                    itemStack = new CustomItemStack(Material.STONE, name);
                    break;
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                return readItem(configuration, countable, addon);
            }
        }

        ItemMeta meta = itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(PLACEABLE, PersistentDataType.INTEGER, placeable ? 1 : 0);
        if (modelId > 0) {
            meta.setCustomModelData(modelId);
        }
        itemStack.setItemMeta(meta);

        if (countable) {
            itemStack.setAmount(amount);
        }

        return glow ? doGlow(itemStack) : itemStack;
    }

    @SuppressWarnings("deprecation")
    public static void saveItem(ItemStack item, String fileName, ProjectAddon addon) {
        File file = new File(addon.getSavedItemsFolder(), fileName + ".yml");
        if (!file.exists()) {
            try {file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        ItemStack stack = item.clone();
        ItemMeta meta = stack.getItemMeta();
        Material material = stack.getType();
        if (meta.getLore() != null) {
            configuration.set("lore", meta.getLore());
        }
        if (meta.hasCustomModelData()) {
            configuration.set("modelId", meta.getCustomModelData());
        }
        if (meta.hasDisplayName()) {
            String name = meta.getDisplayName().replaceAll("§", "&");
            configuration.set("name", name);
        }
        if (isGlowing(item)) {
            configuration.set("glow", true);
        }

        configuration.set("amount", stack.getAmount());
        configuration.set("placeable", material.isBlock());

        SlimefunItem sfi = SlimefunItem.getByItem(item);
        boolean full_sfi = false;
        if (sfi != null) {
            ItemMeta sfiMeta = sfi.getItem().getItemMeta().clone();
            if (meta.getLore() != null && sfiMeta.getLore() != null) {
                full_sfi = meta.getLore().equals(sfiMeta.getLore());
            }

            if (meta.hasDisplayName() && sfiMeta.hasDisplayName()) {
                full_sfi = colorTranslateBack(meta.getDisplayName()).equals(colorTranslateBack(sfiMeta.getDisplayName()));
            }

            configuration.set("material_type", full_sfi ? "full_slimefun" : "slimefun");
            configuration.set("material", sfi.getId());
        } else if (material == Material.PLAYER_HEAD) {
            SkullMeta skull = (SkullMeta) meta;
            PlayerProfile owner = skull.getPlayerProfile();
            if (owner != null) {
                URL skin = owner.getTextures().getSkin();
                if (skin != null) {
                    configuration.set("material_type", "skull_url");
                    configuration.set("material", skin.toString());
                }
            }
        } else {
            configuration.set("material", material.toString());
        }
    }

    private static String colorTranslateBack(String s) {
        return s.replaceAll("§", "&");
    }
}