package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomItem extends SlimefunItem {
    private static final Map<String, String> tempValue = new HashMap<>();

    public CustomItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, @Nullable JavaScriptEval eval) {
        super(itemGroup, item, recipeType, recipe);

        register(RykenSlimefunCustomizer.INSTANCE);

        if (eval != null) {
            this.addItemHandler((ItemUseHandler) e -> eval.evalFunction("onUse", e));
        }
    }
}
