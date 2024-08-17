package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global;

import java.util.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

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

    public static void unregisterAddonDrops(ProjectAddon addon) {
        for (Material material : new ArrayList<>(drops.keySet())) {
            List<Drop> dropsList = getDrops(material);
            dropsList.removeIf(drop -> drop.owner.equals(addon));
            if (dropsList.isEmpty()) {
                drops.remove(material);
            }
        }
    }

    public record Drop(ItemStack itemStack, int dropChance, ProjectAddon owner, int minDropAmount, int maxDropAmount) {
        public Drop(ItemStack itemStack, int dropChance, ProjectAddon owner) {
            this(itemStack, dropChance, owner, itemStack.getAmount(), itemStack.getAmount());
        }

        @Override
        public ItemStack itemStack() {
            ItemStack itemStack = this.itemStack.clone();
            itemStack.setAmount(randomDropAmount());
            return itemStack;
        }

        private int randomDropAmount() {
            Random random = new Random();
            int min = Math.min(minDropAmount, maxDropAmount);
            int max = Math.max(minDropAmount, maxDropAmount);
            return random.nextInt(max - min + 1) + min;
        }
    }
}
