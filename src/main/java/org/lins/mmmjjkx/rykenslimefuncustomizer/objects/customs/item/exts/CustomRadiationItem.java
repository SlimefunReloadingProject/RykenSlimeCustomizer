package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.exts;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.BaseRadiationItem;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent.ScriptEval;

public class CustomRadiationItem extends BaseRadiationItem implements Radioactive {
    private final Radioactivity radioactivity;
    private final Object[] constructorArgs;

    public CustomRadiationItem(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ScriptEval eval,
            Radioactivity radioactivity) {
        super(itemGroup, radioactivity, item, recipeType, recipe);

        this.radioactivity = radioactivity;

        constructorArgs = new Object[] {itemGroup, item, recipeType, recipe, radioactivity};
    }

    @Override
    public Object[] constructArgs() {
        return constructorArgs;
    }

    @NotNull @Override
    public Radioactivity getRadioactivity() {
        return radioactivity;
    }
}
