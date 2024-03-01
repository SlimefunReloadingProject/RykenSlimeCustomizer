package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.RSCItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
    
    public static Component parseToComponent(String text) {
        if (text == null) return Component.empty();

        Component legacy1 = LegacyComponentSerializer.legacySection().deserialize(text);
        Component legacy2 = LEGACY_SERIALIZER.deserialize(LEGACY_SERIALIZER.serialize(legacy1));

        return MINI_MESSAGE.deserialize(MINI_MESSAGE.serialize(legacy2)).decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> toComponents(String... texts) {
        List<Component> components = new ArrayList<>();

        if (texts == null) return components;

        for (String s : texts) {
            if (s != null) {
                components.add(parseToComponent(s));
            } else {
                components.add(Component.newline());
            }
        }

        return components;
    }

    public static List<Component> toComponents(List<String> texts) {
        List<Component> components = new ArrayList<>();

        if (texts == null) return components;

        for (String s : texts) {
            if (s != null) {
                components.add(parseToComponent(s));
            } else {
                components.add(Component.newline());
            }
        }

        return components;
    }

    @Nullable
    public static <T> T getIf(Iterable<T> iterable, Predicate<T> filter) {
        if (iterable == null) return null;

        for (T t : iterable) {
            if (filter.test(t)) {
                return t;
            }
        }
        return null;
    }

    @NotNull
    public static ItemStack[] readRecipe(ConfigurationSection section, ProjectAddon addon) {
        return readRecipe(section, addon, 9);
    }

    @NotNull
    @Contract("null,_,_ -> new")
    public static ItemStack[] readRecipe(ConfigurationSection section, ProjectAddon addon, int size) {
        if (section == null) return new ItemStack[]{};
        ItemStack[] itemStacks = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            ConfigurationSection section1 = section.getConfigurationSection(String.valueOf(i + 1));
            itemStacks[i] = readItem(section1, true, addon);
        }
        return itemStacks;
    }

    @SneakyThrows
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

        if (material.startsWith("ey")) {
            type = "skull";
        } else if (material.startsWith("http") || material.startsWith("https")) {
            type = "skull_url";
        } else if (material.matches("^[A-Za-z0-9]{64}+$")) {
            type = "skull_hash";
        }

        List<String> lore = section.getStringList("lore");
        String name = section.getString("name");
        boolean glow = section.getBoolean("glow", false);
        boolean hasEnchantment = section.contains("enchantments") && section.isConfigurationSection("enchantments");
        int modelId = section.getInt("modelId");
        int amount = section.getInt("amount", 1);

        ItemStack itemStack;

        switch (type.toLowerCase()) {
            default -> {
                Material mat;
                Pair<ExceptionHandler.HandleResult, Material> result =
                        ExceptionHandler.handleEnumValueOf("无法在附属"+addon.getAddonName()+"中读取材料"+material+"错误，已转为石头", Material.class, material);
                if (result.getFirstValue() == ExceptionHandler.HandleResult.FAILED) {
                    mat = Material.STONE;
                } else {
                    if (result.getSecondValue() == null) {
                        //It shouldn't happen
                        mat = Material.STONE;
                    } else mat = result.getSecondValue();
                }

                itemStack = new RSCItemStack(mat, name, lore);
            }
            case "none" -> {
                return new ItemStack(Material.AIR, 1);
            }
            case "skull_hash" -> {
                PlayerSkin playerSkin = PlayerSkin.fromHashCode(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = new RSCItemStack(head, name, lore.toArray(new String[]{}));
            }
            case "skull_base64","skull" -> {
                PlayerSkin playerSkin = PlayerSkin.fromBase64(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = new RSCItemStack(head, name, lore.toArray(new String[]{}));
            }
            case "skull_url" -> {
                PlayerSkin playerSkin = PlayerSkin.fromURL(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = new RSCItemStack(head, name, lore.toArray(new String[]{}));
            }
            case "slimefun" -> {
                SlimefunItem sis = SlimefunItem.getById(material);
                if (sis != null) {
                    ItemStack is = sis.getItem();

                    itemStack = new RSCItemStack(is, name, lore.toArray(new String[]{}));
                    break;
                }
                ExceptionHandler.handleError("无法找到粘液物品"+material+"，已转为石头");
                itemStack = new RSCItemStack(Material.STONE, name);
            }
            case "full_slimefun" -> {
                SlimefunItem sis = SlimefunItem.getById(material);
                if (sis != null) {
                    ItemStack sfis = sis.getItem().clone();
                    return countable ? new RSCItemStack(sfis, amount) : sis.getItem();
                }
                ExceptionHandler.handleError("无法找到粘液物品"+material+"，已转为石头");
                itemStack = new RSCItemStack(Material.STONE, name);
            }
            case "saveditem" -> {
                File file = new File(addon.getSavedItemsFolder(), material + ".yml");
                if (!file.exists()) {
                    ExceptionHandler.handleError("保存物品的文件"+material+"不存在，已转为石头");
                    itemStack = new RSCItemStack(Material.STONE, name, lore);
                    break;
                }

                YamlConfiguration item = YamlConfiguration.loadConfiguration(file);

                if (item.getInt("item.v") > Bukkit.getUnsafe().getDataVersion()) {
                    item.set("item.v", Bukkit.getUnsafe().getDataVersion());
                }

                itemStack = new RSCItemStack(item.getItemStack("item", new RSCItemStack(Material.STONE, name, lore)), name, lore);
            }
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (modelId > 0) {
            meta.setCustomModelData(modelId);
        }

        itemStack.setItemMeta(meta);

        if (countable) {
            if (amount > 64 || amount < 1) {
                ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中读取"+section.getCurrentPath()+"的物品: 物品数量不能大于64或小于1");
                return null;
            }
            itemStack.setAmount(amount);
        }

        if (hasEnchantment) {
            ConfigurationSection enchants = section.getConfigurationSection("enchantments");
            if (enchants != null) {
                for (String enchant : enchants.getKeys(false)) {
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchant.toLowerCase()));
                    if (enchantment == null) {
                        ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中读取物品附魔"+enchant+", 跳过添加此附魔");
                        continue;
                    }
                    int lvl = enchants.getInt(enchant, 1);
                    itemStack.addUnsafeEnchantment(enchantment, lvl);
                }
            }
        }

        return glow ? doGlow(itemStack) : itemStack;
    }

    public static void saveItem(ItemStack item, String fileName, ProjectAddon addon) {
        File folder = addon.getSavedItemsFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, fileName + ".yml");
        if (!file.exists()) {
            try {file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        YamlConfiguration configuration = new YamlConfiguration();

        configuration.set("item", item);

        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}