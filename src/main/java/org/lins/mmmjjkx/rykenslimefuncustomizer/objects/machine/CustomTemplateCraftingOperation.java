package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import lombok.Getter;

public class CustomTemplateCraftingOperation implements MachineOperation {
    @Getter
    private final CustomMachineRecipe recipe;

    @Getter
    private final MachineTemplate template;

    private final int ticks;
    private int currentTicks;

    public CustomTemplateCraftingOperation(MachineTemplate template, CustomMachineRecipe recipe, int totalTicks) {
        this.currentTicks = 0;
        Validate.isTrue(recipe.getOutput().length != 0, "The recipe must have at least one output.");
        Validate.isTrue(
                totalTicks >= 0,
                "The amount of total ticks must be a positive integer or zero, received: " + totalTicks);
        this.recipe = recipe;
        this.ticks = totalTicks;
        this.template = template;
    }

    public void addProgress(int num) {
        Validate.isTrue(num > 0, "Progress must be positive.");
        this.currentTicks += num;
    }

    public int getProgress() {
        return this.currentTicks;
    }

    public int getTotalTicks() {
        return ticks;
    }
}
