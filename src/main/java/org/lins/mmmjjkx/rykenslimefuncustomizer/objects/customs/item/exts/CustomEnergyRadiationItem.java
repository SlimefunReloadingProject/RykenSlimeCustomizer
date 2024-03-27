package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.exts;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent.ScriptEval;

public class CustomEnergyRadiationItem extends CustomEnergyItem implements Radioactive, NotPlaceable {
    private final Radioactivity radioactivity;

    public CustomEnergyRadiationItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, Radioactivity radioactivity, float capacity, ScriptEval scriptEval) {
        super(itemGroup, item, recipeType, recipe, capacity, scriptEval);
        this.radioactivity = radioactivity;
    }

    @NotNull
    @Override
    public Radioactivity getRadioactivity() {
        return radioactivity;
    }
}
