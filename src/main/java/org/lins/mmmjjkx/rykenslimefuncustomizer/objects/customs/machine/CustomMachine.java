package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
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
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineInfo;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.MachineRecord;

import java.util.List;

public class CustomMachine extends AbstractEmptyMachine<MachineOperation> implements EnergyNetComponent {
    private final MachineRecord theRecord;
    private final List<Integer> input;
    private final List<Integer> output;
    private final EnergyNetComponentType type;
    private final MachineProcessor<MachineOperation> processor;
    private final JavaScriptEval eval;

    public CustomMachine(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, @Nullable CustomMenu menu,
                         List<Integer> input, List<Integer> output, MachineRecord record, EnergyNetComponentType type, @Nullable JavaScriptEval eval) {
        super(itemGroup, item, recipeType, recipe);

        this.input = input;
        this.output = output;
        this.theRecord = record;

        this.type = type;
        this.eval = eval;
        this.processor = new MachineProcessor<>(this);

        if (this.eval != null) {
            this.eval.doInit();

            addItemHandler(
                    new BlockBreakHandler(false, false) {
                        @Override
                        public void onPlayerBreak(@NotNull BlockBreakEvent e, @NotNull ItemStack is, @NotNull List<ItemStack> list) {
                            CustomMachine.this.eval.evalFunction("onBreak", e, is, list);
                        }
                    },
                    new BlockPlaceHandler(false) {
                        @Override
                        public void onPlayerPlace(@NotNull BlockPlaceEvent e) {
                            CustomMachine.this.eval.evalFunction("onPlace", e);
                        }
                    }
            );
        }

        if (menu != null) {
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
    }

    @Override
    public void preRegister() {
        super.preRegister();
        this.addItemHandler(getBlockTicker());
    }

    protected void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
        if (eval != null) {
            BlockMenu blockMenu = StorageCacheUtils.getMenu(b.getLocation());
            MachineInfo info = new MachineInfo(blockMenu, data, item, b, theRecord.totalTicks(), theRecord.getProgress(), processor, theRecord, this);
            eval.evalFunction("tick", info);
        }
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
                CustomMachine.this.tick(b, item, data);
            }
        };
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

    @NotNull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return type;
    }

    @Override
    public int getCapacity() {
        return theRecord.capacity();
    }

    @NotNull
    @Override
    public MachineProcessor<MachineOperation> getMachineProcessor() {
        return processor;
    }
}
