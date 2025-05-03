package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item.exts;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.RainbowTickHandler;
import io.github.thebusybiscuit.slimefun4.utils.ColoredMaterial;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.parent.CustomItem;

public class CustomRainbowBlock extends CustomItem {
    private final Object[] constructorArgs;

    public CustomRainbowBlock(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            RainbowTickHandler ticker,
            ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);

        addItemHandler(ticker);

        constructorArgs = new Object[] {itemGroup, item, recipeType, recipe, ticker, recipeOutput};
    }

    public CustomRainbowBlock(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ColoredMaterial materialType,
            ItemStack recipeOutput) {
        this(itemGroup, item, recipeType, recipe, new RainbowTickHandler(materialType), recipeOutput);
    }

    public CustomRainbowBlock(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            List<Material> materials,
            ItemStack recipeOutput) {
        this(itemGroup, item, recipeType, recipe, new RainbowTickHandler(materials), recipeOutput);
    }

    @Override
    public Object[] constructorArgs() {
        return constructorArgs;
    }
}
