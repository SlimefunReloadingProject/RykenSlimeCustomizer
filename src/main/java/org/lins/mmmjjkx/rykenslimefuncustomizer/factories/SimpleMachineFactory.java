package org.lins.mmmjjkx.rykenslimefuncustomizer.factories;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.*;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoDisenchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.BookBinder;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.ProduceCollector;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf.AdvancedAnimalGrowthAccelerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf.AdvancedCropGrowthAccelerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf.AdvancedTreeGrowthAccelerator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.SimpleMachineType;

public class SimpleMachineFactory {
    public static SlimefunItem create(
            ItemGroup group,
            SlimefunItemStack slimefunItemStack,
            RecipeType recipeType,
            ItemStack[] recipe,
            SimpleMachineType machineType,
            int capacity,
            int consumption,
            int speed,
            int radius,
            int repairFactor) {
        SlimefunItem instance =
                switch (machineType) {
                    case ELECTRIC_FURNACE -> new ElectricFurnace(group, slimefunItemStack, recipeType, recipe);
                    case ELECTRIC_GOLD_PAN -> new ElectricGoldPan(group, slimefunItemStack, recipeType, recipe);
                    case ELECTRIC_SMELTERY -> new ElectricSmeltery(group, slimefunItemStack, recipeType, recipe);
                    case ELECTRIC_DUST_WASHER -> new ElectricDustWasher(group, slimefunItemStack, recipeType, recipe);
                    case ELECTRIC_ORE_GRINDER -> new ElectricOreGrinder(group, slimefunItemStack, recipeType, recipe);
                    case ELECTRIC_INGOT_FACTORY -> new ElectricIngotFactory(
                            group, slimefunItemStack, recipeType, recipe);
                    case ELECTRIC_INGOT_PULVERIZER -> new ElectricIngotPulverizer(
                            group, slimefunItemStack, recipeType, recipe);
                    case CHARGING_BENCH -> new ChargingBench(group, slimefunItemStack, recipeType, recipe);
                    case FREEZER -> new Freezer(group, slimefunItemStack, recipeType, recipe);
                    case CARBON_PRESS -> new CarbonPress(group, slimefunItemStack, recipeType, recipe);
                    case ELECTRIC_PRESS -> new ElectricPress(group, slimefunItemStack, recipeType, recipe);
                    case ELECTRIC_CRUCIBLE -> new ElectrifiedCrucible(group, slimefunItemStack, recipeType, recipe);
                    case FOOD_FABRICATOR -> new FoodFabricator(group, slimefunItemStack, recipeType, recipe);
                    case HEATED_PRESSURE_CHAMBER -> new HeatedPressureChamber(
                            group, slimefunItemStack, recipeType, recipe);
                    case BOOK_BINDER -> new BookBinder(group, slimefunItemStack, recipeType, recipe);
                    case AUTO_ENCHANTER -> new AutoEnchanter(group, slimefunItemStack, recipeType, recipe);
                    case AUTO_DISENCHANTER -> new AutoDisenchanter(group, slimefunItemStack, recipeType, recipe);
                    case AUTO_DRIER -> new AutoDrier(group, slimefunItemStack, recipeType, recipe);
                    case AUTO_BREWER -> new AutoBrewer(group, slimefunItemStack, recipeType, recipe);
                    case REFINERY -> new Refinery(group, slimefunItemStack, recipeType, recipe);
                    case PRODUCE_COLLECTOR -> new ProduceCollector(group, slimefunItemStack, recipeType, recipe);
                    case TREE_GROWTH_ACCELERATOR -> new AdvancedTreeGrowthAccelerator(
                            group, slimefunItemStack, recipeType, recipe, capacity, radius, consumption);
                    case ANIMAL_GROWTH_ACCELERATOR -> new AdvancedAnimalGrowthAccelerator(
                            group, slimefunItemStack, recipeType, recipe, capacity, radius, consumption);
                    case CROP_GROWTH_ACCELERATOR -> new AdvancedCropGrowthAccelerator(
                            group, slimefunItemStack, recipeType, recipe, capacity, radius, consumption, speed);
                    case AUTO_ANVIL -> new AutoAnvil(group, repairFactor, slimefunItemStack, recipeType, recipe);
                };

        if (instance instanceof AContainer aContainer) {
            aContainer.setCapacity(capacity);
            aContainer.setEnergyConsumption(consumption);
            aContainer.setProcessingSpeed(speed);
        }

        return instance;
    }
}
