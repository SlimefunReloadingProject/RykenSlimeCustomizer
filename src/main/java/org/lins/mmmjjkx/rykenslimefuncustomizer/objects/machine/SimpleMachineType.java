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
    AUTO_ENHANCED_CRAFTER(true);

    private final boolean energy;

    SimpleMachineType(boolean energy) {
        this.energy = energy;
    }
}
