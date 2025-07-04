package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

@UtilityClass
@SuppressWarnings("deprecation")
public class StackUtils {
    public static boolean itemsMatch(@Nonnull ItemStack cache, @Nullable ItemStack itemStack) {
        return itemsMatch(cache, itemStack, false, false, false);
    }

    public static boolean itemsMatch(@Nonnull ItemStack cache, @Nullable ItemStack itemStack, boolean checkLore) {
        return itemsMatch(cache, itemStack, checkLore, false, false);
    }

    public static boolean itemsMatch(
            @Nonnull ItemStack cache, @Nullable ItemStack itemStack, boolean checkLore, boolean checkAmount) {
        return itemsMatch(cache, itemStack, checkLore, checkAmount, false);
    }
    /**
     * Checks if items match each other, checks go in order from lightest to heaviest
     *
     * @param cache     The cached {@link ItemStack} to compare against
     * @param itemStack The {@link ItemStack} being evaluated
     * @return True if items match
     */
    public static boolean itemsMatch(
            @Nullable ItemStack cache,
            @Nullable ItemStack itemStack,
            boolean checkLore,
            boolean checkAmount,
            boolean checkCustomModelId) {
        // Null check
        if (cache == null || itemStack == null) {
            return itemStack == null;
        }

        // If types do not match, then the items cannot possibly match
        if (itemStack.getType() != cache.getType()) {
            return false;
        }

        if (Tag.SHULKER_BOXES.isTagged(itemStack.getType())) {
            return false;
        }

        if (itemStack.getType() == Material.BUNDLE) {
            return false;
        }

        // If amounts do not match, then the items cannot possibly match
        if (checkAmount && itemStack.getAmount() > cache.getAmount()) {
            return false;
        }

        // If either item does not have a meta then either a mismatch or both without meta = vanilla
        if (!itemStack.hasItemMeta() || !cache.hasItemMeta()) {
            return itemStack.hasItemMeta() == cache.hasItemMeta();
        }

        // Now we need to compare meta's directly - cache is already out, but let's fetch the 2nd meta also
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final ItemMeta cachedMeta = cache.getItemMeta();

        if (itemMeta == null || cachedMeta == null) {
            return itemMeta == cachedMeta;
        }

        // ItemMetas are different types and cannot match
        if (!itemMeta.getClass().equals(cachedMeta.getClass())) {
            return false;
        }

        // Quick meta-extension escapes
        if (canQuickEscapeMetaVariant(itemMeta, cachedMeta)) {
            return false;
        }

        // Has a display name (checking the name occurs later)
        if (itemMeta.hasDisplayName() != cachedMeta.hasDisplayName()) {
            return false;
        }

        if (checkCustomModelId) {
            // Custom model data is different, no match
            final boolean hasCustomOne = itemMeta.hasCustomModelData();
            final boolean hasCustomTwo = cachedMeta.hasCustomModelData();
            if (hasCustomOne) {
                if (!hasCustomTwo || itemMeta.getCustomModelData() != cachedMeta.getCustomModelData()) {
                    return false;
                }
            } else if (hasCustomTwo) {
                return false;
            }
        }

        // PDCs don't match
        if (!itemMeta.getPersistentDataContainer().equals(cachedMeta.getPersistentDataContainer())) {
            return false;
        }

        // Make sure enchantments match
        if (!itemMeta.getEnchants().equals(cachedMeta.getEnchants())) {
            return false;
        }

        // Check item flags
        if (!itemMeta.getItemFlags().equals(cachedMeta.getItemFlags())) {
            return false;
        }

        // Check the attribute modifiers
        final boolean hasAttributeOne = itemMeta.hasAttributeModifiers();
        final boolean hasAttributeTwo = cachedMeta.hasAttributeModifiers();
        if (hasAttributeOne) {
            if (!hasAttributeTwo
                    || !Objects.equals(itemMeta.getAttributeModifiers(), cachedMeta.getAttributeModifiers())) {
                return false;
            }
        } else if (hasAttributeTwo) {
            return false;
        }

        // Check the lore
        if (checkLore
                || itemStack.getType()
                        == Material.PLAYER_HEAD // Fix Soul jars in SoulJars & Number Components in MomoTech
                || itemStack.getType() == Material.SPAWNER // Fix Reinforced Spawner in Slimefun4
                || itemStack.getType() == Material.SUGAR // Fix Symbols in MomoTech
        ) {
            if (itemMeta.hasLore() && cachedMeta.hasLore()) {
                if (!Objects.equals(itemMeta.getLore(), cachedMeta.getLore())) {
                    return false;
                }
            } else if (itemMeta.hasLore() != cachedMeta.hasLore()) {
                return false;
            }
        }

        // Slimefun ID check no need to worry about distinction, covered in PDC + lore
        final Optional<String> optionalStackId1 = Slimefun.getItemDataService().getItemData(itemMeta);
        final Optional<String> optionalStackId2 = Slimefun.getItemDataService().getItemData(cachedMeta);
        if (optionalStackId1.isPresent() != optionalStackId2.isPresent()) {
            return false;
        }
        // Check the display name
        return optionalStackId1
                .map(s -> s.equals(optionalStackId2.get()))
                .orElseGet(() -> !itemMeta.hasDisplayName()
                        || Objects.equals(itemMeta.getDisplayName(), cachedMeta.getDisplayName()));
    }

