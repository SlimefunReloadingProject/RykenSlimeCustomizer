package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.item;

import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import java.util.function.BiFunction;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

public class CustomGeoResource extends UnplaceableBlock implements GEOResource {
    private final BiFunction<World.Environment, Biome, Integer> supply;
    private final int maxDeviation;
    private final boolean obtainableFromGEOMiner;
    private final String name;

    public CustomGeoResource(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType type,
            ItemStack[] recipe,
            BiFunction<World.Environment, Biome, Integer> supply,
            int maxDeviation,
            boolean obtainableFromGEOMiner,
            String name) {
        super(itemGroup, item, type, recipe);

        this.supply = supply;
        this.maxDeviation = maxDeviation;
        this.obtainableFromGEOMiner = obtainableFromGEOMiner;
        this.name = name;

        register();
        register(RykenSlimefunCustomizer.INSTANCE);
    }

    @Override
    public int getDefaultSupply(@NotNull World.Environment environment, @NotNull Biome biome) {
        return supply.apply(environment, biome);
    }

    @Override
    public int getMaxDeviation() {
        return maxDeviation;
    }

    @NotNull @Override
    public String getName() {
        return name;
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
