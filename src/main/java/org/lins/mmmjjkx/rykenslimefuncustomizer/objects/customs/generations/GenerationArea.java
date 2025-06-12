package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.generations;

import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.World;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.Range;

@Getter
@Setter
public class GenerationArea {
    private Range height;
    private int most;
    private int amount;
    private Range size;
    private Material replacement;
    private World.Environment environment;

    public GenerationArea(
            @Nonnull Range height,
            int most,
            int amount,
            @Nonnull Range size,
            @Nonnull Material replacement,
            @Nonnull World.Environment environment) {
        this.height = height;
        this.most = most;
        this.amount = amount;
        if (this.amount > 200) {
            this.amount = 200;
        }

        this.size = size;
        this.replacement = replacement;
        this.environment = environment;
    }

    public GenerationArea(@Nonnull Range height, int most, int amount, @Nonnull Range size) {
        this(height, most, amount, size, Material.STONE, World.Environment.NORMAL);
    }
}