    public static boolean canQuickEscapeMetaVariant(@Nonnull ItemMeta metaOne, @Nonnull ItemMeta metaTwo) {
        // Damageable (first as everything can be damageable apparently)
        if (metaOne instanceof Damageable instanceOne && metaTwo instanceof Damageable instanceTwo) {
            if (instanceOne.hasDamage() != instanceTwo.hasDamage()) {
                return true;
            }

            if (instanceOne.getDamage() != instanceTwo.getDamage()) {
                return true;
            }
        }

        if (metaOne instanceof Repairable instanceOne && metaTwo instanceof Repairable instanceTwo) {
            if (instanceOne.hasRepairCost() != instanceTwo.hasRepairCost()) {
                return true;
            }

            if (instanceOne.getRepairCost() != instanceTwo.getRepairCost()) {
                return true;
            }
        }

        // Axolotl
        if (metaOne instanceof AxolotlBucketMeta instanceOne && metaTwo instanceof AxolotlBucketMeta instanceTwo) {
            if (instanceOne.hasVariant() != instanceTwo.hasVariant()) {
                return true;
            }

            if (!instanceOne.hasVariant() || !instanceTwo.hasVariant()) {
                return true;
            }

            if (instanceOne.getVariant() != instanceTwo.getVariant()) {
                return true;
            }
        }

        // Banner
        if (metaOne instanceof BannerMeta instanceOne && metaTwo instanceof BannerMeta instanceTwo) {
            if (instanceOne.numberOfPatterns() != instanceTwo.numberOfPatterns()) {
                return true;
            }

            if (!instanceOne.getPatterns().equals(instanceTwo.getPatterns())) {
                return true;
            }
        }

        // BlockData
        if (metaOne instanceof BlockDataMeta instanceOne && metaTwo instanceof BlockDataMeta instanceTwo) {
            if (instanceOne.hasBlockData() != instanceTwo.hasBlockData()) {
                return true;
            }
        }

        // BlockState
        if (metaOne instanceof BlockStateMeta instanceOne && metaTwo instanceof BlockStateMeta instanceTwo) {
            if (instanceOne.hasBlockState() != instanceTwo.hasBlockState()) {
                return true;
            }

            if (!instanceOne.getBlockState().equals(instanceTwo.getBlockState())) {
                return true;
            }
        }

        // Books
        if (metaOne instanceof BookMeta instanceOne && metaTwo instanceof BookMeta instanceTwo) {
            if (instanceOne.getPageCount() != instanceTwo.getPageCount()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getAuthor(), instanceTwo.getAuthor())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getTitle(), instanceTwo.getTitle())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getGeneration(), instanceTwo.getGeneration())) {
                return true;
            }
        }

        // Bundle
        if (metaOne instanceof BundleMeta instanceOne && metaTwo instanceof BundleMeta instanceTwo) {
            if (instanceOne.hasItems() != instanceTwo.hasItems()) {
                return true;
            }
            if (!instanceOne.getItems().equals(instanceTwo.getItems())) {
                return true;
            }
        }

        // Compass
        if (metaOne instanceof CompassMeta instanceOne && metaTwo instanceof CompassMeta instanceTwo) {
            if (instanceOne.isLodestoneTracked() != instanceTwo.isLodestoneTracked()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getLodestone(), instanceTwo.getLodestone())) {
                return true;
            }
        }

        // Crossbow
        if (metaOne instanceof CrossbowMeta instanceOne && metaTwo instanceof CrossbowMeta instanceTwo) {
            if (instanceOne.hasChargedProjectiles() != instanceTwo.hasChargedProjectiles()) {
                return true;
            }
            if (!instanceOne.getChargedProjectiles().equals(instanceTwo.getChargedProjectiles())) {
                return true;
            }
        }

        // Enchantment Storage
        if (metaOne instanceof EnchantmentStorageMeta instanceOne
                && metaTwo instanceof EnchantmentStorageMeta instanceTwo) {
            if (instanceOne.hasStoredEnchants() != instanceTwo.hasStoredEnchants()) {
                return true;
            }
            if (!instanceOne.getStoredEnchants().equals(instanceTwo.getStoredEnchants())) {
                return true;
            }
        }

        // Firework Star
        if (metaOne instanceof FireworkEffectMeta instanceOne && metaTwo instanceof FireworkEffectMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getEffect(), instanceTwo.getEffect())) {
                return true;
            }
        }

        // Firework
        if (metaOne instanceof FireworkMeta instanceOne && metaTwo instanceof FireworkMeta instanceTwo) {
            if (instanceOne.getPower() != instanceTwo.getPower()) {
                return true;
            }
            if (!instanceOne.getEffects().equals(instanceTwo.getEffects())) {
                return true;
            }
        }

        // Leather Armor
        if (metaOne instanceof LeatherArmorMeta instanceOne && metaTwo instanceof LeatherArmorMeta instanceTwo) {
            if (!instanceOne.getColor().equals(instanceTwo.getColor())) {
                return true;
            }
        }

        // Maps
        if (metaOne instanceof MapMeta instanceOne && metaTwo instanceof MapMeta instanceTwo) {
            if (instanceOne.hasMapView() != instanceTwo.hasMapView()) {
                return true;
            }
            if (instanceOne.hasLocationName() != instanceTwo.hasLocationName()) {
                return true;
            }
            if (instanceOne.hasColor() != instanceTwo.hasColor()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getMapView(), instanceTwo.getMapView())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getLocationName(), instanceTwo.getLocationName())) {
                return true;
            }
            if (!Objects.equals(instanceOne.getColor(), instanceTwo.getColor())) {
                return true;
            }
        }

        // Potion
        if (metaOne instanceof PotionMeta instanceOne && metaTwo instanceof PotionMeta instanceTwo) {
            if (!Objects.equals(instanceOne.getBasePotionData(), instanceTwo.getBasePotionData())) {
                return true;
            }
            if (instanceOne.hasCustomEffects() != instanceTwo.hasCustomEffects()) {
                return true;
            }
            if (instanceOne.hasColor() != instanceTwo.hasColor()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getColor(), instanceTwo.getColor())) {
                return true;
            }
            if (!instanceOne.getCustomEffects().equals(instanceTwo.getCustomEffects())) {
                return true;
            }
        }

        // Skull
        if (metaOne instanceof SkullMeta instanceOne && metaTwo instanceof SkullMeta instanceTwo) {
            if (instanceOne.hasOwner() != instanceTwo.hasOwner()) {
                return true;
            }
            if (!Objects.equals(instanceOne.getOwningPlayer(), instanceTwo.getOwningPlayer())) {
                return true;
            }
        }

        // Stew
        if (metaOne instanceof SuspiciousStewMeta instanceOne && metaTwo instanceof SuspiciousStewMeta instanceTwo) {
            if (instanceOne.hasCustomEffects() != instanceTwo.hasCustomEffects()) {
                return true;
            }

            if (!Objects.equals(instanceOne.getCustomEffects(), instanceTwo.getCustomEffects())) {
                return true;
            }
        }

        // Fish Bucket
        if (metaOne instanceof TropicalFishBucketMeta instanceOne
                && metaTwo instanceof TropicalFishBucketMeta instanceTwo) {
            if (instanceOne.hasVariant() != instanceTwo.hasVariant()) {
                return true;
            }
            if (!instanceOne.getPattern().equals(instanceTwo.getPattern())) {
                return true;
            }
            if (!instanceOne.getBodyColor().equals(instanceTwo.getBodyColor())) {
                return true;
            }
            if (!instanceOne.getPatternColor().equals(instanceTwo.getPatternColor())) {
                return true;
            }
        }

        // Knowledge Book
        if (metaOne instanceof KnowledgeBookMeta instanceOne && metaTwo instanceof KnowledgeBookMeta instanceTwo) {
            if (instanceOne.hasRecipes() != instanceTwo.hasRecipes()) {
                return true;
            }

            return !Objects.equals(instanceOne.getRecipes(), instanceTwo.getRecipes());
        }

        // Cannot escape via any meta's extension check
        return false;
    }
}
