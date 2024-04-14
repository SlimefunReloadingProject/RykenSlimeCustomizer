package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.util.HashMap;
import java.util.Map;

public class DropFromBlock {
    private static final Map<Material, Map<ProjectAddon, Drop>> drops = new HashMap<>();

    public static void addDrop(Material block, ProjectAddon addon, Drop drop) {
        if (!drops.containsKey(block)) {
            drops.put(block, new HashMap<>());
        }
        drops.get(block).put(addon, drop);
    }

    public static Map<ProjectAddon, Drop> getDrops(Material block) {
        return drops.get(block);
    }

    public static void removeDrop(Material block, ProjectAddon addon) {
        if (drops.containsKey(block)) {
            drops.get(block).remove(addon);
        }
    }

    public record Drop(ItemStack itemStack, int dropChance) {}
}
