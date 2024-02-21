package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecipeMachineRecipe extends MachineRecipe {
    @Getter
    private final List<Integer> chances;
    @Getter
    private final boolean chooseOneIfHas;

    private final Random RNG = new Random();

    public RecipeMachineRecipe(int seconds, ItemStack[] input, ItemStack[] output, List<Integer> chances, boolean chooseOneIfHas) {
        super(seconds, input, output);

        this.chances = chances;
        this.chooseOneIfHas = chooseOneIfHas;
    }

    public List<ItemStack> getMatchChanceResult() {
        List<ItemStack> itemStacks = new ArrayList<>();

        for (int i = 0; i < getOutput().length; i ++) {
            ItemStack output = getOutput()[i];
            int chance = chances.get(i);
            if (output != null && matchChance(chance)) {
                itemStacks.add(output);
            }
        }

        return itemStacks;
    }

    private boolean matchChance(Integer chance) {
        if (chance == null) return false;
        if (chance >= 100) return true;
        if (chance < 1) return false;

        int result = RNG.nextInt(100);
        return result < chance;
    }
}
