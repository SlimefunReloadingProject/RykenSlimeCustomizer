package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import lombok.Getter;

@Getter
public enum SimpleMachineType {
    ELECTRIC_SMELTERY(true),
    ELECTRIC_FURNACE(true),
    ELECTRIC_GOLD_PAN(true),
    ELECTRIC_DUST_WASHER(true),
    ELECTRIC_ORE_GRINDER(true),
    ELECTRIC_INGOT_FACTORY(true),
    ELECTRIC_INGOT_PULVERIZER(true),
    CHARGING_BENCH(true),
    ANIMAL_GROWTH_ACCELERATOR(true),
    TREE_GROWTH_ACCELERATOR(true),
    CROP_GROWTH_ACCELERATOR(true),
    FREEZER(true),
    CARBON_PRESS(true),
    ELECTRIC_PRESS(true),
    ELECTRIC_CRUCIBLE(true),
    FOOD_FABRICATOR(true),
    HEATED_PRESSURE_CHAMBER(true),
    AUTO_ENCHANTER(true),
    AUTO_DISENCHANTER(true),
    BOOK_BINDER(true),
    AUTO_ANVIL(true),
    AUTO_DRIER(true),
    AUTO_BREWER(true),
    REFINERY(true),
    PRODUCE_COLLECTOR(true);

    private final boolean energy;

    SimpleMachineType(boolean energy) {
        this.energy = energy;
    }
}
