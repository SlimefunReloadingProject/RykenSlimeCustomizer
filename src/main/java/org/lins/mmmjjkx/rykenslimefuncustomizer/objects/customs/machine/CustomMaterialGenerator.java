package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class CustomMaterialGenerator extends SlimefunItem implements InventoryBlock, EnergyNetComponent  {
    private final int capacity;
    private final List<Integer> output;
    private final int tickRate;
    private final int statusSlot;
    private final ItemStack generation;
    private final int per;

    public CustomMaterialGenerator(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType,
                                   ItemStack[] recipe, int capacity, List<Integer> output, int statusSlot,
                                   int tickRate, ItemStack generation, int per) {
        super(itemGroup, item, recipeType, recipe);

        this.capacity = capacity;
        this.output = output;
        this.statusSlot = statusSlot;
        this.tickRate = tickRate;
        this.generation = generation;
        this.per = per;

        register(RykenSlimefunCustomizer.INSTANCE);
    }

    private void tick(Block b) {
        int progress = getProgress(b);

        BlockMenu blockMenu = StorageCacheUtils.getMenu(b.getLocation());

        if (getCharge(b.getLocation()) >= per) {
            if (blockMenu != null) {
                if (progress >= tickRate) {
                    if (blockMenu.fits(generation, getOutputSlots())) {
                        blockMenu.pushItem(generation.clone(), getOutputSlots());
                        removeCharge(b.getLocation(), per);
                        progress = 1;
                    } else {
                        if (blockMenu.hasViewer()) {
                            if (statusSlot > -1) {
                                blockMenu.replaceExistingItem(statusSlot, new CustomItemStack(
                                        Material.RED_STAINED_GLASS_PANE,
                                        "&c空间不足"
                                ));
                            }
                        }
                        return;
                    }
                } else {
                    progress++;
                }

                setProgress(b, progress);
                if (blockMenu.hasViewer()) {
                    if (statusSlot > -1) {
                        blockMenu.replaceExistingItem(statusSlot, new CustomItemStack(
                                Material.LIME_STAINED_GLASS_PANE,
                                "&a生产中"
                        ));
                    }
                }
            }
        }
    }

    private static void setProgress(Block b, int progress) {
        StorageCacheUtils.setData(b.getLocation(), "progress", String.valueOf(progress));
    }

    private static int getProgress(Block b) {
        int progress;
        try {
            progress = Integer.parseInt(Objects.requireNonNull(StorageCacheUtils.getData(b.getLocation(), "progress")));
        } catch (NumberFormatException | NullPointerException ex) {
            progress = 1;
        }
        return progress;
    }

    @NotNull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int[] getInputSlots() {
        return new int[0];
    }

    @Override
    public int[] getOutputSlots() {
        int[] output = new int[this.output.size()];
        for (int i = 0; i < this.output.size(); i ++) {
            output[i] = this.output.get(i);
        }
        return output;
    }

    @Override
    public BlockTicker getBlockTicker() {
        return new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return true;
            }

            @Override
            public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
                CustomMaterialGenerator.this.tick(b);
            }
        };
    }
}
