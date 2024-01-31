package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;

public record SmallerMachineInfo(BlockMenu blockMenu, SlimefunBlockData data, SlimefunItem machineItem, Block block, MachineProcessor<?> processor)
        implements IMachineInfo{
    @Override
    public BlockMenu getBlockMenu() {
        return blockMenu;
    }

    @Override
    public SlimefunBlockData getData() {
        return data;
    }

    @Override
    public SlimefunItem getMachineItem() {
        return machineItem;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public MachineProcessor<?> getProcessor() {
        return processor;
    }
}
