package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoBrewer;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;

public class AdvancedAutoBrewer extends AutoBrewer {
    private static final Map<Material, PotionType> potionRecipes = new EnumMap<>(Material.class);
    private static final Map<PotionType, PotionType> fermentations = new EnumMap<>(PotionType.class);

    private final int speed;

    public AdvancedAutoBrewer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int speed) {
        super(itemGroup, item, recipeType, recipe);

        this.speed = speed;
    }

    @Nullable
    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        ItemStack input1 = menu.getItemInSlot(this.getInputSlots()[0]);
        ItemStack input2 = menu.getItemInSlot(this.getInputSlots()[1]);
        if (input1 != null && input2 != null) {
            if (!this.isPotion(input1.getType()) && !this.isPotion(input2.getType())) {
                return null;
            } else {
                boolean isPotionInFirstSlot = this.isPotion(input1.getType());
                ItemStack ingredient = isPotionInFirstSlot ? input2 : input1;
                if (ingredient.hasItemMeta()) {
                    return null;
                } else {
                    ItemStack potionItem = isPotionInFirstSlot ? input1 : input2;
                    PotionMeta potion = (PotionMeta)potionItem.getItemMeta();
                    ItemStack output = this.brew(ingredient.getType(), potionItem.getType(), potion);
                    if (output == null) {
                        return null;
                    } else {
                        output.setItemMeta(potion);
                        if (!InvUtils.fits(menu.toInventory(), output, this.getOutputSlots())) {
                            return null;
                        } else {
                            for(int slot : this.getInputSlots()) {
                                menu.consumeItem(slot);
                            }

                            return new MachineRecipe(30 / speed, new ItemStack[]{input1, input2}, new ItemStack[]{output});
                        }
                    }
                }
            }
        } else {
            return null;
        }
    }

    @ParametersAreNonnullByDefault
    @Nullable
    private ItemStack brew(Material input, Material potionType, PotionMeta potion) {
        PotionData data = potion.getBasePotionData();
        PotionType type = data.getType();
        if (type == PotionType.WATER) {
            if (input == Material.FERMENTED_SPIDER_EYE) {
                potion.setBasePotionData(new PotionData(PotionType.WEAKNESS, false, false));
                return new ItemStack(potionType);
            }

            if (input == Material.NETHER_WART) {
                potion.setBasePotionData(new PotionData(PotionType.AWKWARD, false, false));
                return new ItemStack(potionType);
            }

            if (potionType == Material.POTION && input == Material.GUNPOWDER) {
                return new ItemStack(Material.SPLASH_POTION);
            }

            if (potionType == Material.SPLASH_POTION && input == Material.DRAGON_BREATH) {
                return new ItemStack(Material.LINGERING_POTION);
            }
        } else if (input == Material.FERMENTED_SPIDER_EYE) {
            PotionType fermented = fermentations.get(type);
            if (fermented != null) {
                potion.setBasePotionData(new PotionData(fermented, data.isExtended(), data.isUpgraded()));
                return new ItemStack(potionType);
            }
        } else {
            if (input == Material.REDSTONE && type.isExtendable() && !data.isUpgraded()) {
                potion.setBasePotionData(new PotionData(type, true, false));
                return new ItemStack(potionType);
            }

            if (input == Material.GLOWSTONE_DUST && type.isUpgradeable() && !data.isExtended()) {
                potion.setBasePotionData(new PotionData(type, false, true));
                return new ItemStack(potionType);
            }

            if (type == PotionType.AWKWARD) {
                PotionType potionRecipe = potionRecipes.get(input);
                if (potionRecipe != null) {
                    potion.setBasePotionData(new PotionData(potionRecipe, false, false));
                    return new ItemStack(potionType);
                }
            }
        }

        return null;
    }

    private boolean isPotion(@Nonnull Material mat) {
        return mat == Material.POTION || mat == Material.SPLASH_POTION || mat == Material.LINGERING_POTION;
    }

    static {
        potionRecipes.put(Material.SUGAR, PotionType.SPEED);
        potionRecipes.put(Material.RABBIT_FOOT, PotionType.JUMP);
        potionRecipes.put(Material.BLAZE_POWDER, PotionType.STRENGTH);
        potionRecipes.put(Material.GLISTERING_MELON_SLICE, PotionType.INSTANT_HEAL);
        potionRecipes.put(Material.SPIDER_EYE, PotionType.POISON);
        potionRecipes.put(Material.GHAST_TEAR, PotionType.REGEN);
        potionRecipes.put(Material.MAGMA_CREAM, PotionType.FIRE_RESISTANCE);
        potionRecipes.put(Material.PUFFERFISH, PotionType.WATER_BREATHING);
        potionRecipes.put(Material.GOLDEN_CARROT, PotionType.NIGHT_VISION);
        potionRecipes.put(Material.TURTLE_HELMET, PotionType.TURTLE_MASTER);
        potionRecipes.put(Material.PHANTOM_MEMBRANE, PotionType.SLOW_FALLING);
        fermentations.put(PotionType.SPEED, PotionType.SLOWNESS);
        fermentations.put(PotionType.JUMP, PotionType.SLOWNESS);
        fermentations.put(PotionType.INSTANT_HEAL, PotionType.INSTANT_DAMAGE);
        fermentations.put(PotionType.POISON, PotionType.INSTANT_DAMAGE);
        fermentations.put(PotionType.NIGHT_VISION, PotionType.INVISIBILITY);
    }
}
