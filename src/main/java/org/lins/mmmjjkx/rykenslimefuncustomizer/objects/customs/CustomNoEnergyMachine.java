package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.record.MachineInfo;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.record.MachineRecord;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CustomNoEnergyMachine extends AbstractEmptyMachine {
    private final List<Integer> input;
    private final List<Integer> output;
    private final MachineRecord record;
    private final JavaScriptEval eval;
    private final MachineProcessor<MachineOperation> processor;
    private final CustomMenu menu;

    private boolean worked = false;

    public CustomNoEnergyMachine(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, CustomMenu menu,
                                 List<Integer> input, List<Integer> output, MachineRecord record, JavaScriptEval eval, int work) {
        super(itemGroup, item, recipeType, recipe);

        this.input = input;
        this.output = output;
        this.record = record;
        this.eval = eval;
        this.menu = menu;
        this.processor = new MachineProcessor<>(this);

        this.menu.addMenuClickHandler(work, (p, slot, is, ca) -> {
           this.worked = true;
           return false;
        });

        this.eval.addThing("addClickHandler", (BiConsumer<Integer, ChestMenu.MenuClickHandler>) CustomNoEnergyMachine.this.menu::addMenuClickHandler);
        this.eval.addThing("isWorking", (Supplier<Boolean>) () -> worked);
    }

    @Override
    public void preRegister() {
        super.preRegister();
        this.addItemHandler((BlockUseHandler) e -> menu.open(e.getPlayer()));
        this.addItemHandler(getBlockTicker());
    }

    protected void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
        BlockMenu blockMenu = StorageCacheUtils.getMenu(b.getLocation());
        MachineInfo info = new MachineInfo(blockMenu, data, item, b, record.totalTicks(), record.getProgress(), processor, record);
        eval.evalFunction("tick", info);
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
