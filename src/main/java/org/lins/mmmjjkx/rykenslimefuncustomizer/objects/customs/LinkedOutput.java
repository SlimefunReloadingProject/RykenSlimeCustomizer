package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public record LinkedOutput(
        ItemStack[] freeOutput,
        Map<Integer, ItemStack> linkedOutput,
        int[] freeChances,
        Map<Integer, Integer> linkedChances) {

    public ItemStack[] toArray() {
        ItemStack[] result = new ItemStack[freeOutput.length + linkedOutput.size()];
        System.arraycopy(freeOutput, 0, result, 0, freeOutput.length);
        int i = freeOutput.length;
        for (ItemStack item : linkedOutput.values()) {
            result[i] = item;
            i++;
        }

        return result;
    }

    public List<Integer> chancesToArray() {
        List<Integer> result = new ArrayList<>(freeChances.length + linkedChances.size());
        for (int chance : freeChances) {
            result.add(chance);
        }
        result.addAll(linkedChances.values());
        return result;
    }
}
