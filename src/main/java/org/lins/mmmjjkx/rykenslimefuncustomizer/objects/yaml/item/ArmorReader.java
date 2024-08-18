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
                    "Found an error while loading armor set " + s + " in addon " + addon.getAddonId()
                            + ": Invalid protection type '" + type + "'!",
                    ProtectionType.class,
                    type);
            if (result.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
            protectionTypes.add(result.getSecondValue());
        }

        List<CustomArmorPiece> pieces = new ArrayList<>();
        for (String check : CHECKS) {
            ConfigurationSection pieceSection = section.getConfigurationSection(check);
            if (pieceSection == null) continue;

            String pieceId = pieceSection.getString("id_alias", pieceSection.getString("id", ""));

            ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);
            if (result == ExceptionHandler.HandleResult.FAILED) return null;

            String recipeType = pieceSection.getString("recipe_type", "NULL");

            Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.getRecipeType(
                    "Found an error while loading the armor set " + s + " in addon " + addon.getAddonId()
                            + ": Invalid recipe type '" + recipeType + "'!",
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
                    ExceptionHandler.handleError("Found an error while loading the armor set " + s + "in addon "
                            + addon.getAddonId() + ": Invalid potion effect format '" + effect + "'!");
                    return null;
                }
                String effectName = split[0];
                int amplifier = Integer.parseInt(split[1]);

                PotionEffectType type = PotionEffectType.getByName(effectName);
                if (type == null) {
                    ExceptionHandler.handleError("Found an error while loading the armor set " + s + "in addon "
                            + addon.getAddonId() + ": Invalid potion effect type '" + effectName + "'!");
                    return null;
                }

                if (amplifier < 0) {
                    ExceptionHandler.handleError("Found an error while loading the armor set " + s + "in addon "
                            + addon.getAddonId() + ": Potion effect amplifier cannot be negative! But you entered: '"
                            + amplifier + "'!");
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
            ExceptionHandler.handleError("Found an error while loading the " + s + " armor set of the "
                    + addon.getAddonId() + " addon: No armor parts found");
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

            String id = pieceSection.getString("id_alias", pieceSection.getString("id", ""));

            ItemStack stack = CommonUtils.readItem(pieceSection, false, addon);
            if (stack == null) {
                ExceptionHandler.handleError("Found an error while loading the " + check + "  in the " + s
                        + " armor set of the " + addon.getAddonId()
                        + " addon: The item is null or has an invalid format, skipping it");
                continue;
            }

            SlimefunItemStack sfis = new SlimefunItemStack(id, stack);
            items.add(sfis);
        }

        return items;
    }
}
