package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import de.themoep.minedown.adventure.MineDown;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.Colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.RSCItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.XMaterial;

public class CommonUtils {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand().toBuilder().hexColors().build();

    public static <T extends ItemStack> T doGlow(T item) {
        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        return item;
    }

    public static Component parseToComponent(String text) {
        if (text == null) return Component.empty();

        text = text.replaceAll("§", "&");

        return MineDown.parse(text).decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> toComponents(String... texts) {
        List<Component> components = new ArrayList<>();

        if (texts == null) return components;

        for (String s : texts) {
            if (s != null) {
                components.add(parseToComponent(s));
            } else {
                components.add(Component.empty());
            }
        }

        return components;
    }

    public static List<Component> toComponents(List<String> texts) {
        List<Component> components = new ArrayList<>();

        if (texts == null) return components;

        for (String s : texts) {
            if (s != null && !s.isBlank()) {
                components.add(parseToComponent(s));
            } else {
                components.add(Component.empty());
            }
        }

        return components;
    }

    @Nullable public static <T> T getIf(Iterable<T> iterable, Predicate<T> filter) {
        if (iterable == null) return null;

        for (T t : iterable) {
            if (filter.test(t)) {
                return t;
            }
        }
        return null;
    }

    @NotNull public static ItemStack[] readRecipe(ConfigurationSection section, ProjectAddon addon) {
        return readRecipe(section, addon, 9);
    }

    @NotNull @Contract("null,_,_ -> new")
    public static ItemStack[] readRecipe(ConfigurationSection section, ProjectAddon addon, int size) {
        if (section == null) return new ItemStack[] {};
        ItemStack[] itemStacks = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            ConfigurationSection section1 = section.getConfigurationSection(String.valueOf(i + 1));
            itemStacks[i] = readItem(section1, true, addon);
        }
        return itemStacks;
    }

