package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class CustomTemplateMachineRecipe extends CustomMachineRecipe{
    private final int cost;

    public CustomTemplateMachineRecipe(int seconds, ItemStack[] input, ItemStack[] output, List<Integer> chances, boolean chooseOneIfHas, boolean forDisplay, int cost) {
        super(seconds, input, output, chances, chooseOneIfHas, forDisplay);

        this.cost = cost;
    }
}
