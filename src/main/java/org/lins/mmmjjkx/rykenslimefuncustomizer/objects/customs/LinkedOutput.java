package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public class LinkedOutput {
    private final ItemStack[] freeOutput;
    private final Map<Integer, ItemStack> linkedOutput;
    private final int[] freeChances;
    private final Map<Integer, Integer> linkedChances;
    public LinkedOutput(ItemStack[] freeOutput, Map<Integer, ItemStack> linkedOutput, int[] freeChances, Map<Integer, Integer> linkedChances) {
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
}
