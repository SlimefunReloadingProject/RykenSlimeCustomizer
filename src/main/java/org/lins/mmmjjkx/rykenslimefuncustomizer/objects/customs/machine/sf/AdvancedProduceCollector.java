package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.sf;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.AnimalProduce;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.ProduceCollector;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AdvancedProduceCollector extends ProduceCollector {
    private final int speed;

    public AdvancedProduceCollector(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int speed) {
        super(itemGroup, item, recipeType, recipe);

        this.speed = speed;
    }

    @Override
    public void addProduce(@NotNull AnimalProduce produce) {
        produce.setTicks(produce.getTicks() / speed);
        super.addProduce(produce);
    }
}
