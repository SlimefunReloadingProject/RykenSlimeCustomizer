package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

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
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.SmallerMachineInfo;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CustomNoEnergyMachine extends AbstractEmptyMachine {
    private final List<Integer> input;
    private final List<Integer> output;
    private final JavaScriptEval eval;
    private final MachineProcessor<MachineOperation> processor;
    private final CustomMenu menu;

    private boolean worked = false;

    public CustomNoEnergyMachine(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, CustomMenu menu,
                                 List<Integer> input, List<Integer> output, @Nullable JavaScriptEval eval, int work) {
        super(itemGroup, item, recipeType, recipe);

        this.input = input;
        this.output = output;
        this.eval = eval;
        this.menu = menu;
        this.processor = new MachineProcessor<>(this);

        if (menu != null) {
            this.menu.addMenuClickHandler(work, (p, slot, is, ca) -> {
                this.worked = true;
                return false;
            });
            if (this.eval != null) {
                this.eval.addThing("addClickHandler", (BiConsumer<Integer, ChestMenu.MenuClickHandler>) CustomNoEnergyMachine.this.menu::addMenuClickHandler);
                this.eval.addThing("setWorking", (Consumer<Boolean>) b -> worked = b);
                this.eval.addThing("working", worked);

                if (this.eval.hasFunction("onUse", 1)) {
                    addItemHandler((BlockUseHandler) e -> this.eval.evalFunction("onUse", e));
                }

                if (this.eval.hasFunction("onBreak", 3)) {
                    addItemHandler(new BlockBreakHandler(false, false) {
                        @Override
                        public void onPlayerBreak(@NotNull BlockBreakEvent e, @NotNull ItemStack is, @NotNull List<ItemStack> list) {
                            CustomNoEnergyMachine.this.eval.evalFunction("onBreak", e, is, list);
                        }
                    });
                }

                if (this.eval.hasFunction("onPlace", 1)) {
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
                new BlockBreakHandler(false, false) {
                    @Override
                    public void onPlayerBreak(@NotNull BlockBreakEvent event, @NotNull ItemStack item, @NotNull List<ItemStack> drops) {
                        BlockMenu blockMenu = StorageCacheUtils.getMenu(event.getBlock().getLocation());
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
