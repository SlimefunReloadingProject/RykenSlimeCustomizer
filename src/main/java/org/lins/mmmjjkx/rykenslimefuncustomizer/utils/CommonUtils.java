package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.RSCItemStack;

public class CommonUtils {
    private static final Map<String, String> materialMappings = Map.of(
            "GRASS", "SHORT_GRASS",
            "SHORT_GRASS", "GRASS",
            "SCUTE", "TURTLE_SCUTE",
            "TURTLE_SCUTE", "SCUTE"
    );

    public static ItemStack doGlow(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.LUCK, 1);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        return item;
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
    public static ItemStack[] readRecipe(ConfigurationSection section, @NotNull ProjectAddon addon, int size) {
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
            ExceptionHandler.handleError("你设置了材料类型，但没有设置对应的材料！");
            return null;
        }

        String material = section.getString("material", "");

        if (material.startsWith("ey") || material.startsWith("ew")) {
            type = "skull";
        } else if (material.startsWith("http") || material.startsWith("https")) {
            type = "skull_url";
        } else if (material.matches("^[0-9A-Fa-f]{64}+$")) {
            type = "skull_hash";
        }

        List<String> lore = CMIChatColor.translate(section.getStringList("lore"));
        String name = CMIChatColor.translate(section.getString("name", ""));
        boolean glow = section.getBoolean("glow", false);
        boolean hasEnchantment = section.contains("enchantments") && section.isList("enchantments");
        int modelId = section.getInt("modelId");
        int amount = section.getInt("amount", 1);

        ItemStack itemStack;

