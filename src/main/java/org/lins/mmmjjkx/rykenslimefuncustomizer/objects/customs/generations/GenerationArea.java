package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.generations;

import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.World;

@Getter
@Setter
public class GenerationArea {
    private int maxHeight;
    private int mixHeight;
    private int most;
    private int amount;
    private double chance;
    private double mostChance;
    private Material replacement;
    private World.Environment environment;

    public GenerationArea(
            int maxHeight,
            int mixHeight,
            int most,
            int amount,
            double chance,
            double mostChance,
            @Nonnull Material replacement,
            @Nonnull World.Environment environment) {
        this.maxHeight = maxHeight;
        this.mixHeight = mixHeight;
        this.most = most;
        this.amount = amount;
        this.chance = chance;
        this.mostChance = mostChance;
        this.replacement = replacement;
        this.environment = environment;
    }

    public GenerationArea(int maxHeight, int mixHeight, int most, int amount, double chance) {
        this(maxHeight, mixHeight, most, amount, chance, 0.6d, Material.STONE, World.Environment.NORMAL);
    }
}
