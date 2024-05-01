package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.RecipeMachineRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class CustomMBRecipeMachine extends CustomRecipeMachine {
    public CustomMBRecipeMachine(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, List<Integer> input, List<Integer> output,
                                 RecipeType multiBlockRecipeType, int energyPerCraft, int capacity, @Nullable CustomMenu menu, int speed,
                                 @Nullable List<RecipeMachineRecipe> extraRecipes) {
        super(itemGroup, item, recipeType, recipe, input, output,
                ((Supplier<List<RecipeMachineRecipe>>) () -> {
                    var sf = multiBlockRecipeType.getMachine();
                    if (sf instanceof MultiBlockMachine mbm) {
                        List<ItemStack[]> recipes = mbm.getRecipes();
                        List<RecipeMachineRecipe> machineRecipes = new ArrayList<>();
                        ItemStack[] input1 = null;
                        ItemStack[] output1 = null;
                        for (int i = 0; i < recipes.size(); i++) {
                            if (i % 2 == 0) {
                                input1 = recipes.get(i);
                            } else {
                                output1 = recipes.get(i);
                            }

                            if (input1 != null && output1 != null) {
                                machineRecipes.add(new RecipeMachineRecipe(0, input1, output1, List.of(100), false, false));
                            }
                        }
                        return machineRecipes;
                    }
                    return List.of();
                }).get(),
                energyPerCraft, capacity, menu, speed);

        if (extraRecipes != null) {
            extraRecipes.forEach(this::registerRecipe);
        }
    }

    public CustomMBRecipeMachine(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, List<Integer> input, List<Integer> output,
                                 MultiBlockMachine multiBlockMachine, int energyPerCraft, int capacity, @Nullable CustomMenu menu, int speed,
                                 @Nullable List<RecipeMachineRecipe> extraRecipes) {
        super(itemGroup, item, recipeType, recipe, input, output,
                ((Supplier<List<RecipeMachineRecipe>>) () -> {
                    List<ItemStack[]> recipes = multiBlockMachine.getRecipes();
                    List<RecipeMachineRecipe> machineRecipes = new ArrayList<>();
                    ItemStack[] input1 = null;
                    ItemStack[] output1 = null;
                    for (int i = 0; i < recipes.size(); i++) {
                        if (i % 2 == 0) {
                            input1 = recipes.get(i);
                        } else {
                            output1 = recipes.get(i);
                        }

                        if (input1 != null && output1 != null) {
                            machineRecipes.add(new RecipeMachineRecipe(0, input1, output1, List.of(100), false, false));
                        }
                    }
                    return machineRecipes;
                }).get(),
                energyPerCraft, capacity, menu, speed);

        if (extraRecipes != null) {
            extraRecipes.forEach(this::registerRecipe);
        }
    }
}