        switch (type.toLowerCase()) {
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
                SlimefunItemStack sfis = addon.getPreloadItems().get(material);
                if (sfis != null) {
                    itemStack = sfis.clone();
                    itemStack.editMeta(m -> {
                        if (!name.isBlank()) {
                            m.setDisplayName(name);
                        }

                        if (!lore.isEmpty()) {
                            m.setLore(lore);
                        }
                    });

                } else {
                    SlimefunItem sfItem = SlimefunItem.getById(material);
                    if (sfItem != null) {
                        itemStack = sfItem.getItem().clone();
                        itemStack.editMeta(m -> {
                            if (!name.isBlank()) {
                                m.setDisplayName(name);
                            }

                            if (!lore.isEmpty()) {
                                m.setLore(lore);
                            }
                        });
                    } else {
                        ExceptionHandler.handleError("无法找到粘液物品" + material + "，已转为石头");
                        itemStack = new CustomItemStack(Material.STONE, name, lore);
                    }
                }
            }
            case "saveditem" -> {
                File file = new File(addon.getSavedItemsFolder(), material + ".yml");
                if (!file.exists()) {
                    ExceptionHandler.handleError("保存物品的文件" + material + "不存在，已转为石头");
                    itemStack = new CustomItemStack(Material.STONE, name, lore);
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
                        String r2 = replace.replaceFirst(
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
                        configuration.getItemStack("item", new CustomItemStack(Material.STONE, name, lore)),
                        name,
                        lore);

                if (itemStack.getAmount() > 1 && !countable) {
                    itemStack.setAmount(1);
                }
            }
            //mc
            default -> {
                Optional<Material> materialOptional = Optional.ofNullable(Material.matchMaterial(material));
                Material mat = Material.STONE;

                if (materialOptional.isPresent()) {
                    mat = materialOptional.get();
                } else if (SlimefunItem.getById(material) == null && addon.getPreloadItems().get(material) == null) {
                    if (materialMappings.containsKey(material)) {
                        materialOptional = Optional.ofNullable(Material.matchMaterial(materialMappings.get(material)));
                        if (materialOptional.isPresent()) {
                            mat = materialOptional.get();
                            ExceptionHandler.handleWarning("材料" + material + "已自动修复为" + mat);
                        } else {
                            ExceptionHandler.handleError("无法在附属" + addon.getAddonId() + "中读取材料" + material + "，已转为石头");
                        }
                    } else {
                        ExceptionHandler.handleError("无法在附属" + addon.getAddonId() + "中读取材料" + material + "，已转为石头");
                    }
                }

                itemStack = new CustomItemStack(mat, name, lore);
            }
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (modelId > 0) {
            meta.setCustomModelData(modelId);
        }

        itemStack.setItemMeta(meta);

        if (countable) {
            if (amount > 64 || amount < -1) {
                ExceptionHandler.handleError(
                        "无法在附属" + addon.getAddonId() + "中读取" + section.getCurrentPath() + "的物品: 物品数量不能大于64或小于-1");
                return null;
            }
            itemStack.setAmount(amount);
        }

        if (hasEnchantment) {
            List<String> enchants = section.getStringList("enchantments");
            for (String enchant : enchants) {
                String[] s2 = enchant.split(" ");
                if (s2.length != 2) {
                    ExceptionHandler.handleError("无法在附属" + addon.getAddonId() + "中读取物品附魔" + enchant + ", 跳过添加此附魔");
                    continue;
                }

                String enchantName = s2[0];
                int lvl = Integer.parseInt(s2[1]);

                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName.toLowerCase()));
                if (enchantment == null) {
                    ExceptionHandler.handleError("无法在附属" + addon.getAddonId() + "中读取物品附魔" + enchant + ", 跳过添加此附魔");
                    continue;
                }

                itemStack.addUnsafeEnchantment(enchantment, lvl);
            }
        }

        return glow ? doGlow(itemStack) : itemStack;
    }

    @SuppressWarnings("deprecation")
    public static void addLore(ItemStack stack, boolean emptyLine, String... lore) {
        ItemMeta im = stack.getItemMeta();
        var lorel = im.getLore();
        if (lorel != null) {
            if (emptyLine) {
                lorel.add("");
            }
            lorel.addAll(CMIChatColor.translate(Arrays.asList(lore)));
        } else {
            lorel = CMIChatColor.translate(Arrays.asList(lore));
        }
        im.setLore(lorel);
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

    public static void completeFile(String resourceFile) {
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

            completeFile0(configuration, configuration2);
            configuration2.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleError("无法完成文件" + resourceFile + "的同步，请检查插件文件是否损坏！");
        }
    }

    public static void completeFile(YamlConfiguration origin, YamlConfiguration dest) {
        completeFile0(origin, dest);
    }

    private static void completeFile0(YamlConfiguration origin, YamlConfiguration dest) {
        for (String key : origin.getKeys(true)) {
            Object value = origin.get(key);
            if (value instanceof List<?>) {
                List<?> list2 = dest.getList(key);
                if (list2 == null) {
                    dest.set(key, value);
                    continue;
                }
            }

            if (!dest.contains(key)) {
                dest.set(key, value);
            }
        }
    }

    public static int versionToCode(String s) {
        String[] ver = s.split("\\.");
        String ver2 = "";
        for (String v : ver) {
            ver2 = ver2.concat(v);
        }

        if (ver.length == 2) {
            ver2 = ver2.concat("0");
        }

        return Integer.parseInt(ver2);
    }

    public static String formatSeconds(int seconds) {
        if (seconds < 60) {
            return "&b" + seconds + "&es";
        } else if (seconds > 60 && seconds < 3600) {
            int m = seconds / 60;
            int s = seconds % 60;
            return "&b" + m + "&emin" + (s != 0 ? "&b" + s + "&es" : "");
        } else {
            int h = seconds / 3600;
            int m = (seconds % 3600) / 60;
            int s = (seconds % 3600) % 60;
            return "&b" + h + "&eh" + (m != 0 ? "&b" + m + "&emin" : "") + (s != 0 ? "&b" + s + "&es" : "");
        }
    }

    public static ItemStack[] removeNulls(ItemStack[] origin) {
        int count = 0;
        for (ItemStack element : origin) {
            if (element != null) {
                count++;
            }
        }
        ItemStack[] newArray = new ItemStack[count];

        int index = 0;
        for (ItemStack element : origin) {
            if (element != null) {
                newArray[index] = element;
                index++;
            }
        }

        return newArray;
    }
}
