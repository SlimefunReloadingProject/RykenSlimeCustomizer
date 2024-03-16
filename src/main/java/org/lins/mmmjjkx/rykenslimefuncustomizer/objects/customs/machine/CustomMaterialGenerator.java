package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.RSCItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class CustomMaterialGenerator extends SlimefunItem implements InventoryBlock, EnergyNetComponent, RecipeDisplayItem {
    private final int capacity;
    private final List<Integer> output;
    private final int tickRate;
    private final int statusSlot;
    private final ItemStack generation;
    private final int per;

    public CustomMaterialGenerator(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType,
                                   ItemStack[] recipe, int capacity, List<Integer> output, int statusSlot,
                                   int tickRate, ItemStack generation, CustomMenu menu, int per) {
        super(itemGroup, item, recipeType, recipe);

        this.capacity = capacity;
        this.output = output;
        this.statusSlot = statusSlot;
        this.tickRate = tickRate;
        this.generation = generation;
        this.per = per;

        this.addItemHandler(getBlockTicker());
        this.addItemHandler(new SimpleBlockBreakHandler() {
            @Override
            public void onBlockBreak(@NotNull Block block) {
                BlockMenu bm = StorageCacheUtils.getMenu(block.getLocation());
                if (bm != null) {
                    bm.dropItems(block.getLocation(), getOutputSlots());
                }
            }
        });

        menu.addMenuClickHandler(statusSlot, ChestMenuUtils.getEmptyClickHandler());

        register(RykenSlimefunCustomizer.INSTANCE);
    }

    private void tick(Block b) {
        int progress = getProgress(b);

        BlockMenu blockMenu = StorageCacheUtils.getMenu(b.getLocation());

        if (blockMenu != null) {
            if (getCharge(b.getLocation()) >= per) {
                if (progress >= tickRate) {
                    setProgress(b, 0);
                    if (blockMenu.fits(generation, getOutputSlots())) {
                        if (blockMenu.hasViewer()) {
                            blockMenu.replaceExistingItem(statusSlot, new CustomItemStack(
                                    Material.LIME_STAINED_GLASS_PANE,
                                    "&a生产中"
                            ));
                        }
                        blockMenu.pushItem(generation.clone(), getOutputSlots());
                        removeCharge(b.getLocation(), per);
                    } else {
                        if (blockMenu.hasViewer()) {
                            if (statusSlot > -1) {
                                blockMenu.replaceExistingItem(statusSlot, new CustomItemStack(
                                        Material.ORANGE_STAINED_GLASS_PANE,
                                        "&c空间不足"
                                ));
                            }
                        }
                    }
                } else {
                    addProgress(b);
                }
            } else {
                blockMenu.replaceExistingItem(statusSlot, new CustomItemStack(
                        Material.RED_STAINED_GLASS_PANE,
                        "&4电力不足"
                ));
            }
        }
    }

    private void addProgress(Block b) {
        setProgress(b, getProgress(b)+1);
    }

    private void setProgress(Block b, int progress) {
        StorageCacheUtils.setData(b.getLocation(), "progress", String.valueOf(progress));
    }

    private int getProgress(Block b) {
        int progress;
        try {
            progress = Integer.parseInt(Objects.requireNonNull(StorageCacheUtils.getData(b.getLocation(), "progress")));
        } catch (NumberFormatException | NullPointerException ex) {
            StorageCacheUtils.setData(b.getLocation(), "progress", "0");
            progress = 0;
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

    @NotNull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        ItemStack speed = new RSCItemStack(Material.KNOWLEDGE_BOOK, CommonUtils.parseToComponent("&a&l速度"),
                CommonUtils.parseToComponent("&a&l每 &b&l" + tickRate + " &a&l个粘液刻生成一次"));
        return List.of(speed, generation);
    }
}
