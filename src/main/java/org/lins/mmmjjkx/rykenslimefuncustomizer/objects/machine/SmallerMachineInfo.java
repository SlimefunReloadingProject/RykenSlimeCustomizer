package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomNoEnergyMachine;

import javax.annotation.Nullable;

public record SmallerMachineInfo(
        @Nullable BlockMenuWrapper blockMenu,
        SlimefunBlockData data,
        CustomNoEnergyMachine machine,
        SlimefunItem machineItem,
        Block block,
        MachineProcessor<?> processor) {

    public Inventory getInventory() {
        if (blockMenu == null) {
            return null;
        }
        return blockMenu.getInventory();
    }
}
