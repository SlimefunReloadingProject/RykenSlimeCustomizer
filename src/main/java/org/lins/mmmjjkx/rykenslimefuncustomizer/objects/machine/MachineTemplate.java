package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.inventory.ItemStack;

@Getter
public class MachineTemplate extends ItemStack {
    private final int cost;
    private final RecipeMachineRecipe recipe;

    public MachineTemplate(ItemStack item, int cost, RecipeMachineRecipe recipe) {
        super(item);

        this.cost = cost;
        this.recipe = recipe;
    }

    public boolean matchRecipe(BlockMenu bm, int[] inputSlots) {

    }
}
