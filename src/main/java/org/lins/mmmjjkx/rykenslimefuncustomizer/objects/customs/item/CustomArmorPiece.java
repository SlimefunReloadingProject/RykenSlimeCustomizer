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

public class CustomArmorPiece extends SlimefunArmorPiece implements ProtectiveArmor {
    public CustomArmorPiece(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                            @Nullable PotionEffect[] effects, boolean fullSet, String armorKey) {
        super(itemGroup, item, recipeType, recipe, effects);
    }

    @Override
    public @NotNull ProtectionType[] getProtectionTypes() {
        return new ProtectionType[0];
    }

    @Override
    public boolean isFullSetRequired() {
        return false;
    }

    @Nullable
    @Override
    public NamespacedKey getArmorSetId() {
        return null;
    }
}
