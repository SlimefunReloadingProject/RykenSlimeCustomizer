package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent.ScriptEval;

public class CustomRadiationItem extends CustomUnplaceableItem implements Radioactive {
    private final Radioactivity radioactivity;

    public CustomRadiationItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, Radioactivity radioactivity, @Nullable ScriptEval eval) {
        super(itemGroup, item, recipeType, recipe, eval);

        this.radioactivity = radioactivity;

        register(RykenSlimefunCustomizer.INSTANCE);
    }

    @NotNull
    @Override
    public Radioactivity getRadioactivity() {
        return radioactivity;
    }
}
