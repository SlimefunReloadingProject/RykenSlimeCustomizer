package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import java.util.List;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class MachineTemplate extends ItemStack {
    private final List<Pair<RecipeMachineRecipe, Integer>> recipes;

    public MachineTemplate(ItemStack item, int cost, List<Pair<RecipeMachineRecipe, Integer>> recipes) {
        super(item);

        this.recipes = recipes;
    }
}
