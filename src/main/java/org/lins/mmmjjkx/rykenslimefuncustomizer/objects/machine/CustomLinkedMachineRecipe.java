package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.LinkedOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public class CustomLinkedMachineRecipe extends CustomMachineRecipe {
    private final Map<Integer, ItemStack> linkedInput;
    private final LinkedOutput linkedOutput;
    public CustomLinkedMachineRecipe(int seconds, Map<Integer, ItemStack> input, LinkedOutput linkedOutput, boolean chooseOneIfHas, boolean forDisplay, boolean hide) {
        super(seconds, input.values().toArray(new ItemStack[0]), linkedOutput.toArray(), linkedOutput.chancesToArray(), chooseOneIfHas, forDisplay, hide);
        this.linkedInput = input;
        this.linkedOutput = linkedOutput;
    }
}
