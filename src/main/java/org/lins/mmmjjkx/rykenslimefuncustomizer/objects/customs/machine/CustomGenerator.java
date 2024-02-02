package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.AbstractEnergyProvider;
import io.github.thebusybiscuit.slimefun4.implementation.operations.FuelOperation;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;

import java.util.List;

public class CustomGenerator extends AbstractEnergyProvider implements MachineProcessHolder<FuelOperation> {
    private final List<MachineFuel> fuels;
    private final CustomMenu menu;
    private final MachineProcessor<FuelOperation> processor = new MachineProcessor<>(this);
    private final int capacity;
    private final List<Integer> input;
    private final List<Integer> output;
    private final int production;

    public CustomGenerator(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, @NotNull CustomMenu menu, int capacity, List<Integer> input, List<Integer> output, int production, List<MachineFuel> machineFuels) {
        super(itemGroup, item, recipeType, recipe);

        this.fuels = machineFuels;
        this.menu = menu;
        this.capacity = capacity;
        this.input = input;
        this.output = output;
        this.production = production;

        this.addItemHandler(
                new SimpleBlockBreakHandler() {
                    @Override
                    public void onBlockBreak(@NotNull Block b) {
                        BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());

                        if (inv != null) {
                            inv.dropItems(b.getLocation(), getInputSlots());
                            inv.dropItems(b.getLocation(), getOutputSlots());
                        }

                        processor.endOperation(b);
                    }
                }
        );
    }

    @NotNull
    @Override
    public String getInventoryTitle() {
        return menu.getTitle();
    }

    @NotNull
    @Override
    public ItemStack getProgressBar() {
        return menu.getProgress();
    }

    @Override
    public int getEnergyProduction() {
        return production;
    }

    @Override
    public @NotNull MachineProcessor<FuelOperation> getMachineProcessor() {
        return processor;
    }

    @Override
    public int getCapacity() {
        return capacity;
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

    /**
     * Outside init
     */
    @Override
    public void registerDefaultFuelTypes() {
        for (MachineFuel fuel : fuels) {
            registerFuel(fuel);
        }
    }
}
