package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators.CropGrowthAccelerator;
import org.bukkit.inventory.ItemStack;

public class AdvancedCropGrowthAccelerator extends CropGrowthAccelerator {
    private final int capacity;
    private final int radius;
    private final int energy_consumption;
    private final int speed;

    public AdvancedCropGrowthAccelerator(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                                         int capacity, int radius, int energy_consumption, int speed) {
        super(itemGroup, item, recipeType, recipe);

        this.capacity = capacity;
        this.radius = radius;
        this.energy_consumption = energy_consumption;
        this.speed = speed;
    }

    @Override
    public int getEnergyConsumption() {
        return energy_consumption;
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
