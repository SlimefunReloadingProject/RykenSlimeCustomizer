package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ScriptEval;

public class CustomFood extends SimpleSlimefunItem<ItemConsumptionHandler> {
    private final ScriptEval eval;

    public CustomFood(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            @Nullable ScriptEval eval,
            ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);

        this.eval = eval;

        register(RykenSlimefunCustomizer.INSTANCE);
    }

    @NotNull @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, i) -> {
            if (eval != null) {
                eval.evalFunction("onEat", e, p, i);
            }
        };
    }
}
