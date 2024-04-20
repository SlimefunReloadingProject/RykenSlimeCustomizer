package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class MachineTemplate extends ItemStack {
    private final int cost;
    private final List<RecipeMachineRecipe> recipes;

    public MachineTemplate(ItemStack item, int cost, List<RecipeMachineRecipe> recipes) {
        super(item);

        this.cost = cost;
        this.recipes = recipes;
    }

    public boolean matchRecipe(BlockMenu bm, int[] inputSlots) {
        for (RecipeMachineRecipe recipe : recipes) {

        }
        return false;
    }
}
