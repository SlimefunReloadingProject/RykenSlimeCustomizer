package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.CustomItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;

public class CustomEnergyItem extends CustomItem implements Rechargeable {
    private final float capacity;

    public CustomEnergyItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, float capacity, @Nullable JavaScriptEval eval) {
        super(itemGroup, item, recipeType, recipe);

        this.capacity = capacity;

        if (eval != null) {
            eval.doInit();

            this.addItemHandler((ItemUseHandler) e -> {
                eval.evalFunction("onUse", e, this);
                e.cancel();
            });
        } else {
            this.addItemHandler((ItemUseHandler) PlayerRightClickEvent::cancel);
        }
    }

    @Override
    public float getMaxItemCharge(ItemStack itemStack) {
        return capacity;
    }
}
