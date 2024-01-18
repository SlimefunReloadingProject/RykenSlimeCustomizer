package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

import java.util.function.BiFunction;

public class CustomGeoResource extends SlimefunItem implements GEOResource {
    private final BiFunction<World.Environment, Biome, Integer> supply;
    private final int maxDeviation;
    private final boolean obtainableFromGEOMiner;

    public CustomGeoResource(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                             BiFunction<World.Environment, Biome, Integer> supply, int maxDeviation, boolean obtainableFromGEOMiner) {
        super(itemGroup, item, recipeType, recipe);

        this.supply = supply;
        this.maxDeviation = maxDeviation;
        this.obtainableFromGEOMiner = obtainableFromGEOMiner;

        register(RykenSlimefunCustomizer.INSTANCE);
        register();
    }

    @Override
    public int getDefaultSupply(@NotNull World.Environment environment, @NotNull Biome biome) {
        return supply.apply(environment, biome);
    }

    @Override
    public int getMaxDeviation() {
        return maxDeviation;
    }

    @NotNull
    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isObtainableFromGEOMiner() {
        return obtainableFromGEOMiner;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, getId());
    }
}