    @SneakyThrows
    @Nullable @SuppressWarnings("deprecation")
    public static ItemStack readItem(ConfigurationSection section, boolean countable, ProjectAddon addon) {
        if (section == null) {
            return null;
        }

        String type = section.getString("material_type", "mc");

        if (!type.equalsIgnoreCase("none") && !section.contains("material")) {
            ExceptionHandler.handleError("请先设置一个材料！");
            return null;
        }

        String material = section.getString("material", "");

        if (material.startsWith("ey")) {
            type = "skull";
        } else if (material.startsWith("http") || material.startsWith("https")) {
            type = "skull_url";
        } else if (material.matches("^[A-Za-z0-9]{64}+$")) {
            type = "skull_hash";
        }

        List<String> lore = CMIChatColor.translate(section.getStringList("lore"));
        String name = CMIChatColor.translate(section.getString("name", ""));
        boolean glow = section.getBoolean("glow", false);
        boolean hasEnchantment = section.contains("enchantments") && section.isConfigurationSection("enchantments");
        int modelId = section.getInt("modelId");
        int amount = section.getInt("amount", 1);

        ItemStack itemStack;

        switch (type.toLowerCase()) {
            default -> {
                Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(material);
                Material mat;
                if (xMaterial.isEmpty()) {
                    ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中读取材料" + material + "，已转为石头");
                    mat = Material.STONE;
                } else {
                    mat = xMaterial.get().parseMaterial();
                    if (mat == null) {
                        ExceptionHandler.handleError("无法在附属" + addon.getAddonName() + "中读取材料" + material + "，已转为石头");
                        mat = Material.STONE;
                    }
                }

                itemStack = new RSCItemStack(mat, name, lore);
            }
            case "none" -> {
                return new ItemStack(Material.AIR, 1);
            }
            case "skull_hash" -> {
                PlayerSkin playerSkin = PlayerSkin.fromHashCode(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = new RSCItemStack(head, name, lore);
            }
            case "skull_base64", "skull" -> {
                PlayerSkin playerSkin = PlayerSkin.fromBase64(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = new RSCItemStack(head, name, lore);
            }
            case "skull_url" -> {
                PlayerSkin playerSkin = PlayerSkin.fromURL(material);
                ItemStack head = PlayerHead.getItemStack(playerSkin);

                itemStack = new RSCItemStack(head, name, lore);
            }
            case "slimefun" -> {
                SlimefunItem sis = SlimefunItem.getById(material);
                if (sis != null) {
                    ItemStack is = sis.getItem();

                    itemStack = new RSCItemStack(is, name, lore);
                } else {
                    ExceptionHandler.handleError("无法找到粘液物品" + material + "，已转为石头");
                    itemStack = new RSCItemStack(Material.STONE, name, lore);
                }
            }
            case "saveditem" -> {
                File file = new File(addon.getSavedItemsFolder(), material + ".yml");
                if (!file.exists()) {
                    ExceptionHandler.handleError("保存物品的文件" + material + "不存在，已转为石头");
                    itemStack = new RSCItemStack(Material.STONE, name, lore);
                    break;
                }

                String fileContext = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                Pattern p = Pattern.compile("v: \\S\\d*");

                Matcher matcher = p.matcher(fileContext);
                if (matcher.find()) {
                    int s = matcher.start();
                    int e = matcher.end();
                    String replace = fileContext.substring(s, e);
                    int v = Integer.parseInt(replace.replace("v: ", ""));

                    if (v > Bukkit.getUnsafe().getDataVersion()) {
                        String r2 = replace.replace(
                                String.valueOf(v),
                                String.valueOf(Bukkit.getUnsafe().getDataVersion()));
                        fileContext = fileContext.replace(replace, r2);
                        Files.writeString(
                                file.toPath(),
                                fileContext,
                                StandardCharsets.UTF_8,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING);
                    }
                }

                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

                itemStack = new RSCItemStack(
                        configuration.getItemStack("item", new RSCItemStack(Material.STONE, name, lore)), name, lore);

                if (itemStack.getAmount() > 1 && !countable) {
                    itemStack.setAmount(1);
                }
            }
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (modelId > 0) {
            meta.setCustomModelData(modelId);
        }

        itemStack.setItemMeta(meta);

        if (countable) {
            if (amount > 64 || amount < 1) {
                ExceptionHandler.handleError(
                        "无法在附属" + addon.getAddonName() + "中读取" + section.getCurrentPath() + "的物品: 物品数量不能大于64或小于1");
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
                        ExceptionHandler.handleError(
                                "无法在附属" + addon.getAddonName() + "中读取物品附魔" + enchant + ", 跳过添加此附魔");
                        continue;
                    }
                    int lvl = enchants.getInt(enchant, 1);
                    itemStack.addUnsafeEnchantment(enchantment, lvl);
                }
            }
        }

        return glow ? doGlow(itemStack) : itemStack;
    }

    public static void addLore(ItemStack stack, boolean emptyLine, Component... lore) {
        ItemMeta im = stack.getItemMeta();
        var lorel = im.lore();
        if (lorel != null) {
            if (emptyLine) {
                lorel.add(Component.empty());
            }
            lorel.addAll(Arrays.asList(lore));
        } else {
            lorel = Arrays.asList(lore);
        }
        im.lore(lorel);
        stack.setItemMeta(im);
    }

    public static void saveItem(ItemStack item, String fileName, ProjectAddon addon) {
        File folder = addon.getSavedItemsFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, fileName + ".yml");
        if (!file.exists()) {
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
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

    public static boolean isArmor(Material material) {
        if (material == null) return false;

        final String typeNameString = material.toString();

        return typeNameString.endsWith("_HELMET")
                || typeNameString.endsWith("_CHESTPLATE")
                || typeNameString.endsWith("_LEGGINGS")
                || typeNameString.endsWith("_BOOTS");
    }

    public static void completeFile(String resourceFile, String... notNeedSyncKeys) {
        JavaPlugin plugin = RykenSlimefunCustomizer.INSTANCE;

        InputStream stream = plugin.getResource(resourceFile);
        File file = new File(plugin.getDataFolder(), resourceFile);
        if (!file.exists()) {
            if (stream != null) {
                plugin.saveResource(resourceFile, false);
                return;
            }
            return;
        }
        if (stream == null) {
            ExceptionHandler.handleError("无法找到文件" + resourceFile + "，请检查插件文件是否损坏！");
            return;
        }
        try {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            YamlConfiguration configuration2 = new YamlConfiguration();
            configuration2.load(file);

            for (String key : configuration.getKeys(true)) {
                Object value = configuration.get(key);
                if (value instanceof List<?>) {
                    List<?> list2 = configuration2.getList(key);
                    if (list2 == null) {
                        configuration2.set(key, value);
                        continue;
                    }
                }

                if (!configuration2.contains(key)) {
                    configuration2.set(key, value);
                }
                if (!configuration.getComments(key).equals(configuration2.getComments(key))) {
                    configuration2.setComments(key, configuration.getComments(key));
                }
                YamlConfigurationOptions options1 = configuration.options();
                YamlConfigurationOptions options2 = configuration2.options();

                if (!options2.getHeader().equals(options1.getHeader())) {
                    options2.setHeader(options1.getHeader());
                }
            }

            List<String> notSync = Arrays.stream(notNeedSyncKeys).toList();

            for (String key2 : configuration2.getKeys(true)) {
                boolean b = notSync.contains(key2);

                if (!configuration.contains(key2)) {
                    if (!b) {
                        configuration2.set(key2, null);
                    }
                }
            }

            configuration2.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleError("无法完成文件" + resourceFile + "的同步，请检查插件文件是否损坏！");
        }
    }
}
