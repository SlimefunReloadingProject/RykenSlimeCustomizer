package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators.AbstractGrowthAccelerator;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.inventory.ItemStack;

public class AdvancedTreeGrowthAccelerator extends AbstractGrowthAccelerator {
    private static final ItemStack organicFertilizer = ItemStackWrapper.wrap(SlimefunItems.FERTILIZER);

    private final int capacity;
    private final int radius;
    private final int energy_consumption;

    public AdvancedTreeGrowthAccelerator(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            int capacity,
            int radius,
            int energy_consumption) {
        super(itemGroup, item, recipeType, recipe);

        this.capacity = capacity;
        this.radius = radius;
        this.energy_consumption = energy_consumption;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    protected void tick(@Nonnull Block b) {
        BlockMenu inv = BlockStorage.getInventory(b);
        if (inv != null) {
            if (this.getCharge(b.getLocation()) >= energy_consumption) {
                for (int x = -radius; x <= radius; ++x) {
                    for (int z = -radius; z <= radius; ++z) {
                        Block block = b.getRelative(x, 0, z);
                        if (Tag.SAPLINGS.isTagged(block.getType())) {
                            boolean isGrowthBoosted = this.tryToBoostGrowth(b, inv, block);
                            if (isGrowthBoosted) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @ParametersAreNonnullByDefault
    private boolean tryToBoostGrowth(Block machine, BlockMenu inv, Block sapling) {
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_17)) {
            return this.applyBoneMeal(machine, sapling, inv);
        } else {
            Sapling saplingData = (Sapling) sapling.getBlockData();
            return saplingData.getStage() < saplingData.getMaximumStage()
                    && this.updateSaplingData(machine, sapling, inv, saplingData);
        }
    }

    @ParametersAreNonnullByDefault
    private boolean applyBoneMeal(Block machine, Block sapling, BlockMenu inv) {
        int[] var4 = this.getInputSlots();

        for (int slot : var4) {
            if (this.isFertilizer(inv.getItemInSlot(slot))) {
                this.removeCharge(machine.getLocation(), energy_consumption);
                sapling.applyBoneMeal(BlockFace.UP);
                inv.consumeItem(slot);
                sapling.getWorld()
                        .spawnParticle(
                                Particle.VILLAGER_HAPPY,
                                sapling.getLocation().add(0.5, 0.5, 0.5),
                                4,
                                0.10000000149011612,
                                0.10000000149011612,
                                0.10000000149011612);
                return true;
            }
        }

        return false;
    }

    @ParametersAreNonnullByDefault
    private boolean updateSaplingData(Block machine, Block block, BlockMenu inv, Sapling sapling) {
        int[] var5 = this.getInputSlots();

        for (int slot : var5) {
            if (this.isFertilizer(inv.getItemInSlot(slot))) {
                this.removeCharge(machine.getLocation(), 24);
                sapling.setStage(sapling.getStage() + 1);
                block.setBlockData(sapling, false);
                inv.consumeItem(slot);
                block.getWorld()
                        .spawnParticle(
                                Particle.VILLAGER_HAPPY,
                                block.getLocation().add(0.5, 0.5, 0.5),
                                4,
                                0.10000000149011612,
                                0.10000000149011612,
                                0.10000000149011612);
                return true;
            }
        }

        return false;
    }

    protected boolean isFertilizer(@Nullable ItemStack item) {
        return SlimefunUtils.isItemSimilar(item, organicFertilizer, false, false);
    }
}
