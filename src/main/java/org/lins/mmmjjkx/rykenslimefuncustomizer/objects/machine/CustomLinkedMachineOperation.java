package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;
import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import javax.annotation.Nonnull;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class CustomLinkedMachineOperation extends CraftingOperation {
    private final CustomLinkedMachineRecipe recipe;

    public CustomLinkedMachineOperation(@Nonnull CustomLinkedMachineRecipe recipe) {
        super(recipe.getInput(), recipe.getOutput(), recipe.getTicks());
        Validate.isTrue(
                recipe.getTicks() >= 0,
                "The amount of total ticks must be a positive integer or zero, received: " + recipe.getTicks());
        this.recipe = recipe;
    }

    public int getTotalTicks() {
        return recipe.getTicks();
    }
}
