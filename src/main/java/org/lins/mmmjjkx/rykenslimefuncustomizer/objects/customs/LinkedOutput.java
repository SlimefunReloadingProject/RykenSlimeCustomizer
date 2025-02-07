package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

@Getter
public class LinkedOutput {
    private final ItemStack[] freeOutput;
    private final Map<Integer, ItemStack> linkedOutput;
    private final int[] freeChances;
    private final Map<Integer, Integer> linkedChances;

    public LinkedOutput(
            ItemStack[] freeOutput,
            Map<Integer, ItemStack> linkedOutput,
            int[] freeChances,
            Map<Integer, Integer> linkedChances) {
        this.freeOutput = freeOutput;
        this.linkedOutput = linkedOutput;
        this.freeChances = freeChances;
        this.linkedChances = linkedChances;
    }

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

    public void log() {
        // log free output & linked output
        for (int i = 0; i < freeOutput.length; i++) {
            ItemStack item = freeOutput[i];
            ExceptionHandler.info("FreeOutput: " + i + " 物品: " + item);
        }

        for (int slot : linkedOutput.keySet()) {
            ItemStack item = linkedOutput.get(slot);
            ExceptionHandler.info("LinkedOutput: " + slot + " 物品: " + item + " 位置: " + slot);
        }
    }
}
