package org.lins.mmmjjkx.rykenslimefuncustomizer.listeners;

import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global.DropFromBlock;

public class BlockListener implements Listener {
    public BlockListener() {
        Bukkit.getPluginManager().registerEvents(this, RykenSlimefunCustomizer.INSTANCE);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        List<DropFromBlock.Drop> drops = DropFromBlock.getDrops(block.getType());
        List<DropFromBlock.Drop> matchedDrops =
                drops.stream().filter(drop -> matchChance(drop.dropChance())).toList();
        if (matchedDrops.isEmpty()) return;

        for (DropFromBlock.Drop drop : matchedDrops) {
            block.getWorld().dropItemNaturally(e.getBlock().getLocation(), drop.itemStack());
        }
    }

    private static boolean matchChance(int chance) {
        if (chance >= 100) return true;

        Random rand = new Random();
        return rand.nextInt(100) < chance;
    }
}
