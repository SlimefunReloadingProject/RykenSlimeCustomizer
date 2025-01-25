package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectionType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomArmorPiece;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class ArmorReader extends YamlReader<List<CustomArmorPiece>> {
    private final List<String> CHECKS = List.of("helmet", "chestplate", "leggings", "boots");

    public ArmorReader(YamlConfiguration config, ProjectAddon addon) {
        super(config, addon);
    }

    @Override
    public List<CustomArmorPiece> readEach(String s) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        boolean fullSet = section.getBoolean("fullSet", false);

        Pair<ExceptionHandler.HandleResult, ItemGroup> group =
                ExceptionHandler.handleItemGroupGet(addon, section.getString("item_group", ""));
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        List<String> pt = section.getStringList("protection_types");
        List<ProtectionType> protectionTypes = new ArrayList<>();
        for (String type : pt) {
            Pair<ExceptionHandler.HandleResult, ProtectionType> result = ExceptionHandler.handleEnumValueOf(
                    "在附属" + addon.getAddonId() + "中加载盔甲套" + s + "时遇到了问题: " + "错误的盔甲保护类型: " + type,
                    ProtectionType.class,
                    type);
            if (result.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
            protectionTypes.add(result.getSecondValue());
        }

        List<CustomArmorPiece> pieces = new ArrayList<>();
        for (String check : CHECKS) {
            ConfigurationSection pieceSection = section.getConfigurationSection(check);
            if (pieceSection == null) continue;

            String pieceId = addon.getId(s + "_" + check.toUpperCase(), section.getString("id_alias", pieceSection.getString("id", "")));

            ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(pieceId);
            if (result == ExceptionHandler.HandleResult.FAILED) return null;

            String recipeType = pieceSection.getString("recipe_type", "NULL");

            Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                    "在附属" + addon.getAddonId() + "中加载盔甲套" + s + "的" + check + "时遇到了问题: " + "错误的配方类型" + recipeType + "!",
                    recipeType);
            if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

            SlimefunItemStack sfis = getPreloadItem(pieceId);
            if (sfis == null) return null;

            ConfigurationSection recipeSection = pieceSection.getConfigurationSection("recipe");
            ItemStack[] recipe = CommonUtils.readRecipe(recipeSection, addon);

            List<PotionEffect> potionEffects = new ArrayList<>();
            List<String> effects = pieceSection.getStringList("potion_effects");

            for (String effect : effects) {
                String[] split = effect.split(" ");
                if (split.length != 2) {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载盔甲套" + s + "的" + check + "时遇到了问题: "
                            + "错误的药水效果格式: " + effect);
                    return null;
                }
                String effectName = split[0];
                int amplifier = Integer.parseInt(split[1]);

                PotionEffectType type = PotionEffectType.getByName(effectName);
                if (type == null) {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载盔甲套" + s + "的" + check + "时遇到了问题: "
                            + "错误的药水效果类型: " + effectName);
                    return null;
                }

                if (amplifier < 0) {
                    ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载盔甲套" + s + "的" + check + "时遇到了问题: "
                            + "药水效果等级不能为负数: " + effect + "， 但你设置了" + amplifier);
                    return null;
                }

                potionEffects.add(new PotionEffect(
                        type, (Slimefun.getCfg().getInt("options.armor-update-interval") + 3) * 20, amplifier));
            }

            pieces.add(new CustomArmorPiece(
                    group.getSecondValue(),
                    sfis,
                    rt.getSecondValue(),
                    recipe,
                    potionEffects.toArray(new PotionEffect[] {}),
                    fullSet,
                    s,
                    protectionTypes.toArray(new ProtectionType[] {}),
                    addon.getAddonId()));
        }

        if (pieces.isEmpty()) {
            ExceptionHandler.handleError("在附属" + addon.getAddonId() + "中加载盔甲套" + s + "时遇到了问题: " + "没有找到任何盔甲部分");
            return null;
        }

        return pieces;
    }

    @Override
    public List<SlimefunItemStack> preloadItems(String s) {
        List<SlimefunItemStack> items = new ArrayList<>(4);
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        for (String check : CHECKS) {
            ConfigurationSection pieceSection = section.getConfigurationSection(check);
            if (pieceSection == null) continue;

            ItemStack stack = CommonUtils.readItem(pieceSection, false, addon);
            if (stack == null) {
                ExceptionHandler.handleError(
                        "在附属" + addon.getAddonId() + "中加载盔甲套" + s + "的" + check + "时遇到了问题: " + "物品为空或格式错误导致无法加载，已跳过");
                continue;
            }

            SlimefunItemStack sfis = new SlimefunItemStack(addon.getId(s + "_" + check.toUpperCase(), section.getString("id_alias", pieceSection.getString("id", ""))), stack);
            items.add(sfis);
        }

        return items;
    }
}
