package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import javax.annotation.Nullable;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomNoEnergyMachine;

public record SmallerMachineInfo(
        @Nullable BlockMenu blockMenu,
        Config data,
        CustomNoEnergyMachine machine,
        SlimefunItem machineItem,
        Block block,
        MachineProcessor<?> processor) {}
