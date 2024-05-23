package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMachine;

public record MachineInfo(
        @Nullable BlockMenuWrapper blockMenu,
        SlimefunBlockData data,
        SlimefunItem machineItem,
        Block block,
        int totalTicks,
        int progress,
        MachineProcessor<?> processor,
        MachineOperation operation,
        CustomMachine machine) {

    public Inventory getInventory() {
        if (blockMenu == null) {
            return null;
        }
        return blockMenu.getInventory();
    }
}
