package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectionType;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectiveArmor;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.SlimefunArmorPiece;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

public class CustomArmorPiece extends SlimefunArmorPiece implements ProtectiveArmor {
    private final String armorKey;
    private final boolean fullSet;
    private final ProtectionType[] protectionTypes;
    private final String projectId;

    public CustomArmorPiece(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            @Nullable PotionEffect[] effects,
            boolean fullSet,
            String armorKey,
            ProtectionType[] protectionTypes,
            String projectId) {
        super(itemGroup, item, recipeType, recipe, effects);

        this.armorKey = armorKey;
        this.fullSet = fullSet;
        this.protectionTypes = protectionTypes;
        this.projectId = projectId;

        register(RykenSlimefunCustomizer.INSTANCE);
    }

    @Override
    public @NotNull ProtectionType @NotNull [] getProtectionTypes() {
        return protectionTypes;
    }

    @Override
    public boolean isFullSetRequired() {
        return fullSet;
    }

    @Nullable @Override
    public NamespacedKey getArmorSetId() {
        return new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, projectId + "_" + armorKey);
    }
}
