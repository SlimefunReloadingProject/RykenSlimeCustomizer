package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.WeaponUseHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.CustomItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ScriptEval;

public class CustomUnplaceableItem extends CustomItem implements NotPlaceable {
    private final Object[] constructorArgs;

    public CustomUnplaceableItem(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            @Nullable ScriptEval eval,
            ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);

        if (eval != null) {
            eval.doInit();

            this.addItemHandler((ItemUseHandler) e -> {
                eval.evalFunction("onUse", e);
                e.cancel();
            });

            this.addItemHandler((WeaponUseHandler) (e, p, it) -> eval.evalFunction("onWeaponHit", e, p, it));
            this.addItemHandler((ToolUseHandler) (e, it, i, drops) -> eval.evalFunction("onToolUse", e, it, i, drops));
        } else {
            this.addItemHandler((ItemUseHandler) PlayerRightClickEvent::cancel);
        }

        this.constructorArgs = new Object[] {itemGroup, item, recipeType, recipe, eval, recipeOutput};
    }

    @Override
    public Object[] constructorArgs() {
        return constructorArgs;
    }
}
