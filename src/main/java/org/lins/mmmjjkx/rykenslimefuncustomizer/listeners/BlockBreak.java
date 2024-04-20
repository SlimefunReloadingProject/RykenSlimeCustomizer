package org.lins.mmmjjkx.rykenslimefuncustomizer.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

public class BlockBreak implements Listener {
    public BlockBreak(RykenSlimefunCustomizer plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

    }
}
