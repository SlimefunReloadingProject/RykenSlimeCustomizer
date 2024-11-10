package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class CustomLinkedMachineRecipe extends CustomMachineRecipe {
    @Getter
    private final Map<Integer, ItemStack> input;
    public CustomLinkedMachineRecipe(int seconds, Map<Integer, ItemStack> input, ItemStack[] output, List<Integer> chances, boolean chooseOneIfHas, boolean forDisplay, boolean hide) {
        super(seconds, input.values().toArray(new ItemStack[0]), output, chances, chooseOneIfHas, forDisplay, hide);
        this.input = input;
    }
}
