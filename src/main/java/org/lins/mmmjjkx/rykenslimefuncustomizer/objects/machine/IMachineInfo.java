package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;

public interface IMachineInfo {
    BlockMenu getBlockMenu();
    SlimefunBlockData getData();
    SlimefunItem getMachineItem();
    Block getBlock();
    MachineProcessor<?> getProcessor();
}
