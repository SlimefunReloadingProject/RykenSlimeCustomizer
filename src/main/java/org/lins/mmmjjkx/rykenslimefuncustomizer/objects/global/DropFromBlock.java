package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.util.*;

public class DropFromBlock {
    private static final Map<Material, List<Drop>> drops;

    static {
        drops = new HashMap<>();
    }

    public static void addDrop(Material material, Drop drop) {
        drops.computeIfAbsent(material, k -> new ArrayList<>()).add(drop);
    }

    public static List<Drop> getDrops(Material material) {
        return drops.getOrDefault(material, Collections.emptyList());
    }

    public static void removeDrop(Material material, Drop drop) {
        List<Drop> dropsList = getDrops(material);
        dropsList.remove(drop);
        if (dropsList.isEmpty()) {
            drops.remove(material);
        }
    }

    public static void unregisterAddonDrops(ProjectAddon addon) {
        for (Material material : drops.keySet()) {
            List<Drop> dropsList = getDrops(material);
            dropsList.removeIf(drop -> drop.owner.equals(addon));
            if (dropsList.isEmpty()) {
                drops.remove(material);
            }
        }
    }

    public record Drop(ItemStack itemStack, int dropChance, ProjectAddon owner) {}
}
