package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.util.*;

public class DropFromBlock {
    private static final Multimap<Material, Map<ProjectAddon, List<Drop>>> drops;

    static {
        drops = HashMultimap.create(600, 3000);
    }

    public static void addDrop(Material material, ProjectAddon projectAddon, Drop drop) {
        Collection<Map<ProjectAddon, List<Drop>>> collection = drops.get(material);
        if (collection.isEmpty()) {
            Map<ProjectAddon, List<Drop>> map = Map.of(projectAddon, new ArrayList<>(Collections.singletonList(drop)));
            drops.put(material, map);
        } else {
            for (Map<ProjectAddon, List<Drop>> map : collection) {
                if (map.containsKey(projectAddon)) {
                    map.get(projectAddon).add(drop);
                } else {
                    map.put(projectAddon, new ArrayList<>(Collections.singletonList(drop)));
                }
            }
        }
    }

    public static Collection<Map<ProjectAddon, List<Drop>>> getDrops(Material material) {
        return drops.get(material);
    }

    public static void removeAllDrops(Material material, ProjectAddon projectAddon) {
        Collection<Map<ProjectAddon, List<Drop>>> collection = drops.get(material);
        if (collection.isEmpty()) {
            return;
        }
        for (Map<ProjectAddon, List<Drop>> map : collection) {
            map.remove(projectAddon);
        }
    }

    public static void clear() {
        drops.clear();
    }

    public record Drop(ItemStack itemStack, int dropChance) {}
}
