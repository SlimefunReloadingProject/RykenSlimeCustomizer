package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectionType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.CustomArmorPiece;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class ArmorReader extends YamlReader<CustomArmorPiece> {
    private final List<String> CHECKS = List.of("helmet", "chestplate", "leggings", "boots");

    public ArmorReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public CustomArmorPiece readEach(String s, ProjectAddon addon) {
        /*
         * Example of a custom armor piece:
         *
         * example_armor_piece:
         *   item_group: example_normal_group
         *   fullSet: false
         *   protection_types:
         *     - BEES
         *   helmet:
         *     id: LEATHER_HELMET
         *     recipe_type: ...
         *     recipe: ...
         *     ...universal item properties...
         *    potion_effects:
         *      - SPEED:1:30
         *   chestplate:
         *     id: LEATHER_CHESTPLATE
         *     ....
         *   leggings: ...
         *   boots: ...
         *
         */
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        boolean fullSet = section.getBoolean("fullSet", false);
        NamespacedKey key = new NamespacedKey(addon.getAddonId(), s);

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, section.getString("item_group", ""));
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;

        List<String> pt = section.getStringList("protection_types");
        List<ProtectionType> protectionTypes = new ArrayList<>();
        for (String type : pt) {
            Pair<ExceptionHandler.HandleResult, ProtectionType> result =
                    ExceptionHandler.handleEnumValueOf("错误的盔甲保护类型: " + type, ProtectionType.class, type);
            if (result.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
            protectionTypes.add(result.getSecondValue());
        }

        List<CustomArmorPiece> pieces = new ArrayList<>();
        for (String check : CHECKS) {
            ConfigurationSection pieceSection = section.getConfigurationSection(check);
            if (pieceSection == null) continue;
            String id = pieceSection.getString("id", "");

            ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);
            if (result == ExceptionHandler.HandleResult.FAILED) return null;


        }
    }
}
