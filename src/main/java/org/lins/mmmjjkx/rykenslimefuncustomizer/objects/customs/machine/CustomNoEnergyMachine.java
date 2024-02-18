package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.AbstractEmptyMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.SmallerMachineInfo;

import java.util.List;
import java.util.function.Consumer;

public class CustomNoEnergyMachine extends AbstractEmptyMachine<MachineOperation> {
    private final List<Integer> input;
    private final List<Integer> output;
    private final JavaScriptEval eval;
    private final MachineProcessor<MachineOperation> processor;

    private boolean worked = false;

    public CustomNoEnergyMachine(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, CustomMenu menu,
                                 List<Integer> input, List<Integer> output, @Nullable JavaScriptEval eval, int work) {
        super(itemGroup, item, recipeType, recipe);

        this.input = input;
        this.output = output;
        this.eval = eval;
        this.processor = new MachineProcessor<>(this);

        if (this.eval != null) {
            this.eval.doInit();

            this.eval.addThing("setWorking", (Consumer<Boolean>) b -> worked = b);
            this.eval.addThing("working", worked);
        }

        if (menu != null) {
            if (work != -1) {
                menu.addMenuClickHandler(work, (p, slot, is, ca) -> {
                    this.worked = true;
                    return false;
                });
            }
            if (this.eval != null) {
                if (this.eval.hasFunction("onUse")) {
                    addItemHandler((BlockUseHandler) e -> this.eval.evalFunction("onUse", e));
                }

                if (this.eval.hasFunction("onBreak")) {
                    addItemHandler(new BlockBreakHandler(false, false) {
                        @Override
                        public void onPlayerBreak(@NotNull BlockBreakEvent e, @NotNull ItemStack is, @NotNull List<ItemStack> list) {
                            CustomNoEnergyMachine.this.eval.evalFunction("onBreak", e, is, list);
                        }
                    });
                }

                if (this.eval.hasFunction("onPlace")) {
                    addItemHandler(new BlockPlaceHandler(false) {
                        @Override
                        public void onPlayerPlace(@NotNull BlockPlaceEvent e) {
                            CustomNoEnergyMachine.this.eval.evalFunction("onBreak", e);
                        }
                    });
                }
            }
        }

        this.addItemHandler(
                new SimpleBlockBreakHandler() {
                    @Override
                    public void onBlockBreak(@NotNull Block b) {
                        BlockMenu blockMenu = StorageCacheUtils.getMenu(b.getLocation());
                        if (blockMenu != null) {
                            blockMenu.dropItems(blockMenu.getLocation(), getInputSlots());
                            blockMenu.dropItems(blockMenu.getLocation(), getOutputSlots());
                        }
                    }
                }
        );
    }

    @Override
    public void preRegister() {
        super.preRegister();
        this.addItemHandler(getBlockTicker());
    }

    protected void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
        if (eval != null) {
            BlockMenu blockMenu = StorageCacheUtils.getMenu(b.getLocation());
            SmallerMachineInfo info = new SmallerMachineInfo(blockMenu, data, item, b, processor);
            eval.evalFunction("tick", info);
        }
    }

    @Override
    public int[] getInputSlots() {
        int[] input = new int[this.input.size()];
        for (int i = 0; i < this.input.size(); i ++) {
            input[i] = this.input.get(i);
        }
        return input;
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
                CustomNoEnergyMachine.this.tick(b, item, data);
            }
        };
    }

    @NotNull
    @Override
    public MachineProcessor<MachineOperation> getMachineProcessor() {
        return processor;
    }
}
