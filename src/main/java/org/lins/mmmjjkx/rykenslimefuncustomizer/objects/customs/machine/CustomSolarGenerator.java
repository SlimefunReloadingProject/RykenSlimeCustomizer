package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.SolarGenerator;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;

public class CustomSolarGenerator extends SolarGenerator {
    private final int lightLevel;

    public CustomSolarGenerator(
            ItemGroup itemGroup,
            int dayEnergy,
            int nightEnergy,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            int capacity,
            int lightLevel) {
        super(itemGroup, dayEnergy, nightEnergy, item, recipeType, recipe, capacity);

        if (lightLevel > 15 || lightLevel < 0) {
            lightLevel = 15;
        }

        this.lightLevel = lightLevel;

        register(RykenSlimefunCustomizer.INSTANCE);
    }

    public int getGeneratedOutput(Location l, Config data) {
        World world = l.getWorld();

        if (world.getEnvironment() != World.Environment.NORMAL) {
            return 0;
        } else {
            boolean isDaytime = isDaytime(world);

            if (!isDaytime && getNightEnergy() < 1) {
                return 0;
            } else if (!world.isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)
                    || l.getBlock().getRelative(0, 1, 0).getLightFromSky() < (byte) lightLevel) {
                return 0;
            } else {
                return isDaytime ? getDayEnergy() : getNightEnergy();
            }
        }
    }

    private boolean isDaytime(World world) {
        long time = world.getTime();
        return !world.hasStorm() && !world.isThundering() && (time < 12300L || time > 23850L);
    }
}
